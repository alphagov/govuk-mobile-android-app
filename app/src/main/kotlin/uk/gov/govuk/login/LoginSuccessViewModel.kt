package uk.gov.govuk.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import uk.gov.govuk.analytics.AnalyticsClient
import uk.gov.govuk.data.AppRepo
import uk.gov.govuk.data.auth.AuthRepo
import javax.inject.Inject

internal data class LoginSuccessEvent(val isBiometricsEnabled: Boolean)

@HiltViewModel
internal class LoginSuccessViewModel @Inject constructor(
    private val appRepo: AppRepo,
    private val authRepo: AuthRepo,
    private val analyticsClient: AnalyticsClient
) : ViewModel() {

    private val _loginSuccessCompleted = MutableSharedFlow<LoginSuccessEvent>()
    val loginSuccessCompleted: SharedFlow<LoginSuccessEvent> = _loginSuccessCompleted

    companion object {
        private const val SCREEN_CLASS = "LoginSuccessScreen"
        private const val SCREEN_NAME = "Login Success"
        private const val TITLE = "Login Success"
    }

    fun onPageView() {
        analyticsClient.screenView(
            screenClass = SCREEN_CLASS,
            screenName = SCREEN_NAME,
            title = TITLE
        )
        viewModelScope.launch {
            _loginSuccessCompleted.emit(
                LoginSuccessEvent(authRepo.isAuthenticationEnabled()
                        && !appRepo.hasSkippedBiometrics())
            )
        }
    }

    fun onContinue(text: String) {
        analyticsClient.buttonClick(
            text = text,
            section = LOGIN_SECTION
        )
    }
}
