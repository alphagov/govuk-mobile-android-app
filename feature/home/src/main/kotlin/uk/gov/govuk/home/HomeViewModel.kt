package uk.gov.govuk.home

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import uk.gov.govuk.analytics.AnalyticsClient
import javax.inject.Inject

@HiltViewModel
internal class HomeViewModel @Inject constructor(
    private val analyticsClient: AnalyticsClient
): ViewModel() {

    companion object {
        private const val SCREEN_CLASS = "HomeScreen"
        private const val SCREEN_NAME = "Homepage"
        private const val TITLE = "Homepage"
    }

    fun onPageView(userChatOptInState: Boolean) {
        analyticsClient.screenViewWithType(
            screenClass = SCREEN_CLASS,
            screenName = SCREEN_NAME,
            title = TITLE,
            type = if (userChatOptInState) "chatOptIn" else "chatOptOut"
        )
    }
}
