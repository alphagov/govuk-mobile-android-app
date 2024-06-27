package uk.govuk.app.launch

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
import org.junit.Test

class AppLaunchDataStoreTest {

    private val dataStore = mockk<DataStore<Preferences>>()
    private val preferences = mockk<Preferences>()

    @Test
    fun `Given the data store is empty, When is onboarding completed, then return false`() {
        val datastore = AppLaunchDataStore(dataStore)

        every { dataStore.data } returns emptyFlow()

        runTest {
            assertFalse(datastore.isOnboardingCompleted())
        }
    }

    @Test
    fun `Given the onboarding completed flag is false in the data store, When is onboarding completed, then return false`() {
        val datastore = AppLaunchDataStore(dataStore)

        every { dataStore.data } returns flowOf(preferences)
        every { preferences[booleanPreferencesKey(AppLaunchDataStore.ONBOARDING_COMPLETE_KEY)] } returns false

        runTest {
            assertFalse(datastore.isOnboardingCompleted())
        }
    }

    @Test
    fun `Given the onboarding completed flag is true in the data store, When is onboarding completed, then return true`() {
        val datastore = AppLaunchDataStore(dataStore)

        every { dataStore.data } returns flowOf(preferences)
        every { preferences[booleanPreferencesKey(AppLaunchDataStore.ONBOARDING_COMPLETE_KEY)] } returns true

        runTest {
            assertTrue(datastore.isOnboardingCompleted())
        }
    }
}