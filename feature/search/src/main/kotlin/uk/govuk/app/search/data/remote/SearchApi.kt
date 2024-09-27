package uk.govuk.app.search.data.remote

import retrofit2.http.GET
import retrofit2.http.Query
import uk.govuk.app.search.data.remote.model.SearchResponse

interface SearchApi {
    @GET("/api/search.json")
    suspend fun getSearchResults(
        @Query("q") searchTerm: String,
        @Query("count") count: Int
    ): SearchResponse
}
