package uk.govuk.app.local.data.remote

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import uk.gov.govuk.data.model.Result.Success
import uk.govuk.app.local.data.remote.model.ApiResponse
import uk.govuk.app.local.data.remote.model.RemoteLocalAuthority
import uk.govuk.app.local.domain.StatusCode
import java.net.UnknownHostException

class LocalApiCallTest {
    private val localApi = mockk<LocalApi>(relaxed = true)
    private lateinit var mockWebServer: MockWebServer

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `API call returns 200`() = runTest {
        val apiResponse = ApiResponse(
            localAuthority = RemoteLocalAuthority(
                name = "name",
                homePageUrl = "homePageUrl",
                tier = "unitary",
                slug = "slug"
            ),
            addresses = null,
            status = null
        )

        coEvery {
            localApi.getLocalPostcode("E18QS")
        } returns Response.success(apiResponse)

        val actual = safeLocalApiCall { localApi.getLocalPostcode("E18QS") }
        assertEquals(Success(apiResponse), actual)
    }

    @Test
    fun `API call returns 400`() = runTest {
        val apiResponse = ApiResponse(
            localAuthority = null,
            addresses = null,
            status = StatusCode.INVALID_POSTCODE
        )

        coEvery {
            localApi.getLocalPostcode("E18QS")
        } returns Response.error(400, "Error".toResponseBody(null))

        val actual = safeLocalApiCall { localApi.getLocalPostcode("E18QS") }
        assertEquals(Success(apiResponse), actual)
    }

    @Test
    fun `API call returns 404`() = runTest {
        val apiResponse = ApiResponse(
            localAuthority = null,
            addresses = null,
            status = StatusCode.POSTCODE_NOT_FOUND
        )

        coEvery {
            localApi.getLocalPostcode("E18QS")
        } returns Response.error(404, "Error".toResponseBody(null))

        val actual = safeLocalApiCall { localApi.getLocalPostcode("E18QS") }
        assertEquals(Success(apiResponse), actual)
    }

    @Test
    fun `API call returns 500`() = runTest {
        coEvery {
            localApi.getLocalPostcode("E18QS")
        } returns Response.error(500, "Error".toResponseBody(null))

        val actual = safeLocalApiCall { localApi.getLocalPostcode("E18QS") }
        assertEquals("Error", actual.javaClass.kotlin.simpleName)
    }

    @Test
    fun `API call throws UnknownHostException`() = runTest {
        coEvery {
            localApi.getLocalPostcode("E18QS")
        } throws UnknownHostException()

        val actual = safeLocalApiCall { localApi.getLocalPostcode("E18QS") }
        assertEquals("DeviceOffline", actual.javaClass.kotlin.simpleName)
    }

    @Test
    fun `API call throws Exception`() = runTest {
        coEvery {
            localApi.getLocalPostcode("E18QS")
        } throws Exception()

        val actual = safeLocalApiCall { localApi.getLocalPostcode("E18QS") }
        assertEquals("Error", actual.javaClass.kotlin.simpleName)
    }
}
