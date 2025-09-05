package uk.gov.govuk.chat.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import uk.gov.govuk.chat.data.local.ChatDataStore.Companion.CHAT_OPT_IN_KEY
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

    @Test
    fun `Clears the conversation id`() = runTest {
        chatDataStore.saveConversationId("123")
        assertEquals("123", chatDataStore.conversationId())

        chatDataStore.clearConversation()

        assertNull(chatDataStore.conversationId())
    }

    @Test
    fun `Returns false for chat intro seen if data store is empty`() = runTest {
        assertFalse(chatDataStore.isChatIntroSeen())
    }

    @Test
    fun `Returns true for chat intro seen if data store value is true`() = runTest {
        chatDataStore.saveChatIntroSeen()
        assertTrue(chatDataStore.isChatIntroSeen())
    }

    @Test
    fun `Sets chat seen to true`() = runTest {
        assertFalse(chatDataStore.isChatIntroSeen())

        chatDataStore.saveChatIntroSeen()
        assertTrue(chatDataStore.isChatIntroSeen())
    }

    @Test
    fun `Sets chat opt in to true`() = runTest {
        assertTrue(chatDataStore.isChatOptInNull())
        chatDataStore.saveChatOptIn()
        assertTrue(
            dataStore.data.map { preferences ->
                preferences[booleanPreferencesKey(CHAT_OPT_IN_KEY)] == true
            }.first()
        )
    }

    @Test
    fun `Sets chat opt in to false`() = runTest {
        assertTrue(chatDataStore.isChatOptInNull())
        chatDataStore.saveChatOptOut()
        assertTrue(
            dataStore.data.map { preferences ->
                preferences[booleanPreferencesKey(CHAT_OPT_IN_KEY)] == false
            }.first()
        )
    }

    @Test
    fun `Clears the chat opt in flag`() = runTest {
        assertTrue(chatDataStore.isChatOptInNull())
        chatDataStore.saveChatOptIn()
        assertFalse(chatDataStore.isChatOptInNull())
        chatDataStore.clearChatOptIn()
        assertTrue(chatDataStore.isChatOptInNull())
    }

    @Test
    fun `Checks if chat opt in is present, prior to being set`() = runTest {
        assertTrue(chatDataStore.isChatOptInNull())
    }

    @Test
    fun `Checks if chat opt in is present, once set to true`() = runTest {
        chatDataStore.saveChatOptIn()

        assertFalse(chatDataStore.isChatOptInNull())
    }

    @Test
    fun `Checks if chat opt in is present, once set to false`() = runTest {
        chatDataStore.saveChatOptOut()

        assertFalse(chatDataStore.isChatOptInNull())
    }

    @Test
    fun `Checks has opted in emits true`() = runTest {
        chatDataStore.saveChatOptIn()

        assertTrue(chatDataStore.hasOptedIn.first())
    }

    @Test
    fun `Checks has opted in emits false`() = runTest {
        chatDataStore.saveChatOptOut()

        assertFalse(chatDataStore.hasOptedIn.first())
    }

    @Test
    fun `Checks has opted in emits false when opt in is null`() = runTest {
        chatDataStore.clearChatOptIn()

        assertFalse(chatDataStore.hasOptedIn.first())
    }

    @Test
    fun `Clears everything`() = runTest {
        chatDataStore.saveConversationId("123")
        chatDataStore.saveChatOptIn()
        chatDataStore.saveChatIntroSeen()

        assertEquals("123", chatDataStore.conversationId())
        assertFalse(chatDataStore.isChatOptInNull())
        assertTrue(chatDataStore.isChatIntroSeen())

        chatDataStore.clear()

        assertNull(chatDataStore.conversationId())
        assertTrue(chatDataStore.isChatOptInNull())
        assertFalse(chatDataStore.isChatIntroSeen())
    }
}
