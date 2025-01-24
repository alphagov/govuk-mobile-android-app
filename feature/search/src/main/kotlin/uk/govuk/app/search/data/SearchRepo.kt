package uk.govuk.app.search.data

import kotlinx.coroutines.flow.map
import uk.govuk.app.data.model.Result
import uk.govuk.app.data.remote.safeApiCall
import uk.govuk.app.search.data.local.SearchLocalDataSource
import uk.govuk.app.search.data.remote.AutocompleteApi
import uk.govuk.app.search.data.remote.SearchApi
import uk.govuk.app.search.data.remote.model.AutocompleteResponse
import uk.govuk.app.search.data.remote.model.SearchResponse
import uk.govuk.app.search.domain.SearchConfig
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class SearchRepo @Inject constructor(
    private val searchApi: SearchApi,
    private val autocompleteApi: AutocompleteApi,
    private val localDataSource: SearchLocalDataSource
) {

    val previousSearches = localDataSource.previousSearches.map { localSearches ->
        localSearches.map { it.searchTerm }
    }

    suspend fun removePreviousSearch(searchTerm: String) {
        localDataSource.removePreviousSearch(searchTerm)
    }

    suspend fun removeAllPreviousSearches() {
        localDataSource.removeAllPreviousSearches()
    }

    suspend fun performSearch(
        searchTerm: String, count: Int = SearchConfig.DEFAULT_RESULTS_PER_PAGE
    ): Result<SearchResponse> {
        localDataSource.insertOrUpdatePreviousSearch(searchTerm)
        return safeApiCall { searchApi.getSearchResults(searchTerm, count) }
    }

    suspend fun performLookup(searchTerm: String): Result<AutocompleteResponse> {
        return safeApiCall { autocompleteApi.getSuggestions(searchTerm) }
    }
}

