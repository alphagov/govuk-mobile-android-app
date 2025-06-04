package uk.govuk.app.local.data

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import uk.gov.govuk.data.model.Result
import uk.govuk.app.local.data.local.LocalDataSource
import uk.govuk.app.local.data.remote.LocalApi
import uk.govuk.app.local.data.remote.model.RemoteAddress
import uk.govuk.app.local.data.remote.model.LocalAuthorityResponse
import uk.govuk.app.local.data.remote.model.RemoteLocalAuthority

class LocalRepoTest {
    private val localApi = mockk<LocalApi>(relaxed = true)
    private val localDataSource = mockk<LocalDataSource>(relaxed = true)
    private val apiResponse = mockk<Response<LocalAuthorityResponse>>()
    private lateinit var localRepo: LocalRepo

    @Before
    fun setup() {
        localRepo = LocalRepo(localApi, localDataSource)
    }

    @Test
    fun `Perform postcode lookup returns an unsuccessful response`() = runTest {
        coEvery {
            localApi.fromPostcode("SW1")
        } returns apiResponse

        every { apiResponse.code() } returns 500
        every { apiResponse.isSuccessful } returns false

        val actual = localRepo.fetchLocalAuthority("SW1")
        assertTrue(actual is Result.Error)

        coVerify(exactly = 0) {
            localDataSource.insertOrReplace(any())
        }
    }

    @Test
    fun `Perform get local authority returns an unsuccessful response`() = runTest {
        coEvery {
            localApi.fromSlug("slug")
        } returns apiResponse

        every { apiResponse.code() } returns 500
        every { apiResponse.isSuccessful } returns false

        val actual = localRepo.performGetLocalAuthority("slug")
        assertTrue(actual is Result.Error)

        coVerify(exactly = 0) {
            localDataSource.insertOrReplace(any())
        }
    }

    @Test
    fun `Caching addresses correctly stores addresses, extracts slugs, and fetches authorities`() = runTest {
        val addresses = listOf(
            RemoteAddress("1 Test Street, AB1C1DE", "slug-one", "Slug One"),
            RemoteAddress("2 Test Street, AB1C1DE", "slug-two", "Slug Two"),
            RemoteAddress("3 Test Street, AB1C1DE", "slug-one", "Slug One")
        )
        val remoteLocalAuthority1 = RemoteLocalAuthority("Authority 1", "url1", "unitary", "slug-one")
        val remoteLocalAuthority2 = RemoteLocalAuthority("Authority 2", "url2", "unitary", "slug-two")

        coEvery {
            localApi.fromSlug("slug-one")
        } returns Response.success(
            LocalAuthorityResponse(remoteLocalAuthority1, addresses = null)
        )
        coEvery {
            localApi.fromSlug("slug-two")
        } returns Response.success(
            LocalAuthorityResponse(remoteLocalAuthority2, addresses = null)
        )

        localRepo.cacheAddresses(addresses)

        assertEquals(addresses, localRepo.addresses)
        assertEquals(
            listOf(remoteLocalAuthority1, remoteLocalAuthority2),
            localRepo.localAuthorities
        )

        coVerify(exactly = 1) { localApi.fromSlug("slug-one") }
        coVerify(exactly = 1) { localApi.fromSlug("slug-two") }
    }


    @Test
    fun `Caching addresses handles an empty list`() = runTest {
        localRepo.cacheAddresses(emptyList())

        assertEquals(emptyList<RemoteAddress>(), localRepo.addresses)
        assertEquals(emptyList<RemoteLocalAuthority>(), localRepo.localAuthorities)

        coVerify(exactly = 0) { localApi.fromSlug(any()) }
    }

    @Test
    fun `Cache addresses handles a null localAuthority in the API response`() = runTest {
        val addresses = listOf(
            RemoteAddress("1 Test Street, AB1C1DE", "slug-one", "Slug One"),
        )

        coEvery {
            localApi.fromSlug("slug-one")
        } returns Response.success(
            LocalAuthorityResponse(localAuthority = null, addresses = null)
        )

        localRepo.cacheAddresses(addresses)

        assertEquals(addresses, localRepo.addresses)
        assertEquals(emptyList<RemoteLocalAuthority>(), localRepo.localAuthorities)

        coVerify(exactly = 1) { localApi.fromSlug("slug-one") }
    }

    @Test
    fun `Clear clears local data source`() {
        runTest {
            localRepo.clear()

            coVerify { localDataSource.clear() }
        }
    }
}
