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

    private lateinit var notificationsClient: NotificationsClient

    @Before
    fun setup() {
        notificationsClient = NotificationsClient()

        mockkStatic(OneSignal::class)
        mockkStatic(OneSignal.Debug::class)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `Given we have a notifications client, when initialise is called, One Signal initialise function is called`() {
        val oneSignalAppId = "1234"
        every { OneSignal.initWithContext(context, oneSignalAppId) } returns Unit

        runTest {
            notificationsClient.initialise(context, oneSignalAppId)

            verify(exactly = 1) {
                OneSignal.initWithContext(context, oneSignalAppId)
            }
        }
    }

    @Test
    fun `Given we have a notifications client, when request permission is called and permissions denied, One Signal request permission function is called`() {
        every { OneSignal.Notifications.canRequestPermission } returns true
        coEvery { OneSignal.Notifications.requestPermission(false) } returns true

        runTest {
            val dispatcher = UnconfinedTestDispatcher()
            notificationsClient.requestPermission(dispatcher)

            coVerify(exactly = 1) {
                OneSignal.Notifications.requestPermission(false)
            }
        }
    }

    @Test
    fun `Given we have a notifications client, when request permission is called and permissions granted, One Signal request permission function is called`() {
        every { OneSignal.Notifications.canRequestPermission } returns true
        coEvery { OneSignal.Notifications.requestPermission(false) } returns false

        runTest {
            val dispatcher = UnconfinedTestDispatcher()
            notificationsClient.requestPermission(dispatcher)

            coVerify(exactly = 1) {
                OneSignal.Notifications.requestPermission(false)
            }
        }
    }
}
