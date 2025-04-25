package uk.gov.govuk.login.data

import android.content.Intent
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators
import androidx.biometric.BiometricManager.BIOMETRIC_SUCCESS
import androidx.fragment.app.FragmentActivity
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import kotlinx.coroutines.test.runTest
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationResponse
import net.openid.appauth.AuthorizationService
import net.openid.appauth.AuthorizationService.TokenResponseCallback
import net.openid.appauth.TokenResponse
import org.junit.After
import org.junit.Before
import org.junit.Test
import uk.gov.android.securestore.RetrievalEvent
import uk.gov.android.securestore.SecureStore
import uk.gov.android.securestore.error.SecureStoreErrorType
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class LoginRepoTest {

    private val authIntent = mockk<Intent>(relaxed = true)
    private val authService = mockk<AuthorizationService>(relaxed = true)
    private val tokenResponseMapper = mockk<TokenResponseMapper>(relaxed = true)
    private val secureStore = mockk<SecureStore>(relaxed = true)
    private val biometricManager = mockk<BiometricManager>(relaxed = true)
    private val authResponse = mockk<AuthorizationResponse>(relaxed = true)
    private val authException = mockk<AuthorizationException>(relaxed = true)
    private val tokenResponse = mockk<TokenResponse>(relaxed = true)
    private val activity = mockk<FragmentActivity>(relaxed = true)

    private lateinit var loginRepo: LoginRepo

    @Before
    fun setup() {
        mockkStatic(AuthorizationResponse::class)

        loginRepo = LoginRepo(authIntent, authService, tokenResponseMapper, secureStore, biometricManager)
    }

    @After
    fun tearDown() {
        // Remove mocks to prevent side effects
        unmockkAll()
    }

    @Test
    fun `Given null data, when handle auth response, return false`() {
        runTest {
            assertFalse(loginRepo.handleAuthResponse(null))
        }
    }

    @Test
    fun `Given a null auth response, when handle auth response, return false`() {
        every { AuthorizationResponse.fromIntent(any()) } returns null

        runTest {
            assertFalse(loginRepo.handleAuthResponse(authIntent))
        }
    }

    @Test
    fun `Given the token request returns an exception, when handle auth response, return false`() {
        every { AuthorizationResponse.fromIntent(any()) } returns authResponse
        every { authService.performTokenRequest(any(), any()) } answers {
            val callback = secondArg<TokenResponseCallback>()
            callback.onTokenRequestCompleted(null, authException)
        }

        runTest {
            assertFalse(loginRepo.handleAuthResponse(authIntent))
        }
    }

    @Test
    fun `Given the token request returns a null access token, when handle auth response, return false`() {
        every { AuthorizationResponse.fromIntent(any()) } returns authResponse
        every { authService.performTokenRequest(any(), any()) } answers {
            val callback = secondArg<TokenResponseCallback>()
            callback.onTokenRequestCompleted(tokenResponse, null)
        }
        every { tokenResponseMapper.map(any()) } returns
                TokenResponseMapper.Tokens(
                    accessToken = null,
                    idToken = "idToken",
                    refreshToken = "refreshToken"
                )

        runTest {
            assertFalse(loginRepo.handleAuthResponse(authIntent))
        }
    }

    @Test
    fun `Given the token request returns a null id token, when handle auth response, return false`() {
        every { AuthorizationResponse.fromIntent(any()) } returns authResponse
        every { authService.performTokenRequest(any(), any()) } answers {
            val callback = secondArg<TokenResponseCallback>()
            callback.onTokenRequestCompleted(tokenResponse, null)
        }
        every { tokenResponseMapper.map(any()) } returns
                TokenResponseMapper.Tokens(
                    accessToken = "accessToken",
                    idToken = null,
                    refreshToken = "refreshToken"
                )

        runTest {
            assertFalse(loginRepo.handleAuthResponse(authIntent))
        }
    }

    @Test
    fun `Given the token request returns a null refresh token, when handle auth response, return false`() {
        every { AuthorizationResponse.fromIntent(any()) } returns authResponse
        every { authService.performTokenRequest(any(), any()) } answers {
            val callback = secondArg<TokenResponseCallback>()
            callback.onTokenRequestCompleted(tokenResponse, null)
        }
        every { tokenResponseMapper.map(any()) } returns
                TokenResponseMapper.Tokens(
                    accessToken = "accessToken",
                    idToken = "idToken",
                    refreshToken = null
                )

        runTest {
            assertFalse(loginRepo.handleAuthResponse(authIntent))
        }
    }

    @Test
    fun `Given the token request is successful, when handle auth response, return true`() {
        every { AuthorizationResponse.fromIntent(any()) } returns authResponse
        every { authService.performTokenRequest(any(), any()) } answers {
            val callback = secondArg<TokenResponseCallback>()
            callback.onTokenRequestCompleted(tokenResponse, null)
        }
        every { tokenResponseMapper.map(any()) } returns
                TokenResponseMapper.Tokens(
                    accessToken = "accessToken",
                    idToken = "idToken",
                    refreshToken = "refreshToken"
                )

        runTest {
            assertTrue(loginRepo.handleAuthResponse(authIntent))
        }
    }

    @Test
    fun `Given a successful retrieval, when persist refresh token, return true`() {
        every { AuthorizationResponse.fromIntent(any()) } returns authResponse
        every { authService.performTokenRequest(any(), any()) } answers {
            val callback = secondArg<TokenResponseCallback>()
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
            loginRepo.handleAuthResponse(authIntent)
            assertTrue(
                loginRepo.persistRefreshToken(activity, "", "", "")
            )

            coVerify { secureStore.upsert("refreshToken", "refreshToken") }
        }
    }

    @Test
    fun `Given a failed retrieval, when persist refresh token, return false and delete token`() {
        every { AuthorizationResponse.fromIntent(any()) } returns authResponse
        every { authService.performTokenRequest(any(), any()) } answers {
            val callback = secondArg<TokenResponseCallback>()
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
            loginRepo.handleAuthResponse(authIntent)
            assertFalse(
                loginRepo.persistRefreshToken(activity, "", "", "")
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

        assertTrue(loginRepo.isAuthenticationEnabled())
    }

    @Test
    fun `Given non biometric success, when is authentication enabled, return false`() {
        every {
            biometricManager.canAuthenticate(
                Authenticators.BIOMETRIC_STRONG or Authenticators.DEVICE_CREDENTIAL
            )
        } returns BiometricManager.BIOMETRIC_STATUS_UNKNOWN

        assertFalse(loginRepo.isAuthenticationEnabled())

        every {
            biometricManager.canAuthenticate(
                Authenticators.BIOMETRIC_STRONG or Authenticators.DEVICE_CREDENTIAL
            )
        } returns BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED

        assertFalse(loginRepo.isAuthenticationEnabled())

        every {
            biometricManager.canAuthenticate(
                Authenticators.BIOMETRIC_STRONG or Authenticators.DEVICE_CREDENTIAL
            )
        } returns BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE

        assertFalse(loginRepo.isAuthenticationEnabled())

        every {
            biometricManager.canAuthenticate(
                Authenticators.BIOMETRIC_STRONG or Authenticators.DEVICE_CREDENTIAL
            )
        } returns BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED

        assertFalse(loginRepo.isAuthenticationEnabled())

        every {
            biometricManager.canAuthenticate(
                Authenticators.BIOMETRIC_STRONG or Authenticators.DEVICE_CREDENTIAL
            )
        } returns BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE

        assertFalse(loginRepo.isAuthenticationEnabled())

        every {
            biometricManager.canAuthenticate(
                Authenticators.BIOMETRIC_STRONG or Authenticators.DEVICE_CREDENTIAL
            )
        } returns BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED

        assertFalse(loginRepo.isAuthenticationEnabled())
    }
}
