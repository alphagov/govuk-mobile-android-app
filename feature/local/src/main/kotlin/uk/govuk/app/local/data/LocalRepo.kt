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
        if (response is Result.Success) {
            response.value.localAuthority?.let { localAuthority ->
                storeLocalAuthorities(
                    child = localAuthority,
                    parent = localAuthority.parent
                )
            }
        }
    }

    private suspend fun storeLocalAuthorities(child: LocalAuthority, parent: LocalAuthority?) {
        localDataSource.insertOrReplace(
            child.name,
            child.homePageUrl,
            child.slug,
            parent?.name,
            parent?.homePageUrl,
            parent?.slug
        )
    }
}
