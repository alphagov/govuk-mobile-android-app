package uk.govuk.app.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uk.govuk.app.analytics.AnalyticsClient
import uk.govuk.app.networking.domain.DeviceOfflineException
import uk.govuk.app.search.SearchUiState.Default
import uk.govuk.app.search.SearchUiState.Error
import uk.govuk.app.search.SearchUiState.Results
import uk.govuk.app.search.data.SearchRepo
import uk.govuk.app.visited.Visited
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
internal class SearchViewModel @Inject constructor(
    private val analyticsClient: AnalyticsClient,
    private val visited: Visited,
    private val searchRepo: SearchRepo,
): ViewModel() {

    companion object {
        private const val SCREEN_CLASS = "SearchScreen"
        private const val SCREEN_NAME = "Search"
        private const val TITLE = "Search"
    }

    private val _uiState: MutableStateFlow<SearchUiState?> = MutableStateFlow(null)
    val uiState = _uiState.asStateFlow()

    init {
        emitPreviousSearches()
    }

    fun onPageView() {
        analyticsClient.screenView(
            screenClass = SCREEN_CLASS,
            screenName = SCREEN_NAME,
            title = TITLE
        )
    }

    private fun emitPreviousSearches() {
        viewModelScope.launch {
            _uiState.value = Default(searchRepo.fetchPreviousSearches())
        }
    }

    private fun fetchSearchResults(searchTerm: String) {
        viewModelScope.launch {
            val id = UUID.randomUUID()
            val searchResult = searchRepo.performSearch(searchTerm.trim())
            searchResult.onSuccess { result ->
                if (result.results.isNotEmpty()) {
                    _uiState.value = Results(
                        searchTerm = searchTerm,
                        searchResults = result.results
                    )
                } else {
                    _uiState.value = Error.Empty(id, searchTerm)
                }
            }
            searchResult.onFailure { exception ->
                _uiState.value = when (exception) {
                    is DeviceOfflineException -> Error.Offline(id, searchTerm)
                    else -> Error.ServiceError(id)
                }
            }
        }
    }

    private fun fetchAutocompleteSuggestions(searchTerm: String) {
        viewModelScope.launch {
            val id = UUID.randomUUID()
            val autocompleteResult = searchRepo.performLookup(searchTerm.trim())
            autocompleteResult.onSuccess { result ->
                if (result.suggestions.isNotEmpty()) {
                    _uiState.value = SearchUiState.Autocomplete(
                        searchTerm = searchTerm,
                        suggestions = result.suggestions,
                    )
                } else {
                    _uiState.value = SearchUiState.Autocomplete(
                        searchTerm = searchTerm,
                        suggestions = emptyList(),
                    )
                }
            }
            autocompleteResult.onFailure { exception ->
                _uiState.value = when (exception) {
                    is DeviceOfflineException -> Error.Offline(id, searchTerm)
                    else -> Error.ServiceError(id)
                }
            }
        }
    }

    fun onSearch(searchTerm: String) {
        fetchSearchResults(searchTerm)
        analyticsClient.search(searchTerm)
    }

    fun onSearchResultClicked(title: String, url: String) {
        analyticsClient.searchResultClick(text = title, url = url)
        viewModelScope.launch {
            visited.visitableItemClick(title = title, url = url)
        }
    }

    fun onClear() {
        emitPreviousSearches()
    }

    fun onRemoveAllPreviousSearches() {
        viewModelScope.launch {
            searchRepo.removeAllPreviousSearches()
        }
        emitPreviousSearches()
    }

    fun onRemovePreviousSearch(searchTerm: String) {
        viewModelScope.launch {
            searchRepo.removePreviousSearch(searchTerm)
        }
        emitPreviousSearches()
    }

    fun onAutocomplete(searchTerm: String) {
        fetchAutocompleteSuggestions(searchTerm)
        analyticsClient.autocomplete(searchTerm)
    }
}
