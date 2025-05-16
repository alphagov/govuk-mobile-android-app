package uk.gov.govuk.notifications

import android.os.Build
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

@OptIn(ExperimentalPermissionsApi::class)
@HiltViewModel
internal class NotificationsOnboardingViewModel @Inject constructor(
    private val analyticsClient: AnalyticsClient,
    private val notificationsClient: NotificationsClient,
    private val notificationsDataStore: NotificationsDataStore
) : ViewModel() {

    companion object {
        private const val SCREEN_CLASS = "NotificationsOnboardingScreen"
        private const val TITLE = "NotificationsOnboardingScreen"
    }

    private val _uiState: MutableStateFlow<NotificationsOnboardingUiState?> = MutableStateFlow(null)
    internal val uiState = _uiState.asStateFlow()

    internal fun updateUiState(
        status: PermissionStatus,
        androidVersion: Int = Build.VERSION.SDK_INT,
        fromSettings: Boolean = false,
    ) {
        viewModelScope.launch {
            _uiState.value = if (fromSettings) {
                NotificationsOnboardingUiState.Default
            } else if (status.isGranted) {
                when {
                    (!notificationsDataStore.isOnboardingCompleted()
                            && androidVersion < Build.VERSION_CODES.TIRAMISU) -> {
                        NotificationsOnboardingUiState.NoConsent
                    }

                    !notificationsClient.consentGiven() -> NotificationsOnboardingUiState.NoConsent
                    notificationsDataStore.isOnboardingCompleted() -> NotificationsOnboardingUiState.Finish
                    else -> {
                        NotificationsOnboardingUiState.Default
                    }
                }
            } else {
                when {
                    !notificationsDataStore.isOnboardingCompleted() -> {
                        NotificationsOnboardingUiState.Default
                    }
                    else -> NotificationsOnboardingUiState.Finish
                }
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

    internal fun onContinueClick(text: String) {
        viewModelScope.launch {
            notificationsDataStore.onboardingCompleted()
        }
        notificationsClient.giveConsent()
        notificationsClient.requestPermission {
            _uiState.value = NotificationsOnboardingUiState.Finish
        }
        analyticsClient.buttonClick(
            text = text
        )
    }

    internal fun onSkipClick(text: String) {
        viewModelScope.launch {
            notificationsDataStore.onboardingCompleted()
        }
        analyticsClient.buttonClick(
            text = text
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
