package uk.govuk.app.search.api

import uk.govuk.app.search.api_result.Results

class SearchResultsRepository {
    private val searchResultsService = SearchResultsRetrofitInstance.searchResultsService

    suspend fun getSearchResults(
        searchTerm: String, count: Int = SearchConfig.RESULTS_COUNT
    ): Results {
        return searchResultsService.getSearchResults(searchTerm, count)
    }
}
