package uk.govuk.app.home

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import uk.govuk.app.analytics.AnalyticsClient
import uk.govuk.app.config.data.flags.FlagRepo
import javax.inject.Inject

@HiltViewModel
internal class HomeViewModel @Inject constructor(
    private val analyticsClient: AnalyticsClient,
    private val flagRepo: FlagRepo
): ViewModel() {

    companion object {
        private const val SCREEN_CLASS = "HomeScreen"
        private const val SCREEN_NAME = "Homepage"
        private const val TITLE = "Homepage"
    }

    fun onPageView() {
        analyticsClient.screenView(
            screenClass = SCREEN_CLASS,
            screenName = SCREEN_NAME,
            title = TITLE
        )
    }

    fun isSearchEnabled(): Boolean {
        return flagRepo.isSearchEnabled()
    }
}
