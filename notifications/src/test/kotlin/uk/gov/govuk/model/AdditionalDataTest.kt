package uk.gov.govuk.model

import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.json.JSONObject
import org.junit.Test
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import uk.gov.govuk.notifications.model.asAdditionalData

class AdditionalDataTest {
    private val additionalDataJson = mockk<JSONObject>()

    @Test
    fun `Given we have a JSON object that toString() returns an empty String, When asAdditionalData() is called on it, then additional data should be null`() {
        every { additionalDataJson.toString() } returns ""

        runTest {
            val additionalData = additionalDataJson.asAdditionalData()

            assertNull(additionalData)
        }
    }

    @Test
    fun `Given we have a JSON object containing a deeplink, When asAdditionalData() is called on it, then deepLink should have the JSON value`() {
        every { additionalDataJson.toString() } returns "{\"deeplink\":\"scheme://host\"}"

        runTest {
            val additionalData = additionalDataJson.asAdditionalData()

            assertEquals("scheme://host", additionalData?.deepLink)
        }
    }

    @Test
    fun `Given we have a JSON object that toString() returns malformed JSON, When asAdditionalData() is called on it, then deepLink should be null`() {
        every { additionalDataJson.toString() } returns "{deeplink\":\"scheme://host\"}"

        runTest {
            val additionalData = additionalDataJson.asAdditionalData()

            assertNull(additionalData?.deepLink)
        }
    }
}
