package uk.govuk.app.analytics.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import uk.govuk.app.analytics.data.local.AnalyticsEnabledState.DISABLED
import uk.govuk.app.analytics.data.local.AnalyticsEnabledState.ENABLED
import uk.govuk.app.analytics.data.local.AnalyticsEnabledState.NOT_SET

class AnalyticsDataStoreTest {

    private val dataStore = mockk<DataStore<Preferences>>()
    private val preferences = mockk<Preferences>()

    @Test
    fun `Given the data store is empty, then return not set`() {
        val datastore = AnalyticsDataStore(dataStore)

        every { dataStore.data } returns emptyFlow()

        runTest {
            assertEquals(NOT_SET, datastore.getAnalyticsEnabledState())
        }
    }

    @Test
    fun `Given analytics enabled preference is null, then return not set`() {
        val datastore = AnalyticsDataStore(dataStore)

        every { dataStore.data } returns flowOf(preferences)
        every { preferences[booleanPreferencesKey(AnalyticsDataStore.ANALYTICS_ENABLED_KEY)] } returns null

        runTest {
            assertEquals(NOT_SET, datastore.getAnalyticsEnabledState())
        }
    }

    @Test
    fun `Given analytics are enabled, then return enabled`() {
        val datastore = AnalyticsDataStore(dataStore)

        every { dataStore.data } returns flowOf(preferences)
        every { preferences[booleanPreferencesKey(AnalyticsDataStore.ANALYTICS_ENABLED_KEY)] } returns true

        runTest {
            assertEquals(ENABLED, datastore.getAnalyticsEnabledState())
        }
    }

    @Test
    fun `Given analytics are disabled, then return disabled`() {
        val datastore = AnalyticsDataStore(dataStore)

        every { dataStore.data } returns flowOf(preferences)
        every { preferences[booleanPreferencesKey(AnalyticsDataStore.ANALYTICS_ENABLED_KEY)] } returns false

        runTest {
            assertEquals(DISABLED, datastore.getAnalyticsEnabledState())
        }
    }
}