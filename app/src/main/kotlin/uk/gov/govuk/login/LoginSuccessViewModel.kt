package uk.gov.govuk.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import uk.gov.govuk.data.AppRepo
import uk.gov.govuk.data.auth.AuthRepo
import javax.inject.Inject

internal data class LoginSuccessEvent(val isBiometricsEnabled: Boolean)

@HiltViewModel
internal class LoginSuccessViewModel @Inject constructor(
    private val appRepo: AppRepo,
    private val authRepo: AuthRepo
) : ViewModel() {

    private val _loginSuccessCompleted = MutableSharedFlow<LoginSuccessEvent>()
    val loginSuccessCompleted: SharedFlow<LoginSuccessEvent> = _loginSuccessCompleted

    fun onContinue() {
        viewModelScope.launch {
            _loginSuccessCompleted.emit(
                LoginSuccessEvent(authRepo.isAuthenticationEnabled()
                        && !appRepo.hasSkippedBiometrics())
            )
        }
    }
}
