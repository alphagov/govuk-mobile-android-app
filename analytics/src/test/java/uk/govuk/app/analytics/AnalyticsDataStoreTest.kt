package uk.govuk.app.analytics

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

class AnalyticsDataStoreTest {

    private val dataStore = mockk<DataStore<Preferences>>()
    private val preferences = mockk<Preferences>()

    @Test
    fun `Given the data store is empty, When is analytics enabled, then return true`() {
        val datastore = AnalyticsDataStore(dataStore)

        every { dataStore.data } returns emptyFlow()

        runTest {
            assertTrue(datastore.isAnalyticsEnabled())
        }
    }

    @Test
    fun `Given analytics are enabled, then return true`() {
        val datastore = AnalyticsDataStore(dataStore)

        every { dataStore.data } returns flowOf(preferences)
        every { preferences[booleanPreferencesKey(AnalyticsDataStore.ANALYTICS_ENABLED_KEY)] } returns true

        runTest {
            assertTrue(datastore.isAnalyticsEnabled())
        }
    }

    @Test
    fun `Given analytics are disabled, then return false`() {
        val datastore = AnalyticsDataStore(dataStore)

        every { dataStore.data } returns flowOf(preferences)
        every { preferences[booleanPreferencesKey(AnalyticsDataStore.ANALYTICS_ENABLED_KEY)] } returns false

        runTest {
            assertFalse(datastore.isAnalyticsEnabled())
        }
    }
}