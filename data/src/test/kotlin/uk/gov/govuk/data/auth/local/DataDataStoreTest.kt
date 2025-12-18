package uk.gov.govuk.data.auth.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import uk.gov.govuk.data.local.DataDataStore
import uk.gov.govuk.data.local.DataDataStore.Companion.REALM_IV
import uk.gov.govuk.data.local.DataDataStore.Companion.REALM_KEY
import uk.gov.govuk.data.local.DataDataStore.Companion.SUB_ID
import java.io.File
import kotlin.io.path.createTempDirectory

@OptIn(ExperimentalCoroutinesApi::class)
class DataDataStoreTest {

    private val dispatcher = UnconfinedTestDispatcher()

    private lateinit var tempDir: File
    private lateinit var dataStore: DataStore<Preferences>

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        tempDir = File(createTempDirectory().toString())
        dataStore = PreferenceDataStoreFactory.create(
            produceFile = { File(tempDir, "test.preferences_pb") }
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        tempDir.deleteRecursively()
    }

    @Test
    fun `Given the data store is empty, When get realm key, then return null`() {
        val dataDataStore = DataDataStore(dataStore)

        runTest {
            assertNull(dataDataStore.getRealmKey())
        }
    }

    @Test
    fun `Given there is a realm key, When get realm key, then realm key is returned`() {
        val dataDataStore = DataDataStore(dataStore)

        runTest {
            dataStore.edit { prefs ->
                prefs[stringPreferencesKey(REALM_KEY)] = "12345"
            }
            assertEquals("12345", dataDataStore.getRealmKey())
        }
    }

    @Test
    fun `Given we have a realm key, When set realm key, then the data store has the realm key`() {
        val dataDataStore = DataDataStore(dataStore)

        runTest {
            dataDataStore.saveRealmKey("12345")

            assertEquals("12345", dataDataStore.getRealmKey())
        }
    }

    @Test
    fun `Given the data store is empty, When get realm iv, then return null`() {
        val dataDataStore = DataDataStore(dataStore)

        runTest {
            assertNull(dataDataStore.getRealmIv())
        }
    }

    @Test
    fun `Given there is a realm iv, When get realm iv, then realm iv is returned`() {
        val dataDataStore = DataDataStore(dataStore)

        runTest {
            dataStore.edit { prefs ->
                prefs[stringPreferencesKey(REALM_IV)] = "12345"
            }
            assertEquals("12345", dataDataStore.getRealmIv())
        }
    }

    @Test
    fun `Given we have a realm iv, When set realm iv, then the data store has the realm iv`() {
        val dataDataStore = DataDataStore(dataStore)

        runTest {
            dataDataStore.saveRealmIv("12345")

            assertEquals("12345", dataDataStore.getRealmIv())
        }
    }

    @Test
    fun `Given the data store is empty, When get sub id, then return null`() {
        val dataDataStore = DataDataStore(dataStore)

        runTest {
            assertNull(dataDataStore.getSubId())
        }
    }

    @Test
    fun `Given there is a sub id, When get sub id, then sub id is returned`() {
        val dataDataStore = DataDataStore(dataStore)

        runTest {
            dataStore.edit { prefs ->
                prefs[stringPreferencesKey(SUB_ID)] = "12345"
            }
            assertEquals("12345", dataDataStore.getSubId())
        }
    }

    @Test
    fun `Given we have a sub id, When save sub id, then the data store has the sub id`() {
        val dataDataStore = DataDataStore(dataStore)

        runTest {
            dataDataStore.saveSubId("12345")

            assertEquals("12345", dataDataStore.getSubId())
        }
    }
}
