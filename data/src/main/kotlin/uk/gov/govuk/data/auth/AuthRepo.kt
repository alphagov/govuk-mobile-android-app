package uk.gov.govuk.data.auth

import android.content.Intent
import android.content.SharedPreferences
import android.util.Base64
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators
import androidx.core.content.edit
import androidx.fragment.app.FragmentActivity
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationResponse
import net.openid.appauth.AuthorizationService
import net.openid.appauth.TokenRequest
import net.openid.appauth.TokenResponse
import org.json.JSONObject
import uk.gov.android.securestore.RetrievalEvent
import uk.gov.android.securestore.SecureStore
import uk.gov.android.securestore.authentication.AuthenticatorPromptConfiguration
import uk.gov.android.securestore.error.SecureStorageError
import uk.gov.govuk.data.BuildConfig
import uk.gov.govuk.data.auth.model.Tokens
import uk.gov.govuk.data.remote.AuthApi
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@Singleton
class AuthRepo @Inject constructor(
    private val authRequest: AuthorizationRequest,
    private val authService: AuthorizationService,
    private val tokenRequestBuilder: TokenRequest.Builder,
    private val tokenResponseMapper: TokenResponseMapper,
    private val secureStore: SecureStore,
    private val biometricManager: BiometricManager,
    private val sharedPreferences: SharedPreferences,
    private val authApi: AuthApi
) {
    companion object {
        private const val REFRESH_TOKEN_KEY = "refreshToken"
        private const val SUB_ID_KEY = "subId"
    }

    val authIntent: Intent by lazy {
        authService.getAuthorizationRequestIntent(authRequest)
    }

    private var tokens = Tokens()

    suspend fun handleAuthResponse(data: Intent?): Boolean = suspendCoroutine { continuation ->
        val authResponse = data?.let { AuthorizationResponse.fromIntent(it) }
        if (authResponse != null) {
            authService.performTokenRequest(
                authResponse.createTokenExchangeRequest()
            ) { tokenResponse, exception ->
                val success = handleTokenResponse(tokenResponse, exception)
                continuation.resume(success)
            }
        } else {
            continuation.resume(false)
        }
    }

    suspend fun persistRefreshToken(
        activity: FragmentActivity,
        title: String,
        subtitle: String? = null,
        description: String? = null
    ): Boolean {
        secureStore.upsert(REFRESH_TOKEN_KEY, tokens.refreshToken)
        val result = retrieveRefreshToken(
            activity = activity,
            title = title,
            subtitle = subtitle,
            description = description
        )

        return if (result is RetrievalEvent.Success) {
            true
        } else {
            secureStore.delete(REFRESH_TOKEN_KEY)
            false
        }
    }

    fun isAuthenticationEnabled(): Boolean {
        val result  = biometricManager.canAuthenticate(
            Authenticators.BIOMETRIC_STRONG or Authenticators.DEVICE_CREDENTIAL
        )
        return result == BiometricManager.BIOMETRIC_SUCCESS
    }

    fun isUserSignedIn(): Boolean {
        return secureStore.exists(REFRESH_TOKEN_KEY)
    }

    suspend fun refreshTokens(
        activity: FragmentActivity,
        title: String,
        subtitle: String? = null,
        description: String? = null
    ): Boolean {
        val result = retrieveRefreshToken(
            activity = activity,
            title = title,
            subtitle = subtitle,
            description = description
        )
        return suspendCoroutine { continuation ->
            if (result is RetrievalEvent.Success) {
                val refreshToken = result.value[REFRESH_TOKEN_KEY]
                val tokenRequest = tokenRequestBuilder
                    .setRefreshToken(refreshToken)
                    .build()

                authService.performTokenRequest(tokenRequest) { tokenResponse, exception ->
                    val success = handleTokenResponse(tokenResponse, exception, refreshToken)
                    continuation.resume(success)
                }
            } else {
                continuation.resume(false)
            }
        }
    }

    private suspend fun retrieveRefreshToken(
        activity: FragmentActivity,
        title: String,
        subtitle: String? = null,
        description: String? = null
    ): RetrievalEvent {
        return secureStore.retrieveWithAuthentication(
            key = arrayOf(REFRESH_TOKEN_KEY),
            authPromptConfig = AuthenticatorPromptConfiguration(
                title = title,
                subTitle = subtitle,
                description = description
            ),
            context = activity
        )
    }

    private fun handleTokenResponse(
        tokenResponse: TokenResponse?,
        exception: AuthorizationException?,
        refreshToken: String? = null
    ): Boolean {
        val mappedTokenResponse = tokenResponseMapper.map(tokenResponse)
        val accessToken = mappedTokenResponse.accessToken
        val idToken = mappedTokenResponse.idToken
        val mappedRefreshToken = refreshToken ?: mappedTokenResponse.refreshToken

        return if (exception == null &&
            accessToken != null &&
            idToken != null &&
            mappedRefreshToken != null
        ) {
            tokens = Tokens(
                accessToken = accessToken,
                idToken = idToken,
                refreshToken = mappedRefreshToken
            )

            true
        } else {
            false
        }
    }

    fun isDifferentUser(): Boolean {
        val currentSubId = sharedPreferences.getString(SUB_ID_KEY, "")
        val newSubId = getIdTokenProperty("sub")
        sharedPreferences.edit(commit = true) { putString(SUB_ID_KEY, newSubId) }

        return currentSubId != "" && currentSubId != newSubId
    }

    fun getUserEmail(): String {
        return getIdTokenProperty("email")
    }

    private fun getIdTokenProperty(name: String): String {
        val parts = tokens.idToken.split(".")
        return try {
            if (parts.size == 3) {
                val payload = String(Base64.decode(parts[1], Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP))
                val json = JSONObject(payload)
                return json.getString(name)
            } else ""
        } catch (e: Exception) {
            ""
        }
    }

    suspend fun signOut(): Boolean {
        try {
            secureStore.delete(REFRESH_TOKEN_KEY)

            try {
                authApi.revoke(
                    refreshToken = tokens.refreshToken,
                    clientId = BuildConfig.AUTH_CLIENT_ID
                )
            } catch (e: Exception) {
                // Ignore API failure
            }

            tokens = Tokens()
            return true
        } catch (e: SecureStorageError) {
            return false
        }
    }
}
