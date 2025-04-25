package uk.gov.govuk.login.data

import android.content.Intent
import net.openid.appauth.AuthorizationResponse
import net.openid.appauth.AuthorizationService
import uk.gov.govuk.login.data.model.Tokens
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@Singleton
internal class LoginRepo @Inject constructor(
    val authIntent: Intent,
    private val authService: AuthorizationService
) {
    private lateinit var _tokens: Tokens

    val tokens: Tokens
        get() = _tokens

    suspend fun handleAuthResponse(data: Intent?): Boolean = suspendCoroutine { continuation ->
        val authResponse = data?.let { AuthorizationResponse.fromIntent(it) }
        if (authResponse != null) {
            authService.performTokenRequest(
                authResponse.createTokenExchangeRequest()
            ) { tokenResponse, exception ->
                val accessToken = tokenResponse?.accessToken
                val idToken = tokenResponse?.idToken
                val refreshToken = tokenResponse?.refreshToken

                if (exception == null &&
                    accessToken != null &&
                    idToken != null &&
                    refreshToken != null
                ) {
                    _tokens = Tokens(
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
}
