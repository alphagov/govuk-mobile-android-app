package uk.gov.govuk.notifications

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.net.Uri
import com.onesignal.OneSignal
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.spyk
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.json.JSONObject
import org.junit.After
import org.junit.Assert.assertEquals
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
    fun `Given we have a notifications client, when request permission is called and permissions denied, then One Signal request permission function is called`() {
        every { OneSignal.Notifications.canRequestPermission } returns true
        coEvery { OneSignal.Notifications.requestPermission(false) } returns true

        runTest {
            val dispatcher = UnconfinedTestDispatcher()
            notificationsClient.requestPermission(dispatcher)

            coVerify(exactly = 1) {
                OneSignal.Notifications.requestPermission(false)
            }
            verify(exactly = 1) {
                OneSignal.consentGiven = true
            }
        }
    }

    @Test
    fun `Given we have a notifications client, when request permission is called and permissions granted, then One Signal request permission function is called`() {
        every { OneSignal.Notifications.canRequestPermission } returns true
        coEvery { OneSignal.Notifications.requestPermission(false) } returns false

        runTest {
            val dispatcher = UnconfinedTestDispatcher()
            notificationsClient.requestPermission(dispatcher)

            coVerify(exactly = 1) {
                OneSignal.Notifications.requestPermission(false)
            }
            verify(exactly = 1) {
                OneSignal.consentGiven = false
            }
        }
    }

    @Test
    fun `Given we have a notifications client, when add click listener is called, then One Signal add click listener function is called`() {
        every { OneSignal.Notifications.addClickListener(any()) } returns Unit

        runTest {
            notificationsClient.addClickListener(context)

            verify(exactly = 1) {
                OneSignal.Notifications.addClickListener(any())
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
        every { intent.setFlags(FLAG_ACTIVITY_NEW_TASK) } returns intent
        every { intent.flags } returns FLAG_ACTIVITY_NEW_TASK
        every { additionalData.toString() } returns "{\"deeplink\":\"scheme://host\"}"

        runTest {
            notificationsClient.handleAdditionalData(context, additionalData, intent)

            assertEquals("scheme", intent.data?.scheme)
            assertEquals("host", intent.data?.host)
            assertEquals(FLAG_ACTIVITY_NEW_TASK, intent.flags)

            verify(exactly = 1) {
                context.startActivity(intent)
            }
        }
    }

    @Test
    fun `Given we have a notifications client, when handle additional data function is called and additional data is null, then start activity is not called`() {
        val intent = spyk<Intent>()

        val additionalData: JSONObject? = null

        runTest {
            notificationsClient.handleAdditionalData(context, additionalData, intent)

            verify(exactly = 0) {
                context.startActivity(intent)
            }
        }
    }
}
