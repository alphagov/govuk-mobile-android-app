package uk.govuk.app.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uk.govuk.app.analytics.AnalyticsClient
import uk.govuk.app.data.model.Result.*
import uk.govuk.app.search.SearchUiState.Error
import uk.govuk.app.search.SearchUiState.SearchResults
import uk.govuk.app.search.SearchUiState.Suggestions
import uk.govuk.app.search.data.SearchRepo
import uk.govuk.app.search.domain.SearchConfig
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

    private val _uiState: MutableStateFlow<SearchUiState> = MutableStateFlow(SearchUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            searchRepo.previousSearches.collect { previousSearches ->
                emitUiState(previousSearches = previousSearches)
            }
        }
    }

    fun onPageView() {
        analyticsClient.screenView(
            screenClass = SCREEN_CLASS,
            screenName = SCREEN_NAME,
            title = TITLE
        )
    }

    private fun fetchSearchResults(searchTerm: String) {
        viewModelScope.launch {
            val id = UUID.randomUUID()
            val result = searchRepo.performSearch(searchTerm)
            when (result) {
                is Success -> {
                    if (result.value.results.isNotEmpty()) {
                        emitUiState(
                            searchResults = SearchResults(searchTerm, result.value.results)
                        )
                    } else {
                        emitUiState(error = Error.Empty(id, searchTerm))
                    }
                }
                is DeviceOffline -> emitUiState(error = Error.Offline(id, searchTerm))
                else -> emitUiState(error = Error.ServiceError(id))
            }
        }
    }

    private fun fetchAutocompleteSuggestions(searchTerm: String) {
        viewModelScope.launch {
            val result = searchRepo.performLookup(searchTerm)
            if (result is Success) {
                emitUiState(suggestions = Suggestions(searchTerm, result.value.suggestions))
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
        emitUiState()
    }

    fun onRemoveAllPreviousSearches() {
        viewModelScope.launch {
            searchRepo.removeAllPreviousSearches()
        }
    }

    fun onRemovePreviousSearch(searchTerm: String) {
        viewModelScope.launch {
            searchRepo.removePreviousSearch(searchTerm)
        }
    }

    fun onAutocomplete(searchTerm: String) {
        if (searchTerm.length >= SearchConfig.AUTOCOMPLETE_MIN_LENGTH) {
            fetchAutocompleteSuggestions(searchTerm)
        } else {
            emitUiState()
        }
    }

    fun onAutocompleteResultClick(searchTerm: String) {
        fetchSearchResults(searchTerm)
        analyticsClient.autocomplete(searchTerm)
    }

    fun onPreviousSearchClick(searchTerm: String) {
        fetchSearchResults(searchTerm)
        analyticsClient.history(searchTerm)
    }

    private fun emitUiState(
        previousSearches: List<String> = uiState.value.previousSearches,
        suggestions: Suggestions? = null,
        searchResults: SearchResults? = null,
        error: Error? = null
    ) {
        _uiState.value = SearchUiState(
            previousSearches = previousSearches,
            suggestions = suggestions,
            searchResults = searchResults,
            error = error
        )
    }
}
