package uk.gov.govuk.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AppDataStoreTest {

    private val dispatcher = UnconfinedTestDispatcher()
    private val dataStore = mockk<DataStore<Preferences>>(relaxed = true)
    private val preferences = mockk<Preferences>()

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Given the data store is empty, When is topic selection completed, then return false`() {
        val appDatastore = AppDataStore(dataStore)

        every { dataStore.data } returns emptyFlow()

        runTest {
            assertFalse(appDatastore.isTopicSelectionCompleted())
        }
    }

    @Test
    fun `Given the topic selection completed flag is false in the data store, When is topic selection completed, then return false`() {
        val appDatastore = AppDataStore(dataStore)

        every { dataStore.data } returns flowOf(preferences)
        every { preferences[booleanPreferencesKey(AppDataStore.TOPIC_SELECTION_COMPLETED_KEY)] } returns false

        runTest {
            assertFalse(appDatastore.isTopicSelectionCompleted())
        }
    }

    @Test
    fun `Given the topic selection completed flag is true in the data store, When is topic selection completed, then return true`() {
        val appDatastore = AppDataStore(dataStore)

        every { dataStore.data } returns flowOf(preferences)
        every { preferences[booleanPreferencesKey(AppDataStore.TOPIC_SELECTION_COMPLETED_KEY)] } returns true

        runTest {
            assertTrue(appDatastore.isTopicSelectionCompleted())
        }
    }

    @Test
    fun `Given the data store is empty, when collecting suppressed home widgets, then flow is empty`() {
        every { dataStore.data } returns emptyFlow()

        val appDatastore = AppDataStore(dataStore)

        runTest {
            val suppressedWidgets = mutableListOf<Set<String>>()
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                appDatastore.suppressedHomeWidgets.toList(suppressedWidgets)
            }

            assertTrue(suppressedWidgets.isEmpty())
        }
    }

    @Test
    fun `Given the suppressed widgets is null, when collecting suppressed home widgets, then flow emits an empty set`() {
        every { dataStore.data } returns flowOf(preferences)
        every { preferences[stringSetPreferencesKey(AppDataStore.SUPPRESSED_HOME_WIDGETS)] } returns null

        val appDatastore = AppDataStore(dataStore)

        runTest {
            val suppressedWidgets = mutableListOf<Set<String>>()
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                appDatastore.suppressedHomeWidgets.toList(suppressedWidgets)
            }

            assertTrue(suppressedWidgets[0].isEmpty())
        }
    }

    @Test
    fun `Given the suppressed widgets are empty, when collecting suppressed home widgets, then flow emits an empty set`() {
        every { dataStore.data } returns flowOf(preferences)
        every { preferences[stringSetPreferencesKey(AppDataStore.SUPPRESSED_HOME_WIDGETS)] } returns emptySet()

        val appDatastore = AppDataStore(dataStore)

        runTest {
            val suppressedWidgets = mutableListOf<Set<String>>()
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                appDatastore.suppressedHomeWidgets.toList(suppressedWidgets)
            }

            assertTrue(suppressedWidgets[0].isEmpty())
        }
    }

    @Test
    fun `Given the suppressed widgets are present, when collecting suppressed home widgets, then flow emits a set containing suppressed widgets`() {
        every { dataStore.data } returns flowOf(preferences)
        every { preferences[stringSetPreferencesKey(AppDataStore.SUPPRESSED_HOME_WIDGETS)] } returns setOf("Widget 1", "Widget 2")

        val appDatastore = AppDataStore(dataStore)

        runTest {
            val flowResults = mutableListOf<Set<String>>()
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                appDatastore.suppressedHomeWidgets.toList(flowResults)
            }

            val suppressedWidgets = flowResults[0]
            assertEquals(2, suppressedWidgets.size)
            assertTrue(suppressedWidgets.contains("Widget 1"))
            assertTrue(suppressedWidgets.contains("Widget 2"))
        }
    }
}
