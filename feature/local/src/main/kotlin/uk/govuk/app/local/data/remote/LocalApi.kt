package uk.govuk.app.local.data.remote

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import uk.govuk.app.local.data.remote.model.ApiResponse

interface LocalApi {
    @GET("query.json")
    suspend fun getLocalPostcode(
        @Query("postcode") postcode: String
    ) : Response<ApiResponse>

    @GET("{slug}.json")
    suspend fun getLocalAuthority(
        @Path("slug") slug: String
    ) : Response<ApiResponse>
}
