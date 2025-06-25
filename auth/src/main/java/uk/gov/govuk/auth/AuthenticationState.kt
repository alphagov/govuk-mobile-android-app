package uk.gov.govuk.auth

internal sealed class AuthenticationState {
    data object NotLoggedIn : AuthenticationState()
    data object LoggedIn : AuthenticationState()
}
