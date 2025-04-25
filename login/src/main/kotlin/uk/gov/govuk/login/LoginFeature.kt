package uk.gov.govuk.login

fun interface LoginFeature {
    fun isAuthenticationEnabled(): Boolean
}