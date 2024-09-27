package uk.govuk.app.search.api

import retrofit2.http.GET
import retrofit2.http.Query
import uk.govuk.app.search.api_result.SearchResponse

interface SearchApi {
    @GET("/api/search.json")
    suspend fun getSearchResults(
        @Query("q") searchTerm: String,
        @Query("count") count: Int
    ): SearchResponse
}
