package uk.govuk.app.search.data.remote

import retrofit2.http.GET
import retrofit2.http.Query
import uk.govuk.app.search.data.remote.model.SearchResponse
import uk.govuk.app.search.domain.SearchConfig

interface SearchApi {
    @GET(SearchConfig.SEARCH_PATH)
    suspend fun getSearchResults(
        @Query("q") searchTerm: String,
        @Query("count") count: Int
    ): SearchResponse
}
