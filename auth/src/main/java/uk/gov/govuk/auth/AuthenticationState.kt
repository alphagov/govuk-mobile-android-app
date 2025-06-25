package uk.gov.govuk.auth

internal sealed interface AuthenticationState {
    data object NotLoggedIn : AuthenticationState
    data object LoggedIn : AuthenticationState
}
