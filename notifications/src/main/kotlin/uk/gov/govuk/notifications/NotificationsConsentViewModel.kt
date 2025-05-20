package uk.gov.govuk.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.isGranted
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uk.gov.govuk.analytics.AnalyticsClient
import uk.gov.govuk.notifications.data.local.NotificationsDataStore
import javax.inject.Inject

@HiltViewModel
internal class NotificationsConsentViewModel @Inject constructor(
    private val analyticsClient: AnalyticsClient,
    private val notificationsClient: NotificationsClient,
    private val notificationsDataStore: NotificationsDataStore
) : ViewModel() {

    companion object {
        private const val SCREEN_CLASS = "NotificationsOnboardingScreen"
        private const val TITLE = "NotificationsOnboardingScreen"
    }

    private val _uiState: MutableStateFlow<NotificationsUiState?> = MutableStateFlow(null)
    internal val uiState = _uiState.asStateFlow()

    @OptIn(ExperimentalPermissionsApi::class)
    internal fun updateUiState(
        status: PermissionStatus
    ) {
        viewModelScope.launch {
            _uiState.value = if (!status.isGranted || notificationsClient.consentGiven()) {
                NotificationsUiState.Finish
            } else {
                NotificationsUiState.Default
            }
        }
    }

    internal fun onPageView() {
        analyticsClient.screenView(
            screenClass = SCREEN_CLASS,
            screenName = TITLE,
            title = TITLE
        )
    }

    internal fun onGiveConsentClick(text: String) {
        viewModelScope.launch {
            notificationsDataStore.onboardingCompleted()
        }
        notificationsClient.giveConsent()
        analyticsClient.buttonClick(
            text = text
        )
        _uiState.value = NotificationsUiState.Finish
    }

    internal fun onTurnOffNotificationsClick(text: String) {
        viewModelScope.launch {
            notificationsDataStore.onboardingCompleted()
        }
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
