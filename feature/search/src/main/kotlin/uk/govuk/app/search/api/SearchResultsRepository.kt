package uk.govuk.app.search.api

import uk.govuk.app.search.api_result.Results

class SearchResultsRepository {
    private val searchResultsService = SearchResultsRetrofitInstance.searchResultsService

    suspend fun getSearchResults(searchTerm: String, count: Int = 10): Results {
        return searchResultsService.getSearchResults(searchTerm, count)
    }
}
