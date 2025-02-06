package uk.govuk.app.notifications.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import uk.govuk.app.notifications.data.local.NotificationsPermissionState.DENIED
import uk.govuk.app.notifications.data.local.NotificationsPermissionState.GRANTED
import uk.govuk.app.notifications.data.local.NotificationsPermissionState.NOT_SET

class NotificationsDataStoreTest {

    private val dataStore = mockk<DataStore<Preferences>>()
    private val preferences = mockk<Preferences>()

    private lateinit var notificationsDataStore: NotificationsDataStore

    @Before
    fun setup() {
        notificationsDataStore = NotificationsDataStore(dataStore)
    }

    @Test
    fun `Given the data store is empty, then return not set`() {
        every { dataStore.data } returns emptyFlow()

        runTest {
            assertEquals(
                NOT_SET,
                notificationsDataStore.getNotificationsPermissionState()
            )
        }
    }

    @Test
    fun `Given notifications permission preference is null, then return not set`() {
        every { dataStore.data } returns flowOf(preferences)
        every { preferences[booleanPreferencesKey(NotificationsDataStore.NOTIFICATIONS_PERMISSION_KEY)] } returns null

        runTest {
            assertEquals(NOT_SET, notificationsDataStore.getNotificationsPermissionState())
        }
    }

    @Test
    fun `Given notifications permission are granted, then return granted`() {
        every { dataStore.data } returns flowOf(preferences)
        every { preferences[booleanPreferencesKey(NotificationsDataStore.NOTIFICATIONS_PERMISSION_KEY)] } returns true

        runTest {
            assertEquals(GRANTED, notificationsDataStore.getNotificationsPermissionState())
        }
    }

    @Test
    fun `Given notifications permission are denied, then return denied`() {
        every { dataStore.data } returns flowOf(preferences)
        every { preferences[booleanPreferencesKey(NotificationsDataStore.NOTIFICATIONS_PERMISSION_KEY)] } returns false

        runTest {
            assertEquals(DENIED, notificationsDataStore.getNotificationsPermissionState())
        }
    }
}
