package uk.gov.govuk.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import uk.gov.govuk.home.HomeWidget

class AppDataStoreTest {

    private val dataStore = mockk<DataStore<Preferences>>()
    private val preferences = mockk<Preferences>()

    @Test
    fun `Given the data store is empty, When is onboarding completed, then return false`() {
        val datastore = AppDataStore(dataStore)

        every { dataStore.data } returns emptyFlow()

        runTest {
            assertFalse(datastore.isOnboardingCompleted())
        }
    }

    @Test
    fun `Given the onboarding completed flag is false in the data store, When is onboarding completed, then return false`() {
        val datastore = AppDataStore(dataStore)

        every { dataStore.data } returns flowOf(preferences)
        every { preferences[booleanPreferencesKey(AppDataStore.ONBOARDING_COMPLETED_KEY)] } returns false

        runTest {
            assertFalse(datastore.isOnboardingCompleted())
        }
    }

    @Test
    fun `Given the onboarding completed flag is true in the data store, When is onboarding completed, then return true`() {
        val datastore = AppDataStore(dataStore)

        every { dataStore.data } returns flowOf(preferences)
        every { preferences[booleanPreferencesKey(AppDataStore.ONBOARDING_COMPLETED_KEY)] } returns true

        runTest {
            assertTrue(datastore.isOnboardingCompleted())
        }
    }

    @Test
    fun `Given the data store is empty, When is topic selection completed, then return false`() {
        val datastore = AppDataStore(dataStore)

        every { dataStore.data } returns emptyFlow()

        runTest {
            assertFalse(datastore.isTopicSelectionCompleted())
        }
    }

    @Test
    fun `Given the topic selection completed flag is false in the data store, When is topic selection completed, then return false`() {
        val datastore = AppDataStore(dataStore)

        every { dataStore.data } returns flowOf(preferences)
        every { preferences[booleanPreferencesKey(AppDataStore.TOPIC_SELECTION_COMPLETED_KEY)] } returns false

        runTest {
            assertFalse(datastore.isTopicSelectionCompleted())
        }
    }

    @Test
    fun `Given the topic selection completed flag is true in the data store, When is topic selection completed, then return true`() {
        val datastore = AppDataStore(dataStore)

        every { dataStore.data } returns flowOf(preferences)
        every { preferences[booleanPreferencesKey(AppDataStore.TOPIC_SELECTION_COMPLETED_KEY)] } returns true

        runTest {
            assertTrue(datastore.isTopicSelectionCompleted())
        }
    }

    @Test
    fun `Given the data store is empty, When is widget suppressed, then return false`() {
        val datastore = AppDataStore(dataStore)

        every { dataStore.data } returns emptyFlow()

        runTest {
            assertFalse(datastore.isHomeWidgetInSuppressedList(HomeWidget.NOTIFICATIONS))
        }
    }

    @Test
    fun `Given the suppressed widgets is empty, When is widget suppressed, then return false`() {
        val datastore = AppDataStore(dataStore)

        every { dataStore.data } returns flowOf(preferences)
        every { preferences[stringSetPreferencesKey(AppDataStore.SUPPRESSED_HOME_WIDGETS)] } returns setOf()

        runTest {
            assertFalse(datastore.isHomeWidgetInSuppressedList(HomeWidget.NOTIFICATIONS))
        }
    }

    @Test
    fun `Given the suppressed widgets has notifications, When is widget suppressed, then return true`() {
        val datastore = AppDataStore(dataStore)

        every { dataStore.data } returns flowOf(preferences)
        every { preferences[stringSetPreferencesKey(AppDataStore.SUPPRESSED_HOME_WIDGETS)] } returns setOf("NOTIFICATIONS")

        runTest {
            assertTrue(datastore.isHomeWidgetInSuppressedList(HomeWidget.NOTIFICATIONS))
        }
    }
}
