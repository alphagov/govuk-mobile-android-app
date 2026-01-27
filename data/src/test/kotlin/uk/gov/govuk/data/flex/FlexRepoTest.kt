package uk.gov.govuk.data.flex

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import uk.gov.govuk.data.flex.model.FlexPreferencesResponse
import uk.gov.govuk.data.flex.remote.FlexApi

class FlexRepoTest {

    private val flexApi = mockk<FlexApi>(relaxed = true)

    private lateinit var flexRepo: FlexRepo

    @Before
    fun setup() {
        flexRepo = FlexRepo(flexApi)
    }

    @Test
    fun `Given a flex preferences response, when response body is null, then get user id returns null`() {
        coEvery {
            flexApi.getFlexPreferences("Bearer 12345").body()
        } returns null

        runTest {
            val response = flexRepo.getUserId("12345")
            assertNull(response)
        }
    }

    @Test
    fun `Given a flex preferences response, when response body is not null, then get user id returns user id`() {
        coEvery {
            flexApi.getFlexPreferences("Bearer 12345").body()
        } returns FlexPreferencesResponse("userId")

        runTest {
            val response = flexRepo.getUserId("12345")
            assertEquals(response, "userId")
        }
    }
}
