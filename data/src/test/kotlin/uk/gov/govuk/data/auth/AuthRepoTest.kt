package uk.gov.govuk.data.auth

import android.content.Intent
import android.content.SharedPreferences
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators
import androidx.biometric.BiometricManager.BIOMETRIC_SUCCESS
import androidx.fragment.app.FragmentActivity
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationResponse
import net.openid.appauth.AuthorizationService
import net.openid.appauth.AuthorizationService.TokenResponseCallback
import net.openid.appauth.ClientAuthentication
import net.openid.appauth.TokenRequest
import net.openid.appauth.TokenResponse
import okio.IOException
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import uk.gov.android.securestore.RetrievalEvent
import uk.gov.android.securestore.SecureStore
import uk.gov.android.securestore.error.SecureStorageError
import uk.gov.android.securestore.error.SecureStoreErrorType
import uk.gov.govuk.data.BuildConfig
import uk.gov.govuk.data.remote.AuthApi

class AuthRepoTest {

    private val attestationProvider = mockk<AttestationProvider>(relaxed = true)
    private val authRequest = mockk<AuthorizationRequest>(relaxed = true)
    private val authService = mockk<AuthorizationService>(relaxed = true)
    private val tokenRequestBuilder = mockk<TokenRequest.Builder>(relaxed = true)
    private val tokenResponseMapper = mockk<TokenResponseMapper>(relaxed = true)
    private val secureStore = mockk<SecureStore>(relaxed = true)
    private val biometricManager = mockk<BiometricManager>(relaxed = true)
    private val intent = mockk<Intent>(relaxed = true)
    private val authResponse = mockk<AuthorizationResponse>(relaxed = true)
    private val authException = mockk<AuthorizationException>(relaxed = true)
    private val tokenResponse = mockk<TokenResponse>(relaxed = true)
    private val sharedPrefs = mockk<SharedPreferences>(relaxed = true)
    private val authApi = mockk<AuthApi>(relaxed = true)
    private val activity = mockk<FragmentActivity>(relaxed = true)

    private lateinit var authRepo: AuthRepo

    @Before
    fun setup() {
        mockkStatic(AuthorizationResponse::class)

        authRepo = AuthRepo(attestationProvider, authRequest, authService, tokenRequestBuilder,
            tokenResponseMapper, secureStore, biometricManager, sharedPrefs, authApi)
    }

    @After
    fun tearDown() {
        // Remove mocks to prevent side effects
        unmockkAll()
    }

    @Test
    fun `Given null data, when handle auth response, return false`() {
        runTest {
            assertFalse(authRepo.handleAuthResponse(null))
        }
    }

    @Test
    fun `Given a null auth response, when handle auth response, return false`() {
        every { AuthorizationResponse.fromIntent(any()) } returns null

        runTest {
            assertFalse(authRepo.handleAuthResponse(intent))
        }
    }

    @Test
    fun `Given attestation provider returns a null token then do not add header`() {
        every { AuthorizationResponse.fromIntent(any()) } returns authResponse
        coEvery { attestationProvider.getToken() } returns null

        val slot = slot<ClientAuthentication>()
        every {
            authService.performTokenRequest(
                any(),
                capture(slot),
                any()
            )
        } answers {
            val callback = thirdArg<TokenResponseCallback>()
            callback.onTokenRequestCompleted(tokenResponse, null)
        }

        runTest {
            authRepo.handleAuthResponse(intent)

            assertFalse(slot.captured.getRequestHeaders("").containsKey("X-Attestation-Token"))
            assertEquals(BuildConfig.AUTH_CLIENT_ID, slot.captured.getRequestParameters("")["client_id"])
        }
    }

    @Test
    fun `Given attestation provider returns a token then add header`() {
        every { AuthorizationResponse.fromIntent(any()) } returns authResponse
        coEvery { attestationProvider.getToken() } returns "attestation_token"

        val slot = slot<ClientAuthentication>()
        every {
            authService.performTokenRequest(
                any(),
                capture(slot),
                any()
            )
        } answers {
            val callback = thirdArg<TokenResponseCallback>()
            callback.onTokenRequestCompleted(tokenResponse, null)
        }

        runTest {
            authRepo.handleAuthResponse(intent)

            assertEquals("attestation_token", slot.captured.getRequestHeaders("")["X-Attestation-Token"])
            assertEquals(BuildConfig.AUTH_CLIENT_ID, slot.captured.getRequestParameters("")["client_id"])
        }
    }

    @Test
    fun `Given the token request returns an exception, when handle auth response, return false`() {
        every { AuthorizationResponse.fromIntent(any()) } returns authResponse
        every { authService.performTokenRequest(any(), any(), any()) } answers {
            val callback = thirdArg<TokenResponseCallback>()
            callback.onTokenRequestCompleted(null, authException)
        }

        runTest {
            assertFalse(authRepo.handleAuthResponse(intent))
        }
    }

    @Test
    fun `Given the token request returns a null access token, when handle auth response, return false`() {
        every { AuthorizationResponse.fromIntent(any()) } returns authResponse
        every { authService.performTokenRequest(any(), any(), any()) } answers {
            val callback = thirdArg<TokenResponseCallback>()
            callback.onTokenRequestCompleted(tokenResponse, null)
        }
        every { tokenResponseMapper.map(any()) } returns
                TokenResponseMapper.Tokens(
                    accessToken = null,
                    idToken = "idToken",
                    refreshToken = "refreshToken"
                )

        runTest {
            assertFalse(authRepo.handleAuthResponse(intent))
        }
    }

    @Test
    fun `Given the token request returns a null id token, when handle auth response, return false`() {
        every { AuthorizationResponse.fromIntent(any()) } returns authResponse
        every { authService.performTokenRequest(any(), any(), any()) } answers {
            val callback = thirdArg<TokenResponseCallback>()
            callback.onTokenRequestCompleted(tokenResponse, null)
        }
        every { tokenResponseMapper.map(any()) } returns
                TokenResponseMapper.Tokens(
                    accessToken = "accessToken",
                    idToken = null,
                    refreshToken = "refreshToken"
                )

        runTest {
            assertFalse(authRepo.handleAuthResponse(intent))
        }
    }

    @Test
    fun `Given the token request returns a null refresh token, when handle auth response, return false`() {
        every { AuthorizationResponse.fromIntent(any()) } returns authResponse
        every { authService.performTokenRequest(any(), any(), any()) } answers {
            val callback = thirdArg<TokenResponseCallback>()
            callback.onTokenRequestCompleted(tokenResponse, null)
        }
        every { tokenResponseMapper.map(any()) } returns
                TokenResponseMapper.Tokens(
                    accessToken = "accessToken",
                    idToken = "idToken",
                    refreshToken = null
                )

        runTest {
            assertFalse(authRepo.handleAuthResponse(intent))
        }
    }

    @Test
    fun `Given the token request is successful, when handle auth response, return true`() {
        every { AuthorizationResponse.fromIntent(any()) } returns authResponse
        every { authService.performTokenRequest(any(), any(), any()) } answers {
            val callback = thirdArg<TokenResponseCallback>()
            callback.onTokenRequestCompleted(tokenResponse, null)
        }
        every { tokenResponseMapper.map(any()) } returns
                TokenResponseMapper.Tokens(
                    accessToken = "accessToken",
                    idToken = "idToken",
                    refreshToken = "refreshToken"
                )

        runTest {
            assertTrue(authRepo.handleAuthResponse(intent))
        }
    }

    @Test
    fun `Given a successful retrieval, when persist refresh token, return true`() {
        every { AuthorizationResponse.fromIntent(any()) } returns authResponse
        every { authService.performTokenRequest(any(), any(), any()) } answers {
            val callback = thirdArg<TokenResponseCallback>()
            callback.onTokenRequestCompleted(tokenResponse, null)
        }
        every { tokenResponseMapper.map(any()) } returns
                TokenResponseMapper.Tokens(
                    accessToken = "accessToken",
                    idToken = "idToken",
                    refreshToken = "refreshToken"
                )
        coEvery {
            secureStore.retrieveWithAuthentication(
                key = arrayOf("refreshToken"),
                any(),
                any()
            )
        } returns RetrievalEvent.Success(emptyMap())

        runTest {
            authRepo.handleAuthResponse(intent)
            assertTrue(
                authRepo.persistRefreshToken(activity, "")
            )

            coVerify { secureStore.upsert("refreshToken", "refreshToken") }
        }
    }

    @Test
    fun `Given a failed retrieval, when persist refresh token, return false and delete token`() {
        every { AuthorizationResponse.fromIntent(any()) } returns authResponse
        every { authService.performTokenRequest(any(), any(), any()) } answers {
            val callback = thirdArg<TokenResponseCallback>()
            callback.onTokenRequestCompleted(tokenResponse, null)
        }
        every { tokenResponseMapper.map(any()) } returns
                TokenResponseMapper.Tokens(
                    accessToken = "accessToken",
                    idToken = "idToken",
                    refreshToken = "refreshToken"
                )
        coEvery {
            secureStore.retrieveWithAuthentication(
                key = arrayOf("refreshToken"),
                any(),
                any()
            )
        } returns RetrievalEvent.Failed(SecureStoreErrorType.GENERAL)

        runTest {
            authRepo.handleAuthResponse(intent)
            assertFalse(
                authRepo.persistRefreshToken(activity, "")
            )

            coVerify { secureStore.upsert("refreshToken", "refreshToken") }
            coVerify { secureStore.delete("refreshToken") }
        }
    }

    @Test
    fun `Given biometric success, when is authentication enabled, return true`() {
        every {
            biometricManager.canAuthenticate(
                Authenticators.BIOMETRIC_STRONG or Authenticators.DEVICE_CREDENTIAL
            )
        } returns  BIOMETRIC_SUCCESS

        assertTrue(authRepo.isAuthenticationEnabled())
    }

    @Test
    fun `Given non biometric success, when is authentication enabled, return false`() {
        every {
            biometricManager.canAuthenticate(
                Authenticators.BIOMETRIC_STRONG or Authenticators.DEVICE_CREDENTIAL
            )
        } returns BiometricManager.BIOMETRIC_STATUS_UNKNOWN

        assertFalse(authRepo.isAuthenticationEnabled())

        every {
            biometricManager.canAuthenticate(
                Authenticators.BIOMETRIC_STRONG or Authenticators.DEVICE_CREDENTIAL
            )
        } returns BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED

        assertFalse(authRepo.isAuthenticationEnabled())

        every {
            biometricManager.canAuthenticate(
                Authenticators.BIOMETRIC_STRONG or Authenticators.DEVICE_CREDENTIAL
            )
        } returns BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE

        assertFalse(authRepo.isAuthenticationEnabled())

        every {
            biometricManager.canAuthenticate(
                Authenticators.BIOMETRIC_STRONG or Authenticators.DEVICE_CREDENTIAL
            )
        } returns BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED

        assertFalse(authRepo.isAuthenticationEnabled())

        every {
            biometricManager.canAuthenticate(
                Authenticators.BIOMETRIC_STRONG or Authenticators.DEVICE_CREDENTIAL
            )
        } returns BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE

        assertFalse(authRepo.isAuthenticationEnabled())

        every {
            biometricManager.canAuthenticate(
                Authenticators.BIOMETRIC_STRONG or Authenticators.DEVICE_CREDENTIAL
            )
        } returns BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED

        assertFalse(authRepo.isAuthenticationEnabled())
    }

    @Test
    fun `Given the refresh token retrieval is unsuccessful, when refresh tokens, then return false`() {
        coEvery {
            secureStore.retrieveWithAuthentication(
                key = arrayOf("refreshToken"),
                authPromptConfig = any(),
                context = any()
            )
        } returns RetrievalEvent.Failed(SecureStoreErrorType.GENERAL)

        runTest {
            assertFalse(authRepo.refreshTokens(activity, ""))
        }
    }

    @Test
    fun `Given the token request returns an exception, when refresh tokens, then return false`() {
        coEvery {
            secureStore.retrieveWithAuthentication(
                key = arrayOf("refreshToken"),
                authPromptConfig = any(),
                context = any()
            )
        } returns RetrievalEvent.Success(mapOf("refreshToken" to "token"))

        every { authService.performTokenRequest(any(), any(), any()) } answers {
            val callback = thirdArg<TokenResponseCallback>()
            callback.onTokenRequestCompleted(null, authException)
        }

        runTest {
            assertFalse(authRepo.refreshTokens(activity, ""))
        }
    }

    @Test
    fun `Given the token request returns a null access token, when refresh tokens, then return false`() {
        coEvery {
            secureStore.retrieveWithAuthentication(
                key = arrayOf("refreshToken"),
                authPromptConfig = any(),
                context = any()
            )
        } returns RetrievalEvent.Success(mapOf("refreshToken" to "token"))

        every { authService.performTokenRequest(any(), any(), any()) } answers {
            val callback = thirdArg<TokenResponseCallback>()
            callback.onTokenRequestCompleted(tokenResponse, null)
        }

        every { tokenResponseMapper.map(any()) } returns
                TokenResponseMapper.Tokens(
                    accessToken = null,
                    idToken = "idToken",
                    refreshToken = "refreshToken"
                )

        runTest {
            assertFalse(authRepo.refreshTokens(activity, ""))
        }
    }

    @Test
    fun `Given the token request returns a null id token, when refresh tokens, then return false`() {
        coEvery {
            secureStore.retrieveWithAuthentication(
                key = arrayOf("refreshToken"),
                authPromptConfig = any(),
                context = any()
            )
        } returns RetrievalEvent.Success(mapOf("refreshToken" to "token"))

        every { authService.performTokenRequest(any(), any(), any()) } answers {
            val callback = thirdArg<TokenResponseCallback>()
            callback.onTokenRequestCompleted(tokenResponse, null)
        }

        every { tokenResponseMapper.map(any()) } returns
                TokenResponseMapper.Tokens(
                    accessToken = "accessToken",
                    idToken = null,
                    refreshToken = "refreshToken"
                )

        runTest {
            assertFalse(authRepo.refreshTokens(activity, ""))
        }
    }

    @Test
    fun `Given the token request is successful, when refresh tokens, then return true`() {
        coEvery {
            secureStore.retrieveWithAuthentication(
                key = arrayOf("refreshToken"),
                authPromptConfig = any(),
                context = any()
            )
        } returns RetrievalEvent.Success(mapOf("refreshToken" to "token"))

        every { authService.performTokenRequest(any(), any(), any()) } answers {
            val callback = thirdArg<TokenResponseCallback>()
            callback.onTokenRequestCompleted(tokenResponse, null)
        }

        every { tokenResponseMapper.map(any()) } returns
                TokenResponseMapper.Tokens(
                    accessToken = "accessToken",
                    idToken = "idToken",
                    refreshToken = null
                )

        runTest {
            assertTrue(authRepo.refreshTokens(activity, ""))
        }
    }

    @Test
    fun `Given the user signs out, when sign out, delete token from secure store, revoke token and return true`() {
        runTest {
            assertTrue(authRepo.signOut())
        }

        coVerify {
            secureStore.delete("refreshToken")
            authApi.revoke(any(), any())
        }
    }

    @Test
    fun `Given the user signs out and the auth api throws an exception, when sign out, return true`() {
        coEvery {
            authApi.revoke(any(), any())
        } throws IOException()

        runTest {
            assertTrue(authRepo.signOut())
        }

        verify {
            secureStore.delete("refreshToken")
        }
    }

    @Test
    fun `Given the user signs out, when a SecureStorageError is thrown, do not revoke token and return false`() {
        every {
            secureStore.delete("refreshToken")
        } throws SecureStorageError(
            type = SecureStoreErrorType.GENERAL,
            exception = Exception()
        )

        runTest {
            assertFalse(authRepo.signOut())
        }

        coVerify(exactly = 0) {
            authApi.revoke(any(), any())
        }
    }

    @Test
    fun `Given a user session is not active, when is user session active, return false`() {
        assertFalse(authRepo.isUserSessionActive())
    }

    @Test
    fun `Given a user session is active, when is user session active, return true`() {
        every { AuthorizationResponse.fromIntent(any()) } returns authResponse
        every { authService.performTokenRequest(any(), any(), any()) } answers {
            val callback = thirdArg<TokenResponseCallback>()
            callback.onTokenRequestCompleted(tokenResponse, null)
        }
        every { tokenResponseMapper.map(any()) } returns
                TokenResponseMapper.Tokens(
                    accessToken = "accessToken",
                    idToken = "idToken",
                    refreshToken = "refreshToken"
                )

        runTest {
            authRepo.handleAuthResponse(intent)
            assertTrue(authRepo.isUserSessionActive())
        }
    }


    @Test
    fun `Given a user session is ended, when end user session, then user session is no longer active`() {
        every { AuthorizationResponse.fromIntent(any()) } returns authResponse
        every { authService.performTokenRequest(any(), any(), any()) } answers {
            val callback = thirdArg<TokenResponseCallback>()
            callback.onTokenRequestCompleted(tokenResponse, null)
        }
        every { tokenResponseMapper.map(any()) } returns
                TokenResponseMapper.Tokens(
                    accessToken = "accessToken",
                    idToken = "idToken",
                    refreshToken = "refreshToken"
                )

        runTest {
            authRepo.handleAuthResponse(intent)
            assertTrue(authRepo.isUserSessionActive())
            authRepo.endUserSession()
            assertFalse(authRepo.isUserSessionActive())
        }
    }
}
