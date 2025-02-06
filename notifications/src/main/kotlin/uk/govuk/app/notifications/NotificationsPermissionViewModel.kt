package uk.govuk.app.notifications

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import uk.govuk.app.analytics.AnalyticsClient
import javax.inject.Inject

@HiltViewModel
class NotificationsPermissionViewModel @Inject constructor(
    private val analyticsClient: AnalyticsClient,
    private val notificationsClient: NotificationsClient
) : ViewModel() {

    companion object {
        private const val SCREEN_CLASS = "NotificationsPermissionScreen"
        private const val TITLE = "NotificationsPermissionScreen"
    }

    internal fun onPageView() {
        analyticsClient.screenView(
            screenClass = SCREEN_CLASS,
            screenName = TITLE,
            title = TITLE
        )
    }

    internal fun onImInClick(text: String) {
        notificationsClient.requestPermission()
        analyticsClient.buttonClick(
            text = text
        )
    }

    internal fun onSkipClick(text: String) {
        analyticsClient.buttonClick(
            text = text
        )
    }
}
