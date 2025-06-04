package uk.govuk.app.local.data

import kotlinx.coroutines.flow.map
import uk.gov.govuk.data.model.Result
import uk.govuk.app.local.data.local.LocalDataSource
import uk.govuk.app.local.data.remote.LocalApi
import uk.govuk.app.local.data.remote.model.LocalAuthorityResult
import uk.govuk.app.local.data.remote.model.RemoteAddress
import uk.govuk.app.local.data.remote.model.RemoteLocalAuthority
import uk.govuk.app.local.data.remote.safeLocalApiCall
import uk.govuk.app.local.domain.model.Address
import uk.govuk.app.local.domain.model.LocalAuthority
import uk.govuk.app.local.domain.toAddress
import uk.govuk.app.local.domain.toLocalAuthority
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class LocalRepo @Inject constructor(
    private val localApi: LocalApi,
    private val localDataSource: LocalDataSource
) {
    val localAuthority = localDataSource.localAuthority.map {
        it?.toLocalAuthority()
    }

    private lateinit var _cachedLocalAuthority: LocalAuthority
    val cachedLocalAuthority
        get() = _cachedLocalAuthority

    private var _addresses: List<Address> = emptyList()
    val addresses: List<Address>
        get() = _addresses

    private var _localAuthorities: List<LocalAuthority> = emptyList()
    val localAuthorities: List<LocalAuthority>
        get() = _localAuthorities.toList()

    suspend fun fetchLocalAuthority(
        postcode: String
    ): Result<LocalAuthorityResult> {
        val result = safeLocalApiCall { localApi.getLocalPostcode(postcode) }

        ((result as? Result.Success)?.value
                as? LocalAuthorityResult.LocalAuthority)?.localAuthority?.let { remoteLocalAuthority ->
            _cachedLocalAuthority = remoteLocalAuthority.toLocalAuthority()
        }

        return result
    }

    suspend fun cacheAddresses(
        addresses: List<RemoteAddress>
    ) {
        val slugList = addresses.distinctBy { it.slug }.map { it.slug }

        val localAuthorities = emptyList<RemoteLocalAuthority>().toMutableList()
        slugList.forEach { slug ->
            val result = safeLocalApiCall { localApi.getLocalAuthority(slug) }

            ((result as? Result.Success)?.value
                as? LocalAuthorityResult.LocalAuthority)?.localAuthority?.let { localAuthority ->
                localAuthorities += localAuthority
            }
        }

        _addresses = addresses.map { it.toAddress() }
        _localAuthorities = localAuthorities.map { it.toLocalAuthority() }
    }

    fun cacheLocalAuthority(slug: String) {
        localAuthorities.find { it.slug == slug }?.let { localAuthority ->
            _cachedLocalAuthority = localAuthority
        }
    }

    suspend fun selectLocalAuthority() {
        localDataSource.insertOrReplace(cachedLocalAuthority)
    }

    suspend fun clear() {
        localDataSource.clear()
    }
}
