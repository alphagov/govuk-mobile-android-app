package uk.gov.govuk.login.data

import android.content.Intent
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators
import androidx.fragment.app.FragmentActivity
import net.openid.appauth.AuthorizationResponse
import net.openid.appauth.AuthorizationService
import uk.gov.android.securestore.RetrievalEvent
import uk.gov.android.securestore.SecureStore
import uk.gov.android.securestore.authentication.AuthenticatorPromptConfiguration
import uk.gov.govuk.login.data.model.Tokens
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@Singleton
internal class LoginRepo @Inject constructor(
    val authIntent: Intent,
    private val authService: AuthorizationService,
    private val tokenResponseMapper: TokenResponseMapper,
    private val secureStore: SecureStore,
    private val biometricManager: BiometricManager
) {
    companion object {
        private const val REFRESH_TOKEN_KEY = "refreshToken"
    }

    private lateinit var tokens: Tokens

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
        val result = secureStore.retrieveWithAuthentication(
            key = arrayOf(REFRESH_TOKEN_KEY),
            authPromptConfig = AuthenticatorPromptConfiguration(
                title = title,
                subTitle = subtitle,
                description = description
            ),
            context = activity
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
}
