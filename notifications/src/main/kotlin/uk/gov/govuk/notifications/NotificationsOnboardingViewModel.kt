package uk.gov.govuk.notifications

import androidx.lifecycle.ViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.isGranted
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import uk.gov.govuk.analytics.AnalyticsClient
import javax.inject.Inject

@OptIn(ExperimentalPermissionsApi::class)
@HiltViewModel
internal class NotificationsOnboardingViewModel @Inject constructor(
    private val analyticsClient: AnalyticsClient,
    private val notificationsClient: NotificationsClient
) : ViewModel() {

    companion object {
        private const val SCREEN_CLASS = "NotificationsOnboardingScreen"
        private const val TITLE = "NotificationsOnboardingScreen"
    }

    private val _uiState: MutableStateFlow<NotificationsOnboardingUiState?> = MutableStateFlow(null)
    internal val uiState = _uiState.asStateFlow()

    internal fun updateUiState(status: PermissionStatus) {
        _uiState.value = if (status.isGranted) {
            if (notificationsClient.consentGiven()) {
                NotificationsOnboardingUiState.Finish
            } else {
                NotificationsOnboardingUiState.NoConsent
            }
        } else {
            NotificationsOnboardingUiState.Default
        }
    }

    internal fun onPageView() {
        analyticsClient.screenView(
            screenClass = SCREEN_CLASS,
            screenName = TITLE,
            title = TITLE
        )
    }

    internal fun onContinueClick(text: String) {
        notificationsClient.giveConsent()
        notificationsClient.requestPermission {
            _uiState.value = NotificationsOnboardingUiState.Finish
        }
        analyticsClient.buttonClick(
            text = text
        )
    }

    internal fun onSkipClick(text: String) {
        analyticsClient.buttonClick(
            text = text
        )
    }

    internal fun onGiveConsentClick(text: String) {
        notificationsClient.giveConsent()
        analyticsClient.buttonClick(
            text = text
        )
        _uiState.value = NotificationsOnboardingUiState.Finish
    }

    internal fun onTurnOffNotificationsClick(text: String) {
        analyticsClient.buttonClick(
            text = text,
            external = true
        )
    }

    internal fun onPrivacyPolicyClick(text: String, url: String) {
        analyticsClient.buttonClick(
            text = text,
            url = url,
            external = true
        )
    }
}
