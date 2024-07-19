package uk.govuk.app.home

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import uk.govuk.app.analytics.Analytics
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val analytics: Analytics
): ViewModel() {

    companion object {
        private const val SCREEN_CLASS = "HomeScreen"
        private const val SCREEN_ALIAS = "HOMEPAGE"
        private const val TITLE = "Homepage"
    }

    fun onPageView() {
        analytics.screenView(
            screenClass = SCREEN_CLASS,
            alias = SCREEN_ALIAS,
            title = TITLE
        )
    }

}