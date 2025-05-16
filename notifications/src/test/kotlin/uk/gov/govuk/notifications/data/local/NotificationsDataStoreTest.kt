package uk.gov.govuk.notifications.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class NotificationsDataStoreTest {

    private val dataStore = mockk<DataStore<Preferences>>()
    private val preferences = mockk<Preferences>()

    private lateinit var notificationsDataStore: NotificationsDataStore

    @Before
    fun setup() {
        notificationsDataStore = NotificationsDataStore(dataStore)
    }

    @Test
    fun `Given the data store is empty, then return false`() {
        every { dataStore.data } returns emptyFlow()

        runTest {
            assertFalse(notificationsDataStore.isOnboardingCompleted())
        }
    }

    @Test
    fun `Given notifications onboarding completed is null, then return false`() {
        every { dataStore.data } returns flowOf(preferences)
        every { preferences[booleanPreferencesKey(NotificationsDataStore.NOTIFICATIONS_ONBOARDING_COMPLETED_KEY)] } returns null

        runTest {
            assertFalse(notificationsDataStore.isOnboardingCompleted())
        }
    }

    @Test
    fun `Given notifications onboarding completed is true, then return true`() {
        every { dataStore.data } returns flowOf(preferences)
        every { preferences[booleanPreferencesKey(NotificationsDataStore.NOTIFICATIONS_ONBOARDING_COMPLETED_KEY)] } returns true

        runTest {
            assertTrue(notificationsDataStore.isOnboardingCompleted())
        }
    }
}
