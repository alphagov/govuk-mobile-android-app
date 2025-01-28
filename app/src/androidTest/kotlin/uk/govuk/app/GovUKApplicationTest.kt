package uk.govuk.app

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import io.mockk.mockk
import io.mockk.verify
import org.junit.Test
import org.junit.runner.RunWith
import uk.govuk.app.notifications.NotificationsClient

@RunWith(AndroidJUnit4::class)
class GovUKApplicationTest {

    private val notificationsClient = mockk<NotificationsClient>(relaxed = true)

    @Test
    fun govUkApplicationOnCreate() {
        val govUkApplication: GovUkApplication =
            InstrumentationRegistry.getInstrumentation().targetContext.applicationContext as GovUkApplication
        govUkApplication.notificationsClient = notificationsClient
        govUkApplication.onCreate()

        verify(exactly = 1) {
            notificationsClient.initialise(govUkApplication, BuildConfig.ONE_SIGNAL_APP_ID)
        }
    }
}
