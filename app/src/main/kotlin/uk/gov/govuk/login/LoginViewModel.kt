package uk.gov.govuk.login

import android.content.Intent
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uk.gov.govuk.R
import uk.gov.govuk.data.auth.AuthRepo
import uk.gov.govuk.data.auth.ErrorEvent
import uk.gov.govuk.login.data.LoginRepo
import java.util.Date
import javax.inject.Inject

internal data class LoginEvent(val isBiometricLogin: Boolean)

@HiltViewModel
internal class LoginViewModel @Inject constructor(
    private val authRepo: AuthRepo,
    private val loginRepo: LoginRepo
) : ViewModel() {

    private val _isLoading: MutableStateFlow<Boolean?> = MutableStateFlow(null)
    val isLoading = _isLoading.asStateFlow()

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
                if (shouldRefreshTokens()) {
                    authRepo.refreshTokens(activity = activity, title = activity.getString(R.string.login_biometric_prompt_title)).collect { status ->
                        when (status) {
                            AuthRepo.RefreshStatus.LOADING -> {
                                _isLoading.value = true
                            }
                            AuthRepo.RefreshStatus.SUCCESS ->
                                _loginCompleted.emit(LoginEvent(isBiometricLogin = true))
                            AuthRepo.RefreshStatus.ERROR -> {
                                _isLoading.value = false
                            }
                        }
                    }
                } else {
                    authRepo.endUserSession()
                    authRepo.clear()
                }
            }
        }
    }

    fun onAuthResponse(data: Intent?) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = authRepo.handleAuthResponse(data)
            if (result) {
                saveRefreshTokenExpiryDate()
                _loginCompleted.emit(LoginEvent(isBiometricLogin = false))
            } else {
                _errorEvent.emit(ErrorEvent.UnableToSignInError)
            }
        }
    }

    private suspend fun shouldRefreshTokens(): Boolean {
        val refreshTokenExpiryDate = loginRepo.getRefreshTokenExpiryDate()
        return refreshTokenExpiryDate == null || refreshTokenExpiryDate > Date().toInstant().epochSecond
    }

    private fun saveRefreshTokenExpiryDate() {
        viewModelScope.launch {
            authRepo.getIdTokenIssueDate()?.let { idTokenIssueDate ->
                val datePlusSixDaysTwentyThreeHours = idTokenIssueDate + 601200L
                loginRepo.setRefreshTokenExpiryDate(datePlusSixDaysTwentyThreeHours)
            }
        }
    }
}
