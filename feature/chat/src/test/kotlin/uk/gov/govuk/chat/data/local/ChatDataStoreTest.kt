package uk.gov.govuk.chat.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import java.io.File
import kotlin.io.path.createTempDirectory

class ChatDataStoreTest {

    private lateinit var tempDir: File
    private lateinit var dataStore: DataStore<Preferences>
    private lateinit var chatDataStore: ChatDataStore

    @Before
    fun setup() {
        tempDir = File(createTempDirectory().toString())
        dataStore = PreferenceDataStoreFactory.create(
            produceFile = { File(tempDir, "test.preferences_pb") }
        )
        chatDataStore = ChatDataStore(dataStore)
    }

    @After
    fun tearDown() {
        tempDir.deleteRecursively()
    }

    @Test
    fun `Returns null for conversation id if data store is empty`() = runTest {
        assertNull(chatDataStore.conversationId())
    }

    @Test
    fun `Saves and returns conversation id`() = runTest {
        assertNull(chatDataStore.conversationId())

        chatDataStore.saveConversationId("123")
        assertEquals("123", chatDataStore.conversationId())
    }
}
