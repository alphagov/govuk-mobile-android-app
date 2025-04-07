package uk.govuk.app.local.data

import uk.gov.govuk.data.model.Result
import uk.govuk.app.local.data.remote.LocalApi
import uk.govuk.app.local.data.remote.model.ApiResponse
import uk.govuk.app.local.data.remote.safeLocalApiCall
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class LocalRepo @Inject constructor(
    private val localApi: LocalApi
) {
    suspend fun performGetLocalPostcode(
        postcode: String
    ): Result<ApiResponse> {
        return safeLocalApiCall { localApi.getLocalPostcode(postcode) }
    }

    suspend fun performGetLocalAuthority(
        slug: String
    ): Result<ApiResponse> {
        return safeLocalApiCall { localApi.getLocalAuthority(slug) }
    }
}
