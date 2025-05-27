package uk.gov.govuk.login.data.remote

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header

interface LoginApi {
    @GET("staging")
    suspend fun get(
        @Header("Authorization") token: String
    ): Response<String>
}
