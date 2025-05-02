package uk.gov.govuk.data.auth

import android.content.Intent
import android.util.Base64
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators
import androidx.fragment.app.FragmentActivity
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationResponse
import net.openid.appauth.AuthorizationService
import org.json.JSONObject
import uk.gov.android.securestore.RetrievalEvent
import uk.gov.android.securestore.SecureStore
import uk.gov.android.securestore.authentication.AuthenticatorPromptConfiguration
import uk.gov.govuk.data.auth.model.Tokens
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@Singleton
class AuthRepo @Inject constructor(
    private val authRequest: AuthorizationRequest,
    private val authService: AuthorizationService,
    private val tokenResponseMapper: TokenResponseMapper,
    private val secureStore: SecureStore,
    private val biometricManager: BiometricManager
) {
    companion object {
        private const val REFRESH_TOKEN_KEY = "refreshToken"
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
                val mappedTokenResponse = tokenResponseMapper.map(tokenResponse)

                val accessToken = mappedTokenResponse.accessToken
                val idToken = mappedTokenResponse.idToken
                val refreshToken = mappedTokenResponse.refreshToken

                if (exception == null &&
                    accessToken != null &&
                    idToken != null &&
                    refreshToken != null
                ) {
                    tokens = Tokens(
                        accessToken = accessToken,
                        idToken = idToken,
                        refreshToken = refreshToken
                    )
                    continuation.resume(true)
                } else {
                    continuation.resume(false)
                }
            }
        } else {
            continuation.resume(false)
        }
    }

    suspend fun persistRefreshToken(
        activity: FragmentActivity,
        title: String,
        subtitle: String,
        description: String
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

    suspend fun performTokenExchange(
        activity: FragmentActivity,
        title: String,
        subtitle: String,
        description: String
    ): Boolean {
        val result = retrieveRefreshToken(
            activity = activity,
            title = title,
            subtitle = subtitle,
            description = description
        )
        return result is RetrievalEvent.Success
    }

    private suspend fun retrieveRefreshToken(
        activity: FragmentActivity,
        title: String,
        subtitle: String,
        description: String
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

    fun getUserEmail(): String {
        val parts = tokens.idToken.split(".")
        return try {
            if (parts.size == 3) {
                val payload = String(Base64.decode(parts[1], Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP))
                val json = JSONObject(payload)
                return json.getString("email")
            } else ""
        } catch (e: Exception) {
            ""
        }
    }

    fun signOut() {
        secureStore.delete(REFRESH_TOKEN_KEY)
        tokens = Tokens()
    }
}
