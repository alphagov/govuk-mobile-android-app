package uk.govuk.app.topics.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class TopicsDataStoreTest {

    private val dataStore = mockk<DataStore<Preferences>>()
    private val preferences = mockk<Preferences>()

    @Test
    fun `Given realm key is null, then return null`() {
        val datastore = TopicsDataStore(dataStore)

        every { dataStore.data } returns flowOf(preferences)
        every { preferences[stringPreferencesKey(TopicsDataStore.REALM_TOPICS_KEY)] } returns null

        runTest {
            assertNull(datastore.getRealmTopicsKey())
        }
    }

    @Test
    fun `Given realm key is not null, then return realm key`() {
        val datastore = TopicsDataStore(dataStore)

        every { dataStore.data } returns flowOf(preferences)
        every { preferences[stringPreferencesKey(TopicsDataStore.REALM_TOPICS_KEY)] } returns "realmKey"

        runTest {
            assertEquals("realmKey", datastore.getRealmTopicsKey())
        }
    }

    @Test
    fun `Given realm iv is null, then return null`() {
        val datastore = TopicsDataStore(dataStore)

        every { dataStore.data } returns flowOf(preferences)
        every { preferences[stringPreferencesKey(TopicsDataStore.REALM_TOPICS_IV)] } returns null

        runTest {
            assertNull(datastore.getRealmTopicsIv())
        }
    }

    @Test
    fun `Given realm iv is not null, then return realm iv`() {
        val datastore = TopicsDataStore(dataStore)

        every { dataStore.data } returns flowOf(preferences)
        every { preferences[stringPreferencesKey(TopicsDataStore.REALM_TOPICS_IV)] } returns "realmIv"

        runTest {
            assertEquals("realmIv", datastore.getRealmTopicsIv())
        }
    }
}