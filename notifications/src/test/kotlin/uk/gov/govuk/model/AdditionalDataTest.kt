package uk.gov.govuk.model

import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import uk.gov.govuk.notifications.model.asAdditionalData

class AdditionalDataTest {
    @Test
    fun `Given we have an empty String, When asAdditionalData() is called on it, then additional data should be null`() {
        val emptyString = ""

        runTest {
            val additionalData = emptyString.asAdditionalData()

            assertNull(additionalData)
        }
    }

    @Test
    fun `Given we have a valid JSON String containing a deeplink value, When asAdditionalData() is called on it, then deepLink should have the JSON value`() {
        val validJson = "{\"deeplink\":\"scheme://host\"}"

        runTest {
            val additionalData = validJson.asAdditionalData()

            assertEquals("scheme://host", additionalData?.deepLink)
        }
    }

    @Test
    fun `Given we have a malformed JSON String, When asAdditionalData() is called on it, then deepLink should be null`() {
        val malformedJson = "{deeplink\":\"scheme://host\"}"

        runTest {
            val additionalData = malformedJson.asAdditionalData()

            assertNull(additionalData?.deepLink)
        }
    }
}
