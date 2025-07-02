package uk.gov.govuk

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import io.mockk.mockk
import io.mockk.verify
import org.junit.Test
import org.junit.runner.RunWith
import uk.gov.govuk.notifications.NotificationsClient

@RunWith(AndroidJUnit4::class)
class GovUKApplicationTest {

    private val notificationsClient = mockk<NotificationsClient>(relaxed = true)

    @Test
    fun given_the_GovUKApplication_is_launched_When_onCreate_is_called_then_the_notifications_client_functions_are_called_once() {
        val govUkApplication: GovUkApplication =
            InstrumentationRegistry.getInstrumentation().targetContext.applicationContext as GovUkApplication
        govUkApplication.notificationsClient = notificationsClient
        govUkApplication.onCreate()

        verify(exactly = 1) {
            notificationsClient.initialise(govUkApplication, BuildConfig.ONE_SIGNAL_APP_ID)

            notificationsClient.addClickListener(govUkApplication)
        }
    }
}
