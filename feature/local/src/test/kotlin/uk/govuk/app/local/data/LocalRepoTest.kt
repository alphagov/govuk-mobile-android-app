package uk.govuk.app.local.data

import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.experimental.runners.Enclosed
import org.junit.runner.RunWith
import retrofit2.Response
import uk.gov.govuk.data.model.Result.Success
import uk.govuk.app.local.data.remote.LocalApi
import uk.govuk.app.local.data.remote.model.Address
import uk.govuk.app.local.data.remote.model.ApiResponse
import uk.govuk.app.local.data.remote.model.RemoteLocalAuthority
import uk.govuk.app.local.data.local.LocalDataSource
import uk.govuk.app.local.data.local.LocalRealmProvider

@RunWith(Enclosed::class)
class LocalRepoTest {
    class GetLocalPostcodeTest {
        private val realmProvider = mockk<LocalRealmProvider>(relaxed = true)
        private val localApi = mockk<LocalApi>(relaxed = true)
        private val apiResponse = mockk<Response<ApiResponse>>()
        private lateinit var localRepo: LocalRepo

        @Before
        fun setup() {
            val localDataSource = LocalDataSource(realmProvider)
            localRepo = LocalRepo(localApi, localDataSource)
        }

        @Test
        fun `Perform postcode lookup returns a unitary local authority when postcode is valid and known`() {
            val unitaryResponse = ApiResponse(
                localAuthority = RemoteLocalAuthority(
                    name = "name",
                    homePageUrl = "homePageUrl",
                    tier = "unitary",
                    slug = "slug"
                ),
                addresses = null,
                message = null
            )

            coEvery {
                localApi.getLocalPostcode("E18QS")
            } returns apiResponse

            every { apiResponse.code() } returns 200
            every { apiResponse.isSuccessful } returns true
            every { apiResponse.body() } returns unitaryResponse

            runTest {
                val actual = localRepo.performGetLocalPostcode("E18QS")
                assertEquals(Success(unitaryResponse), actual)
            }
        }

        @Test
        fun `Perform postcode lookup returns a two-tier local authority when postcode is valid and known`() {
            val twoTierResponse = ApiResponse(
                localAuthority = RemoteLocalAuthority(
                    name = "name",
                    homePageUrl = "homePageUrl",
                    tier = "district",
                    slug = "slug",
                    parent = RemoteLocalAuthority(
                        name = "parent name",
                        homePageUrl = "parentHomePageUrl",
                        tier = "county",
                        slug = "slug"
                    )
                ),
                addresses = null,
                message = null
            )

            coEvery {
                localApi.getLocalPostcode("E18QS")
            } returns apiResponse

            every { apiResponse.code() } returns 200
            every { apiResponse.isSuccessful } returns true
            every { apiResponse.body() } returns twoTierResponse

            runTest {
                val actual = localRepo.performGetLocalPostcode("E18QS")
                assertEquals(Success(twoTierResponse), actual)
            }
        }

        @Test
        fun `Perform postcode lookup returns an address list when postcode is valid and known but ambiguous`() {
            val addressListResponse = ApiResponse(
                localAuthority = null,
                addresses = listOf(
                    Address(
                        address = "address1",
                        slug = "slug1",
                        name = "name1"
                    ),
                    Address(
                        address = "address2",
                        slug = "slug2",
                        name = "name2"
                    )
                ),
                message = null
            )

            coEvery {
                localApi.getLocalPostcode("E18QS")
            } returns apiResponse

            every { apiResponse.code() } returns 200
            every { apiResponse.isSuccessful } returns true
            every { apiResponse.body() } returns addressListResponse

            runTest {
                val actual = localRepo.performGetLocalPostcode("E18QS")
                assertEquals(Success(addressListResponse), actual)
            }
        }

        @Test
        fun `Perform postcode lookup returns a message when postcode is valid but not found`() {
            val notFoundResponse = ApiResponse(
                localAuthority = null,
                addresses = null,
                message = "Postcode not found"
            )

            coEvery {
                localApi.getLocalPostcode("SW1A1AA")
            } returns apiResponse

            every { apiResponse.code() } returns 404
            every { apiResponse.isSuccessful } returns true
            every { apiResponse.body() } returns notFoundResponse

            runTest {
                val actual = localRepo.performGetLocalPostcode("SW1A1AA")
                assertEquals(Success(notFoundResponse), actual)
            }
        }

        @Test
        fun `Perform postcode lookup returns a message when postcode is invalid`() {
            val postcodeInvalidResponse = ApiResponse(
                localAuthority = null,
                addresses = null,
                message = "Invalid postcode"
            )

            coEvery {
                localApi.getLocalPostcode("SW1")
            } returns apiResponse

            every { apiResponse.code() } returns 400
            every { apiResponse.isSuccessful } returns true
            every { apiResponse.body() } returns postcodeInvalidResponse

            runTest {
                val actual = localRepo.performGetLocalPostcode("SW1")
                assertEquals(Success(postcodeInvalidResponse), actual)
            }
        }
    }

    class GetStoredRemoteLocalAuthorityTest {
        private val realmProvider = mockk<LocalRealmProvider>(relaxed = true)
        private val localApi = mockk<LocalApi>(relaxed = true)
        private val apiResponse = mockk<Response<ApiResponse>>()
        private lateinit var localRepo: LocalRepo

        @Before
        fun setup() {
            val localDataSource = LocalDataSource(realmProvider)
            localRepo = LocalRepo(localApi, localDataSource)
        }

        @Test
        fun `Perform get local authority returns a unitary local authority`() {
            val unitaryResponse = ApiResponse(
                localAuthority = RemoteLocalAuthority(
                    name = "name",
                    homePageUrl = "homePageUrl",
                    tier = "unitary",
                    slug = "slug"
                ),
                addresses = null,
                message = null
            )

            coEvery {
                localApi.getLocalAuthority("slug")
            } returns apiResponse

            every { apiResponse.code() } returns 200
            every { apiResponse.isSuccessful } returns true
            every { apiResponse.body() } returns unitaryResponse

            runTest {
                val actual = localRepo.performGetLocalAuthority("slug")
                assertEquals(Success(unitaryResponse), actual)
            }
        }

        @Test
        fun `Perform get local authority returns a two-tier local authority`() {
            val twoTierResponse = ApiResponse(
                localAuthority = RemoteLocalAuthority(
                    name = "name",
                    homePageUrl = "homePageUrl",
                    tier = "district",
                    slug = "slug",
                    parent = RemoteLocalAuthority(
                        name = "parent name",
                        homePageUrl = "parentHomePageUrl",
                        tier = "county",
                        slug = "slug"
                    )
                ),
                addresses = null,
                message = null
            )

            coEvery {
                localApi.getLocalAuthority("slug")
            } returns apiResponse

            every { apiResponse.code() } returns 200
            every { apiResponse.isSuccessful } returns true
            every { apiResponse.body() } returns twoTierResponse

            runTest {
                val actual = localRepo.performGetLocalAuthority("slug")
                assertEquals(Success(twoTierResponse), actual)
            }
        }
    }
}
