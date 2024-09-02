package uk.govuk.app.search

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import uk.govuk.app.analytics.Analytics
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val analytics: Analytics
): ViewModel() {

    companion object {
        private const val SCREEN_CLASS = "SearchScreen"
        private const val SCREEN_ALIAS = "SEARCH"
        private const val TITLE = "Search"
    }

    fun onPageView() {
        analytics.screenView(
            screenClass = SCREEN_CLASS,
            alias = SCREEN_ALIAS,
            title = TITLE
        )
    }

    fun onSearch(searchTerm: String) {
        analytics.search(searchTerm)
    }

}