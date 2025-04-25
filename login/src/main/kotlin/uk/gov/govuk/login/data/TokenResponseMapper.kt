package uk.gov.govuk.login.data

import net.openid.appauth.TokenResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class TokenResponseMapper @Inject constructor() {

    data class Tokens(
        val accessToken: String?,
        val idToken: String?,
        val refreshToken: String?
    )

    fun map(tokenResponse: TokenResponse?): Tokens {
        return Tokens(
            accessToken = tokenResponse?.accessToken,
            idToken = tokenResponse?.idToken,
            refreshToken = tokenResponse?.refreshToken
        )
    }
}