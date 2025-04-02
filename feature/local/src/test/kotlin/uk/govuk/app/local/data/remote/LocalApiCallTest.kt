package uk.govuk.app.local.data.remote

import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import uk.gov.govuk.data.model.Result.Success
import uk.govuk.app.local.data.LocalRepo
import uk.govuk.app.local.data.remote.model.ApiResponse
import uk.govuk.app.local.data.remote.model.LocalAuthority
import java.net.UnknownHostException

class LocalApiCallTest {
    private val localApi = mockk<LocalApi>(relaxed = true)
    private val apiResponse = mockk<Response<ApiResponse>>()
    private lateinit var localRepo: LocalRepo

    private val responseWithUnitaryResult = ApiResponse.LocalAuthorityResponse(
        localAuthority = LocalAuthority(
            name = "name",
            homePageUrl = "homePageUrl",
            tier = "unitary",
            slug = "slug"
        )
    )

    @Before
    fun setup() {
        localRepo = LocalRepo(localApi)
    }

    @Test
    fun `Successful API call with 200 status code and non null body`() {
        coEvery {
            localApi.getLocalPostcode("E18QS")
        } returns Response.success(responseWithUnitaryResult)
        every { apiResponse.code() } returns 200
        every { apiResponse.body() } returns responseWithUnitaryResult

        runTest {
            val actual = safeLocalApiCall(
                { localApi.getLocalPostcode("E18QS") }
            )
            println(actual)
            assertEquals(Success(responseWithUnitaryResult), actual)
        }
    }

    @Test
    fun `Successful API call with custom safe status code and non null body`() {
        coEvery {
            localApi.getLocalPostcode("E18QS")
        } returns Response.success(responseWithUnitaryResult)
        every { apiResponse.code() } returns 500
        every { apiResponse.body() } returns responseWithUnitaryResult

        runTest {
            val actual = safeLocalApiCall(
                { localApi.getLocalPostcode("E18QS") },
                intArrayOf(500)
            )
            assertEquals(Success(responseWithUnitaryResult), actual)
        }
    }

    @Test
    fun `Successful API call with 200 status code and null body`() {
        coEvery {
            localApi.getLocalPostcode("E18QS")
        } returns Response.success(
            ApiResponse.MessageResponse(message = "message")
        )
        every { apiResponse.code() } returns 200
        every { apiResponse.body() } returns null

        runTest {
            val actual = safeLocalApiCall(
                { localApi.getLocalPostcode("E18QS") }
            )

            assertEquals(
                Success(ApiResponse.MessageResponse(message = "message")),
                actual
            )
        }
    }

    @Test
    fun `Successful API call with custom safe status code and null body`() {
        coEvery {
            localApi.getLocalPostcode("E18QS")
        } returns Response.success(
            ApiResponse.MessageResponse(message = "message")
        )
        every { apiResponse.code() } returns 500
        every { apiResponse.body() } returns null

        runTest {
            val actual = safeLocalApiCall(
                { localApi.getLocalPostcode("E18QS") },
                intArrayOf(500)
            )
            assertEquals(
                Success(ApiResponse.MessageResponse(message = "message")),
                actual
            )
        }
    }

    @Test
    fun `API call returns a non safe status code`() {
        coEvery {
            localApi.getLocalPostcode("E18QS")
        }
        every { apiResponse.code() } returns 500

        runTest {
            val actual = safeLocalApiCall(
                { localApi.getLocalPostcode("E18QS") }
            )

            assertEquals("Error", actual.javaClass.kotlin.simpleName)
        }
    }

    @Test
    fun `API call throws UnknownHostException`() {
        val localApi = mockk<LocalApi>(relaxed = true)

        coEvery {
            localApi.getLocalPostcode("E18QS")
        } throws UnknownHostException()

        runTest {
            val actual = safeLocalApiCall(
                { localApi.getLocalPostcode("E18QS") }
            )

            assertEquals("DeviceOffline", actual.javaClass.kotlin.simpleName)
        }
    }

    @Test
    fun `API call throws a generic Exception`() {
        coEvery {
            localApi.getLocalPostcode("E18QS")
        } throws Exception()

        runTest {
            val actual = safeLocalApiCall(
                { localApi.getLocalPostcode("E18QS") }
            )

            assertEquals("Error", actual.javaClass.kotlin.simpleName)
        }
    }
}
