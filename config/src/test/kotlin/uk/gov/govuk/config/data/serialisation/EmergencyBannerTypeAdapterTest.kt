package uk.gov.govuk.config.data.serialisation

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Test
import uk.gov.govuk.config.data.remote.model.EmergencyBanner
import uk.gov.govuk.config.data.remote.model.EmergencyBannerType

class EmergencyBannerTypeAdapterTest {

    private lateinit var gson: Gson

    @Before
    fun setup() {
        gson = GsonBuilder()
            .registerTypeAdapter(EmergencyBannerType::class.java, EmergencyBannerTypeAdapter())
            .create()
    }

    @Test
    fun `Unknown EmergencyBannerType should be deserialized to INFORMATION`() {
        val json = """
            {
              "id": "1",
              "title": "Unknown Banner",
              "type": "invalid-type",
              "allowsDismissal": true
            }
        """.trimIndent()

        val result = gson.fromJson(json, EmergencyBanner::class.java)

        assertEquals(EmergencyBannerType.INFORMATION, result.type)
        assertEquals("1", result.id)
    }

    @Test
    fun `Empty EmergencyBannerType should be deserialized to INFORMATION`() {
        val json = """
            {
              "id": "test_empty_2",
              "title": "Empty Type Banner",
              "type": "",
              "allowsDismissal": true
            }
        """.trimIndent()

        val result = gson.fromJson(json, EmergencyBanner::class.java)

        assertEquals(EmergencyBannerType.INFORMATION, result.type)
    }

    @Test
    fun ` EmergencyBannerType should be deserialized to INFORMATION`() {
        val json = """
            {
              "id": "test_empty_2",
              "title": "Empty Type Banner",
              "allowsDismissal": true
            }
        """.trimIndent()

        val result = gson.fromJson(json, EmergencyBanner::class.java)

        assertEquals(EmergencyBannerType.INFORMATION, result.type)
    }
}