package uk.govuk.app.local.data

import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import uk.gov.govuk.data.model.Result.Success
import uk.govuk.app.local.data.remote.LocalApi
import uk.govuk.app.local.data.remote.model.Address
import uk.govuk.app.local.data.remote.model.ApiResponse
import uk.govuk.app.local.data.remote.model.LocalAuthority

class LocalRepoTest {
    private val localApi = mockk<LocalApi>(relaxed = true)
    private val apiResponse = mockk<Response<ApiResponse>>()
    private val localAuthorityResponse = mockk<Response<ApiResponse.LocalAuthorityResponse>>()
    private lateinit var localRepo: LocalRepo
    private val responseWithInvalid = ApiResponse.MessageResponse(
        message = "Invalid postcode"
    )
    private val responseWithNotKnown = ApiResponse.MessageResponse(
        message = "Postcode not found"
    )
    private val responseWithUnitaryResult = ApiResponse.LocalAuthorityResponse(
        localAuthority = LocalAuthority(
            name = "name",
            homePageUrl = "homePageUrl",
            tier = "unitary",
            slug = "slug"
        )
    )
    private val responseWithTwoTierResult = ApiResponse.LocalAuthorityResponse(
        localAuthority = LocalAuthority(
            name = "name",
            homePageUrl = "homePageUrl",
            tier = "district",
            slug = "slug",
            parent = LocalAuthority(
                name = "parent name",
                homePageUrl = "parentHomePageUrl",
                tier = "county",
                slug = "slug"
            )
        )
    )
    private val responseWithAddressListResult = ApiResponse.AddressListResponse(
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
        )
    )

    @Before
    fun setup() {
        localRepo = LocalRepo(localApi)
    }

    @Test
    fun `Perform postcode lookup returns a unitary local authority when postcode is valid and known`() {
        coEvery {
            localApi.getLocalPostcode("E18QS")
        } returns apiResponse

        every { apiResponse.code() } returns 200
        every { apiResponse.body() } returns responseWithUnitaryResult

        val expected = Success(responseWithUnitaryResult)

        runTest {
            val actual = localRepo.performGetLocalPostcode("E18QS")
            assertEquals(expected, actual)
        }
    }

    @Test
    fun `Perform postcode lookup returns a two-tier local authority when postcode is valid and known`() {
        coEvery {
            localApi.getLocalPostcode("E18QS")
        } returns apiResponse

        every { apiResponse.code() } returns 200
        every { apiResponse.body() } returns responseWithTwoTierResult

        val expected = Success(responseWithTwoTierResult)

        runTest {
            val actual = localRepo.performGetLocalPostcode("E18QS")
            assertEquals(expected, actual)
        }
    }

    @Test
    fun `Perform postcode lookup returns an address list when postcode is valid and known but ambiguous`() {
        coEvery {
            localApi.getLocalPostcode("E18QS")
        } returns apiResponse

        every { apiResponse.code() } returns 200
        every { apiResponse.body() } returns responseWithAddressListResult

        val expected = Success(responseWithAddressListResult)

        runTest {
            val actual = localRepo.performGetLocalPostcode("E18QS")
            assertEquals(expected, actual)
        }
    }

    @Test
    fun `Perform postcode lookup returns a message when postcode is valid but not known`() {
        coEvery {
            localApi.getLocalPostcode("SW1A1AA")
        } returns apiResponse

        every { apiResponse.code() } returns 404
        every { apiResponse.body() } returns responseWithNotKnown

        val expected = Success(responseWithNotKnown)

        runTest {
            val actual = localRepo.performGetLocalPostcode("SW1A1AA")
            assertEquals(expected, actual)
        }
    }

    @Test
    fun `Perform postcode lookup returns a message when postcode is invalid`() {
        coEvery {
            localApi.getLocalPostcode("SW1")
        } returns apiResponse
        every {  apiResponse.isSuccessful } returns true

        every { apiResponse.code() } returns 404
        every { apiResponse.body() } returns responseWithInvalid

        val expected = Success(responseWithInvalid)

        runTest {
            val actual = localRepo.performGetLocalPostcode("SW1")
            assertEquals(expected, actual)
        }
    }

    @Test
    fun `Perform get local authority returns a unitary local authority`() {
        coEvery {
            localApi.getLocalAuthority("slug")
        } returns localAuthorityResponse
        every {  localAuthorityResponse.isSuccessful } returns true
        every { localAuthorityResponse.body() } returns responseWithUnitaryResult

        val expected = Success(responseWithUnitaryResult)

        runTest {
            val actual = localRepo.performGetLocalAuthority("slug")
            assertEquals(expected, actual)
        }
    }

    @Test
    fun `Perform get local authority returns a two-tier local authority`() {
        coEvery {
            localApi.getLocalAuthority("slug")
        } returns localAuthorityResponse
        every {  localAuthorityResponse.isSuccessful } returns true
        every { localAuthorityResponse.body() } returns responseWithTwoTierResult

        val expected = Success(responseWithTwoTierResult)

        runTest {
            val actual = localRepo.performGetLocalAuthority("slug")
            assertEquals(expected, actual)
        }
    }
}
