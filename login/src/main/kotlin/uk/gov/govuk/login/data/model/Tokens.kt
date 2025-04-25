package uk.gov.govuk.login.data.model

data class Tokens(
    val accessToken: String,
    val idToken: String,
    val refreshToken: String
)
