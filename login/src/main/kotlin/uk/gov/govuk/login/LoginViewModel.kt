package uk.gov.govuk.login

import android.content.Intent
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import uk.gov.govuk.analytics.AnalyticsClient
import uk.gov.govuk.data.auth.AuthRepo
import javax.inject.Inject

sealed class ErrorEvent {
    data object UnableToSignInError: ErrorEvent()
}

@HiltViewModel
internal class LoginViewModel @Inject constructor(
    private val authRepo: AuthRepo,
    private val analyticsClient: AnalyticsClient
) : ViewModel() {

    companion object {
        private const val SCREEN_CLASS = "LoginScreen"
        private const val SCREEN_NAME = "Login"
        private const val TITLE = "Login"

        private const val SECTION = "Login"
    }

    private val _loginCompleted = MutableSharedFlow<Boolean>()
    val loginCompleted: SharedFlow<Boolean> = _loginCompleted

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
                        title = activity.getString(R.string.login_biometric_prompt_title),
                        subtitle = activity.getString(R.string.login_biometric_prompt_subtitle),
                        description = activity.getString(R.string.login_biometric_prompt_description)
                    )
                ) {
                    _loginCompleted.emit(false)
                } else {
                    // Todo - handle failure!!!
                }
            }
        }
    }

    fun onPageView() {
        analyticsClient.screenView(
            screenClass = SCREEN_CLASS,
            screenName = SCREEN_NAME,
            title = TITLE
        )
    }

    fun onContinue(text: String) {
        analyticsClient.buttonClick(
            text = text,
            section = SECTION
        )
    }

    fun onAuthResponse(data: Intent?) {
        viewModelScope.launch {
            val result = authRepo.handleAuthResponse(data)
            if (result) {
                _loginCompleted.emit(authRepo.isDifferentUser())
            } else {
                _errorEvent.emit(ErrorEvent.UnableToSignInError)
            }
        }
    }
}