package uk.govuk.app.config.data.remote

import retrofit2.Response
import retrofit2.http.GET
import uk.govuk.app.config.data.remote.model.ConfigResponse

interface ConfigApi {
    @GET("appinfo/android")
    suspend fun getConfig(): Response<ConfigResponse>
}