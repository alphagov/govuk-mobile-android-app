package uk.govuk.app.local.data

import kotlinx.coroutines.flow.map
import uk.gov.govuk.data.model.Result
import uk.govuk.app.local.data.local.LocalDataSource
import uk.govuk.app.local.data.remote.LocalApi
import uk.govuk.app.local.data.remote.model.ApiResponse
import uk.govuk.app.local.data.remote.safeLocalApiCall
import uk.govuk.app.local.domain.model.LocalAuthority
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class LocalRepo @Inject constructor(
    private val localApi: LocalApi,
    private val localDataSource: LocalDataSource
) {
    val localAuthority = localDataSource.localAuthority.map {
        it?.let { storedLocalAuthority ->
            LocalAuthority(
                name = storedLocalAuthority.name,
                url = storedLocalAuthority.url,
                slug = storedLocalAuthority.slug,
                parent = storedLocalAuthority.parent ?.let { parent ->
                    LocalAuthority(
                        name = parent.name,
                        url = parent.url,
                        slug = parent.slug
                    )
                }
            )
        }
    }

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
                localDataSource.insertOrReplace(localAuthority)
            }
        }
    }
}
