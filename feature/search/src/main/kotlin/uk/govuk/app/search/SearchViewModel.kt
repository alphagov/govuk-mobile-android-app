package uk.govuk.app.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uk.govuk.app.analytics.Analytics
import uk.govuk.app.networking.domain.DeviceOfflineException
import uk.govuk.app.search.data.SearchRepo
import uk.govuk.app.visited.Visited
import javax.inject.Inject

@HiltViewModel
internal class SearchViewModel @Inject constructor(
    private val analytics: Analytics,
    private val visited: Visited,
    private val repository: SearchRepo
) : ViewModel() {

    private val _uiState: MutableStateFlow<SearchUiState?> = MutableStateFlow(null)
    val uiState = _uiState.asStateFlow()

    private fun fetchSearchResults(searchTerm: String) {
        viewModelScope.launch {
            val searchResult = repository.performSearch(searchTerm)
            searchResult.onSuccess { result ->
                _uiState.value = SearchUiState.Default(
                    searchTerm = searchTerm,
                    searchResults = result.results
                )
            }
            searchResult.onFailure { exception ->
                _uiState.value = when (exception) {
                    is DeviceOfflineException -> SearchUiState.Offline(searchTerm)
                    else -> SearchUiState.ServiceError(searchTerm)
                }
            }
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
        viewModelScope.launch {
            visited.visitableItemClick(title = title, url = url)
        }
    }

    fun onClear() {
        _uiState.value = null
    }
}
