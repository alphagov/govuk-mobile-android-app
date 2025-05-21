package uk.gov.govuk.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import uk.gov.govuk.analytics.AnalyticsClient
import uk.gov.govuk.notifications.data.local.NotificationsDataStore
import javax.inject.Inject

@HiltViewModel
internal open class NotificationsViewModel @Inject constructor(
    private val analyticsClient: AnalyticsClient,
    private val notificationsClient: NotificationsClient,
    private val notificationsDataStore: NotificationsDataStore
) : ViewModel() {

    companion object {
        private const val SCREEN_CLASS = "NotificationsOnboardingScreen"
        private const val TITLE = "NotificationsOnboardingScreen"
    }

    internal fun onPageView() {
        analyticsClient.screenView(
            screenClass = SCREEN_CLASS,
            screenName = TITLE,
            title = TITLE
        )
    }

    internal fun onAllowNotificationsClick(text: String, onCompleted: () -> Unit) {
        viewModelScope.launch {
            notificationsDataStore.onboardingCompleted()
            notificationsDataStore.firstPermissionRequestCompleted()
        }
        notificationsClient.giveConsent()
        notificationsClient.requestPermission {
            viewModelScope.launch {
                onCompleted()
            }
        }
        analyticsClient.buttonClick(
            text = text
        )
    }

    internal fun onNotNowClick(text: String) {
        viewModelScope.launch {
            notificationsDataStore.onboardingCompleted()
        }
        analyticsClient.buttonClick(
            text = text
        )
    }

    internal fun onGiveConsentClick(text: String, onCompleted: () -> Unit) {
        viewModelScope.launch {
            notificationsDataStore.onboardingCompleted()
        }
        notificationsClient.giveConsent()
        analyticsClient.buttonClick(
            text = text
        )
        onCompleted()
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
