package uk.govuk.app.local.data.remote

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import uk.govuk.app.local.data.remote.model.LocalLinksManagerApiResponse
import uk.govuk.app.local.data.remote.model.LocationsApiResponse

interface LocalApi {
    @GET("/v1/locations")
    suspend fun getLocalCustodianCode(
        @Query("postcode") postcode: String
    ): Response<LocationsApiResponse>

    @GET("/api/local-authority")
    suspend fun getLocalAuthority(
        @Query("local_custodian_code") localCustodianCode: String
    ): Response<LocalLinksManagerApiResponse>
}
