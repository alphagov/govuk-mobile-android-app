package uk.govuk.app.local.data

import uk.gov.govuk.data.model.Result
import uk.govuk.app.local.data.remote.LocalApi
import uk.govuk.app.local.data.remote.model.ApiResponse
import uk.govuk.app.local.data.remote.model.LocalAuthority
import uk.govuk.app.local.data.remote.safeLocalApiCall
import uk.govuk.app.local.data.store.LocalDataSource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class LocalRepo @Inject constructor(
    private val localApi: LocalApi,
    private val localDataSource: LocalDataSource
) {
    suspend fun performGetLocalPostcode(
        postcode: String
    ): Result<ApiResponse> {
        val response = safeLocalApiCall { localApi.getLocalPostcode(postcode) }
        processResponse(response)
        return response
    }

    suspend fun performGetLocalAuthority(
        slug: String
    ): Result<ApiResponse> {
        val response = safeLocalApiCall { localApi.getLocalAuthority(slug) }
        processResponse(response)
        return response
    }

    private suspend fun processResponse(response: Result<ApiResponse>) {
        if (response is Result.Success && response.value.localAuthority != null) {
            val localAuthority = response.value.localAuthority

            var parentLocalAuthority: LocalAuthority? = null
            if (localAuthority?.parent != null) {
                parentLocalAuthority = localAuthority.parent
            }

            storeLocalAuthorities(
                localAuthorities = arrayListOf(
                    localAuthority,
                    parentLocalAuthority
                )
            )
        }
    }

    private suspend fun storeLocalAuthorities(localAuthorities: List<LocalAuthority?>) {
        if (localAuthorities.isNotEmpty()) {
            localDataSource.insertOrReplace(
                localAuthorities[0]?.name ?: "",
                localAuthorities[0]?.homePageUrl ?: "",
                localAuthorities[0]?.slug ?: "",
                localAuthorities[1]?.name ?: "",
                localAuthorities[1]?.homePageUrl ?: "",
                localAuthorities[1]?.slug ?: ""
            )
        }
    }
}
