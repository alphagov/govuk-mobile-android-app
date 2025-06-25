package uk.gov.govuk.auth

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import uk.gov.govuk.data.auth.AuthRepo
import javax.inject.Inject

@HiltViewModel
internal class AuthenticationViewModel @Inject constructor(
    private val authRepo: AuthRepo
) : ViewModel() {

    val authenticationState: AuthenticationState
        get() = if (authRepo.isUserSessionActive()) {
            AuthenticationState.LoggedIn
        } else {
            AuthenticationState.NotLoggedIn
        }
}
