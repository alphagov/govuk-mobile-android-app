package uk.govuk.app.notifications

import android.content.Context
import com.onesignal.OneSignal
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.unmockkObject
import io.mockk.unmockkStatic
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class NotificationsClientTest {
    private val dispatcher = UnconfinedTestDispatcher()
    private val context = mockk<Context>(relaxed = true)

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)

        mockkStatic(OneSignal::class)
        mockkObject(OneSignal.Debug)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()

        unmockkStatic(OneSignal::class)
        unmockkObject(OneSignal.Debug)
    }

    @Test
    fun `Given we have a notifications client, when initialise is called, One Signal setup functions are called`() {
        val oneSignalAppId = "4c235189-5c5f-4a71-8385-2549fc36419f"
        every { OneSignal.initWithContext(context, oneSignalAppId) } returns Unit
        every { OneSignal.Notifications.canRequestPermission } returns true

        val sut = NotificationsClient()
        sut.initialise(context)

        verify {
            OneSignal.initWithContext(context, oneSignalAppId)
        }
        coVerify {
            OneSignal.Notifications.requestPermission(false)
        }
    }
}
