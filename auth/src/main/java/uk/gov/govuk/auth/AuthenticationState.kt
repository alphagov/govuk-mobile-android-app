package uk.gov.govuk.auth

sealed interface AuthenticationState {
    data object NotLoggedIn : AuthenticationState
    data object LoggedIn : AuthenticationState
}
