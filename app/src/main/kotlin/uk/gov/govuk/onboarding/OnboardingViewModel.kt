package uk.gov.govuk.onboarding

import android.content.Intent
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import uk.gov.govuk.R
import uk.gov.govuk.analytics.AnalyticsClient
import uk.gov.govuk.data.auth.AuthRepo
import uk.gov.govuk.data.auth.ErrorEvent
import javax.inject.Inject

@HiltViewModel
internal class OnboardingViewModel @Inject constructor(
    private val authRepo: AuthRepo,
    private val analyticsClient: AnalyticsClient
): ViewModel() {

    companion object {
        private const val ONBOARDING_SCREEN_CLASS = "OnboardingScreen"
        private const val ONBOARDING_SCREEN_NAME = "Onboarding Page"
        private const val ONBOARDING_TITLE = "Onboarding Page"
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
                        title = activity.getString(R.string.login_biometric_prompt_title)
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
            screenClass = ONBOARDING_SCREEN_CLASS,
            screenName = ONBOARDING_SCREEN_NAME,
            title = ONBOARDING_TITLE
        )
    }

    fun onButtonClick(text: String) {
        analyticsClient.buttonClick(text)
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
