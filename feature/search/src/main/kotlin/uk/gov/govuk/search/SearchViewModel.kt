package uk.gov.govuk.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uk.gov.govuk.analytics.AnalyticsClient
import uk.gov.govuk.data.model.Result.DeviceOffline
import uk.gov.govuk.data.model.Result.Success
import uk.gov.govuk.search.SearchUiState.Error
import uk.gov.govuk.search.SearchUiState.SearchResults
import uk.gov.govuk.search.SearchUiState.Suggestions
import uk.gov.govuk.search.data.SearchRepo
import uk.gov.govuk.search.domain.SearchConfig
import uk.gov.govuk.visited.Visited
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

    private var performingSearch: Boolean = false

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
        val trimmedSearchTerm = searchTerm.trim()

        if (trimmedSearchTerm.isNotEmpty()) {
            viewModelScope.launch {
                performingSearch = true
                emitUiState()
                val result = searchRepo.performSearch(trimmedSearchTerm)
                performingSearch = false
                val id = UUID.randomUUID()
                when (result) {
                    is Success -> {
                        if (result.value.results.isNotEmpty()) {
                            emitUiState(
                                searchResults = SearchResults(
                                    trimmedSearchTerm,
                                    result.value.results
                                )
                            )
                        } else {
                            emitUiState(error = Error.Empty(id, trimmedSearchTerm))
                        }
                    }

                    is DeviceOffline -> emitUiState(error = Error.Offline(id, trimmedSearchTerm))
                    else -> emitUiState(error = Error.ServiceError(id))
                }
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
            performingSearch = performingSearch,
            error = error
        )
    }
}