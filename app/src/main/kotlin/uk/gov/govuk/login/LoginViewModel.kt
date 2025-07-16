package uk.gov.govuk.login

import android.content.Intent
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import uk.gov.govuk.R
import uk.gov.govuk.data.auth.AuthRepo
import uk.gov.govuk.data.auth.ErrorEvent
import javax.inject.Inject

internal data class LoginEvent(val isBiometricLogin: Boolean)

@HiltViewModel
internal class LoginViewModel @Inject constructor(
    private val authRepo: AuthRepo
) : ViewModel() {

    private val _loginCompleted = MutableSharedFlow<LoginEvent>()
    val loginCompleted: SharedFlow<LoginEvent> = _loginCompleted

    private val _errorEvent = MutableSharedFlow<ErrorEvent>()
    val errorEvent: SharedFlow<ErrorEvent> = _errorEvent

    val authIntent: Intent by lazy {
        authRepo.authIntent
    }

    fun init(activity: FragmentActivity) {
        if (authRepo.isUserSignedIn()) {
            viewModelScope.launch {
                if (
                    authRepo.refreshTokens(
                        activity = activity,
                        title = activity.getString(R.string.login_biometric_prompt_title)
                    )
                ) {
                    _loginCompleted.emit(LoginEvent(isBiometricLogin = true))
                } else {
                    // Todo - handle failure!!!
                }
            }
        }
    }

    fun onAuthResponse(data: Intent?) {
        viewModelScope.launch {
            val result = authRepo.handleAuthResponse(data)
            if (result) {
                _loginCompleted.emit(LoginEvent(isBiometricLogin = false))
            } else {
                _errorEvent.emit(ErrorEvent.UnableToSignInError)
            }
        }
    }
}
