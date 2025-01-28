package uk.govuk.app.notifications

import android.content.Context
import com.onesignal.OneSignal
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class NotificationsClientTest {
    private val context = mockk<Context>(relaxed = true)

    @Before
    fun setup() {
        mockkStatic(OneSignal::class)
        mockkStatic(OneSignal.Debug::class)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `Given we have a notifications client, when initialise is called, One Signal setup functions are called`() {
        val oneSignalAppId = "1234"
        every { OneSignal.initWithContext(context, oneSignalAppId) } returns Unit
        every { OneSignal.Notifications.canRequestPermission } returns true
        coEvery { OneSignal.Notifications.requestPermission(false) } returns true

        runTest {
            val dispatcher = UnconfinedTestDispatcher()
            val notificationsClient = NotificationsClient()
            notificationsClient.initialise(context, oneSignalAppId, dispatcher)

            verify(exactly = 1) {
                OneSignal.initWithContext(context, oneSignalAppId)
            }
            coVerify(exactly = 1) {
                OneSignal.Notifications.requestPermission(false)
            }
        }
    }
}
