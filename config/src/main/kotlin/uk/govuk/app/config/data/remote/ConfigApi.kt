package uk.govuk.app.config.data.remote

import retrofit2.Response
import retrofit2.http.GET

interface ConfigApi {
    @GET("config/appinfo/android")
    suspend fun getConfig(): Response<String>
}
