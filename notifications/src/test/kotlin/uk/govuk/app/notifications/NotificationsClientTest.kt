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
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import uk.govuk.app.notifications.data.local.NotificationsDataStore
import uk.govuk.app.notifications.data.local.NotificationsPermissionState

@OptIn(ExperimentalCoroutinesApi::class)
class NotificationsClientTest {
    private val context = mockk<Context>(relaxed = true)
    private val dataStore = mockk<NotificationsDataStore>(relaxed = true)

    private lateinit var notificationsClient: NotificationsClient

    @Before
    fun setup() {
        notificationsClient = NotificationsClient(dataStore)

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
    fun `Given we have a notifications client, when request permission is called, One Signal request permission function is called`() {
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
    fun `Given we have a notifications client, when permission determined is called, and permission is granted, then return true`() {
        coEvery { dataStore.getNotificationsPermissionState() } returns NotificationsPermissionState.GRANTED
        every { OneSignal.initWithContext(context) } returns true
        every { OneSignal.Notifications.permission } returns false
        runTest {
            assertTrue(notificationsClient.permissionDetermined())
        }
    }

    @Test
    fun `Given we have a notifications client, when permission determined is called, and permission is denied, then return true`() {
        coEvery { dataStore.getNotificationsPermissionState() } returns NotificationsPermissionState.DENIED
        every { OneSignal.initWithContext(context) } returns true
        every { OneSignal.Notifications.permission } returns false
        runTest {
            assertTrue(notificationsClient.permissionDetermined())
        }
    }

    @Test
    fun `Given we have a notifications client, when permission determined is called, and permission is not determined, then return false`() {
        coEvery { dataStore.getNotificationsPermissionState() } returns NotificationsPermissionState.NOT_SET
        every { OneSignal.initWithContext(context) } returns true
        every { OneSignal.Notifications.permission } returns false
        runTest {
            assertFalse(notificationsClient.permissionDetermined())
        }
    }

    @Test
    fun `Given we have a notifications client, when permission determined is called, and permission is granted and permission is not determined, then return false`() {
        coEvery { dataStore.getNotificationsPermissionState() } returns NotificationsPermissionState.NOT_SET
        every { OneSignal.initWithContext(context) } returns true
        every { OneSignal.Notifications.permission } returns true
        runTest {
            assertTrue(notificationsClient.permissionDetermined())
        }
    }

    @Test
    fun `Given we have a notifications client, when permission determined is called, and permission is denied and permission is not determined, then return false`() {
        coEvery { dataStore.getNotificationsPermissionState() } returns NotificationsPermissionState.NOT_SET
        every { OneSignal.initWithContext(context) } returns true
        every { OneSignal.Notifications.permission } returns false
        runTest {
            assertFalse(notificationsClient.permissionDetermined())
        }
    }
}
