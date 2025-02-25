package uk.govuk.app.local.data

import uk.govuk.app.data.model.Result
import uk.govuk.app.data.remote.safeApiCall
import uk.govuk.app.local.data.remote.LocalApi
import uk.govuk.app.local.data.remote.model.LocalLinksManagerApiResponse
import uk.govuk.app.local.data.remote.model.LocationsApiResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class LocalRepo @Inject constructor(
    private val localApi: LocalApi
) {
    suspend fun performLocationsApiCall(
        postcode: String
    ): Result<LocationsApiResponse> {
        return safeApiCall { localApi.getLocalCustodianCode(postcode) }
    }

    suspend fun performLocalAuthorityApiCall(
        localCustodianCode: String
    ): Result<LocalLinksManagerApiResponse> {
        return safeApiCall { localApi.getLocalAuthority(localCustodianCode) }
    }
}
