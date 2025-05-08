package uk.govuk.app.local.data

import kotlinx.coroutines.flow.map
import uk.gov.govuk.data.model.Result
import uk.govuk.app.local.data.local.LocalDataSource
import uk.govuk.app.local.data.remote.LocalApi
import uk.govuk.app.local.data.remote.model.Address
import uk.govuk.app.local.data.remote.model.LocalAuthorityResult
import uk.govuk.app.local.data.remote.model.RemoteLocalAuthority
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

    var addressList: List<Address> = emptyList()
    var localAuthorityList: List<RemoteLocalAuthority> = emptyList()

    suspend fun cacheAddresses(
        addresses: List<Address>
    ) {
        addressList = addresses
        val slugList = addresses.distinctBy { it.slug }.map { it.slug }

        val localAuthorities = emptyList<RemoteLocalAuthority>().toMutableList()
        slugList.forEach { slug ->
            val result = safeLocalApiCall { localApi.getLocalAuthority(slug) }

            ((result as? Result.Success)?.value
                as? LocalAuthorityResult.LocalAuthority)?.localAuthority?.let { localAuthority ->
                localAuthorities += localAuthority
            }
        }

        localAuthorityList = localAuthorities
    }

    suspend fun updateLocalAuthority(slug: String) {
        localAuthorityList.find { it.slug == slug }?.let { localAuthority ->
            localDataSource.insertOrReplace(localAuthority)
        }
    }

    suspend fun performGetLocalPostcode(
        postcode: String
    ): Result<LocalAuthorityResult> {
        val result = safeLocalApiCall { localApi.getLocalPostcode(postcode) }
        processResult(result)
        return result
    }

    suspend fun performGetLocalAuthority(
        slug: String
    ): Result<LocalAuthorityResult> {
        val result = safeLocalApiCall { localApi.getLocalAuthority(slug) }
        processResult(result)
        return result
    }

    private suspend fun processResult(result: Result<LocalAuthorityResult>) {
        ((result as? Result.Success)?.value
                as? LocalAuthorityResult.LocalAuthority)?.localAuthority?.let { localAuthority ->
            localDataSource.insertOrReplace(localAuthority)
        }
    }
}
