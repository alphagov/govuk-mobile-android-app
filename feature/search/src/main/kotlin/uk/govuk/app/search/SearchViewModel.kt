package uk.govuk.app.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uk.govuk.app.analytics.Analytics
import uk.govuk.app.search.api.SearchRepo
import uk.govuk.app.search.api_result.Result
import uk.govuk.app.search.domain.ResultStatus
import javax.inject.Inject

internal data class SearchUiState(
    val searchTerm: String,
    val searchResults: List<Result>,
    val resultStatus: ResultStatus,
)

@HiltViewModel
internal class SearchViewModel @Inject constructor(
    private val analytics: Analytics,
    private val repository: SearchRepo
): ViewModel() {

    private val _uiState: MutableStateFlow<SearchUiState?> = MutableStateFlow(null)
    val uiState = _uiState.asStateFlow()

    private fun fetchSearchResults(searchTerm: String) {
        viewModelScope.launch {
            val searchResult = repository.performSearch(searchTerm)

            _uiState.value = SearchUiState(
                searchTerm = searchTerm,
                searchResults = searchResult.response.results,
                resultStatus = searchResult.status
            )
        }
    }

    companion object {
        private const val SCREEN_CLASS = "SearchScreen"
        private const val SCREEN_NAME = "Search"
        private const val TITLE = "Search"
    }

    fun onPageView() {
        analytics.screenView(
            screenClass = SCREEN_CLASS,
            screenName = SCREEN_NAME,
            title = TITLE
        )
    }

    fun onSearch(searchTerm: String) {
        fetchSearchResults(searchTerm)
        analytics.search(searchTerm)
    }

    fun onSearchResultClicked(title: String, url: String) {
        analytics.searchResultClick(text = title, url = url)
    }
}
