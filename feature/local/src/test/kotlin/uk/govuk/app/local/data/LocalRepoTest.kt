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
import org.junit.experimental.runners.Enclosed
import org.junit.runner.RunWith
import retrofit2.Response
import uk.gov.govuk.data.model.Result
import uk.gov.govuk.data.model.Result.Success
import uk.govuk.app.local.data.local.LocalDataSource
import uk.govuk.app.local.data.remote.LocalApi
import uk.govuk.app.local.data.remote.model.Address
import uk.govuk.app.local.data.remote.model.ApiResponse
import uk.govuk.app.local.data.remote.model.RemoteLocalAuthority
import uk.govuk.app.local.domain.StatusCode

@RunWith(Enclosed::class)
class LocalRepoTest {
    class GetLocalPostcodeTest {
        private val localApi = mockk<LocalApi>(relaxed = true)
        private val localDataSource = mockk<LocalDataSource>(relaxed = true)
        private val apiResponse = mockk<Response<ApiResponse>>()
        private lateinit var localRepo: LocalRepo

        @Before
        fun setup() {
            localRepo = LocalRepo(localApi, localDataSource)
        }

        @Test
        fun `Perform postcode lookup returns a unitary local authority when postcode is valid and known`() {
            val remoteLocalAuthority = RemoteLocalAuthority(
                name = "name",
                homePageUrl = "homePageUrl",
                tier = "unitary",
                slug = "slug"
            )
            val unitaryResponse = ApiResponse(
                localAuthority = remoteLocalAuthority,
                addresses = null,
                status = null
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

            coVerify {
                localDataSource.insertOrReplace(remoteLocalAuthority)
            }
        }

        @Test
        fun `Perform postcode lookup returns a two-tier local authority when postcode is valid and known`() {
            val remoteLocalAuthority = RemoteLocalAuthority(
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
            )
            val twoTierResponse = ApiResponse(
                localAuthority = remoteLocalAuthority,
                addresses = null,
                status = null
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

            coVerify {
                localDataSource.insertOrReplace(remoteLocalAuthority)
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
                status = null
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

            coVerify(exactly = 0) {
                localDataSource.insertOrReplace(any())
            }
        }

        @Test
        fun `Perform postcode lookup returns a message when postcode is valid but not found`() {
            val notFoundResponse = ApiResponse(
                localAuthority = null,
                addresses = null,
                status = StatusCode.POSTCODE_NOT_FOUND
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

            coVerify(exactly = 0) {
                localDataSource.insertOrReplace(any())
            }
        }

        @Test
        fun `Perform postcode lookup returns a message when postcode is invalid`() {
            val postcodeInvalidResponse = ApiResponse(
                localAuthority = null,
                addresses = null,
                status = StatusCode.INVALID_POSTCODE
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

            coVerify(exactly = 0) {
                localDataSource.insertOrReplace(any())
            }
        }

        @Test
        fun `Perform postcode lookup returns an unsuccessful response`() {
            coEvery {
                localApi.getLocalPostcode("SW1")
            } returns apiResponse

            every { apiResponse.code() } returns 500
            every { apiResponse.isSuccessful } returns false

            runTest {
                val actual = localRepo.performGetLocalPostcode("SW1")
                assertTrue(actual is Result.Error)
            }

            coVerify(exactly = 0) {
                localDataSource.insertOrReplace(any())
            }
        }
    }

    class GetLocalAuthorityTest {
        private val localApi = mockk<LocalApi>(relaxed = true)
        private val localDataSource = mockk<LocalDataSource>(relaxed = true)
        private val apiResponse = mockk<Response<ApiResponse>>()
        private lateinit var localRepo: LocalRepo

        @Before
        fun setup() {
            localRepo = LocalRepo(localApi, localDataSource)
        }

        @Test
        fun `Perform get local authority returns a unitary local authority`() {
            val remoteLocalAuthority = RemoteLocalAuthority(
                name = "name",
                homePageUrl = "homePageUrl",
                tier = "unitary",
                slug = "slug"
            )
            val unitaryResponse = ApiResponse(
                localAuthority = remoteLocalAuthority,
                addresses = null,
                status = null
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

            coVerify {
                localDataSource.insertOrReplace(remoteLocalAuthority)
            }
        }

        @Test
        fun `Perform get local authority returns a two-tier local authority`() {
            val remoteLocalAuthority = RemoteLocalAuthority(
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
            )
            val twoTierResponse = ApiResponse(
                localAuthority = remoteLocalAuthority,
                addresses = null,
                status = null
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

            coVerify {
                localDataSource.insertOrReplace(remoteLocalAuthority)
            }
        }

        @Test
        fun `Perform get local authority returns an unsuccessful response`() {
            coEvery {
                localApi.getLocalAuthority("slug")
            } returns apiResponse

            every { apiResponse.code() } returns 500
            every { apiResponse.isSuccessful } returns false

            runTest {
                val actual = localRepo.performGetLocalAuthority("slug")
                assertTrue(actual is Result.Error)
            }

            coVerify(exactly = 0) {
                localDataSource.insertOrReplace(any())
            }
        }
    }
}
