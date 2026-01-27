package uk.gov.govuk.data.flex.remote

import retrofit2.Response
import retrofit2.http.Header
import retrofit2.http.POST
import uk.gov.govuk.data.flex.model.FlexPreferencesResponse

interface FlexApi {

    @POST("1.0/app/user")
    suspend fun getFlexPreferences(
        @Header("Authorization") accessToken: String
    ): Response<FlexPreferencesResponse>
}
