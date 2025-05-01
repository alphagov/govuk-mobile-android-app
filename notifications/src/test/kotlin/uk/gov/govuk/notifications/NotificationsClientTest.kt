package uk.gov.govuk.notifications

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.net.Uri
import com.onesignal.OneSignal
import com.onesignal.notifications.INotification
import com.onesignal.notifications.INotificationClickEvent
import com.onesignal.notifications.INotificationClickListener
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.spyk
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.json.JSONObject
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
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
        mockkStatic(Uri::class)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `Given we have a notifications client, when initialise is called, then One Signal initialise function is called`() {
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
    fun `Given we have a notifications client, when request permission is called and permissions denied, then One Signal request permission function is called and consent given is true`() {
        every { OneSignal.Notifications.canRequestPermission } returns true
        coEvery { OneSignal.Notifications.requestPermission(false) } returns true

        runTest {
            val dispatcher = UnconfinedTestDispatcher()
            notificationsClient.requestPermission(dispatcher)

            coVerify(exactly = 1) {
                OneSignal.Notifications.requestPermission(false)
            }
            assertTrue(OneSignal.consentGiven)
        }
    }

    @Test
    fun `Given we have a notifications client, when request permission is called and permissions granted, then One Signal request permission function is called and consent given is false`() {
        every { OneSignal.Notifications.canRequestPermission } returns true
        coEvery { OneSignal.Notifications.requestPermission(false) } returns false

        runTest {
            val dispatcher = UnconfinedTestDispatcher()
            notificationsClient.requestPermission(dispatcher)

            coVerify(exactly = 1) {
                OneSignal.Notifications.requestPermission(false)
            }
            assertFalse(OneSignal.consentGiven)
        }
    }

    @Test
    fun `Given we have a notifications client, when give consent is called, then One Signal consent given is true`() {
        runTest {
            notificationsClient.giveConsent()

            assertTrue(OneSignal.consentGiven)
        }
    }

    @Test
    fun `Given we have a notifications client, when add click listener is called, then the correct functions are called`() {
        val uri = mockk<Uri>()
        val event = mockk<INotificationClickEvent>()
        val notification = mockk<INotification>()
        val clickListener = slot<INotificationClickListener>()
        every { Uri.parse("") } returns uri
        every { event.notification.additionalData } returns null
        every { event.notification.additionalData.toString() } returns ""
        every { event.notification } returns notification
        every { notification.additionalData } returns null
        every { notification.additionalData.toString() } returns ""
        every {
            OneSignal.Notifications.addClickListener(listener = capture(clickListener))
        } answers {
            clickListener.captured.onClick(event)
        }

        runTest {
            notificationsClient.addClickListener(context)

            verify(exactly = 1) {
                OneSignal.Notifications.addClickListener(any())

                notificationsClient.handleAdditionalData(context, notification.additionalData, null)
            }
        }
    }

    @Test
    fun `Given we have a notifications client, when handle additional data function is called with valid additional data, then a new activity is started with an intent`() {
        val uri = mockk<Uri>()
        val intent = spyk<Intent>()
        val additionalData = mockk<JSONObject>()

        every { uri.scheme } returns "scheme"
        every { uri.host } returns "host"
        every { Uri.parse("scheme://host") } returns uri
        every { intent.setData(uri) } returns intent
        every { intent.data } returns uri
        every { intent.setFlags(FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TASK) } returns intent
        every { intent.flags } returns (FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TASK)
        every { additionalData.toString() } returns "{\"deeplink\":\"scheme://host\"}"

        runTest {
            notificationsClient.handleAdditionalData(context, additionalData, intent)

            assertEquals("scheme", intent.data?.scheme)
            assertEquals("host", intent.data?.host)
            assertEquals(FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TASK, intent.flags)

            verify(exactly = 1) {
                context.startActivity(intent)
            }
        }
    }

    @Test
    fun `Given we have a notifications client, when handle additional data function is called with invalid additional data, then start activity is called with expected data`() {
        val uri = mockk<Uri>()
        val intent = spyk<Intent>()
        val additionalData = mockk<JSONObject>()

        every { Uri.parse("") } returns uri
        every { intent.setData(uri) } returns intent
        every { intent.data } returns uri
        every { intent.setFlags(FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TASK) } returns intent
        every { additionalData.toString() } returns ""

        runTest {
            notificationsClient.handleAdditionalData(context, additionalData, intent)

            assertEquals(uri, intent.data)

            verify(exactly = 1) {
                context.startActivity(intent)
            }
        }
    }

    @Test
    fun `Given we have a notifications client, when handle additional data function is called and additional data is null, then start activity is called with expected data`() {
        val uri = mockk<Uri>()
        val intent = spyk<Intent>()

        every { Uri.parse("") } returns uri
        every { intent.setData(uri) } returns intent
        every { intent.data } returns uri
        every { intent.setFlags(FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TASK) } returns intent

        val additionalData: JSONObject? = null

        runTest {
            notificationsClient.handleAdditionalData(context, additionalData, intent)

            assertEquals(uri, intent.data)

            verify(exactly = 1) {
                context.startActivity(intent)
            }
        }
    }

    @Test
    fun `Given we have a notifications client, when handle additional data function is called and intent is null, then start activity is not called`() {
        val intent: Intent? = null
        val additionalData = mockk<JSONObject>()

        runTest {
            notificationsClient.handleAdditionalData(context, additionalData, intent)

            verify(exactly = 0) {
                context.startActivity(intent)
            }
        }
    }
}
