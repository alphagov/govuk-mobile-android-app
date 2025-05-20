package uk.gov.govuk.notifications

import android.content.Context
import android.content.Intent
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Test

class NotificationsSettingsProviderTest {
    private val context = mockk<Context>(relaxed = true)
    private val intent = mockk<Intent>(relaxed = true)

    @Test
    fun openDeviceSettingsTest() {
        runTest {
            openDeviceSettings(context, intent)

            verify(exactly = 1) {
                context.startActivity(intent)
            }
        }
    }
}
