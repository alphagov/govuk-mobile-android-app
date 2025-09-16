package uk.gov.govuk.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import uk.gov.govuk.analytics.AnalyticsClient
import uk.gov.govuk.chat.ChatFeature
import javax.inject.Inject

@HiltViewModel
internal class HomeViewModel @Inject constructor(
    private val analyticsClient: AnalyticsClient,
    private val chatFeature: ChatFeature
): ViewModel() {

    companion object {
        private const val SCREEN_CLASS = "HomeScreen"
        private const val SCREEN_NAME = "Homepage"
        private const val TITLE = "Homepage"
    }

    fun onPageView() {
        viewModelScope.launch {
            val hasOptedIn = chatFeature.hasOptedIn().first()
            val type = if (hasOptedIn) "chatOptIn" else "chatOptOut"

            analyticsClient.screenViewWithType(
                screenClass = SCREEN_CLASS,
                screenName = SCREEN_NAME,
                title = TITLE,
                type = type
            )
        }
    }
}
