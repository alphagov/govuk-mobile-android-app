package uk.gov.govuk.chat.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
internal class ChatDataStore @Inject constructor(
    @Named("chat_prefs") private val dataStore: DataStore<Preferences>
) {

    companion object {
        internal const val CONVERSATION_ID_KEY = "conversation_id"
    }

    internal suspend fun conversationId(): String? {
        return dataStore.data.firstOrNull()?.get(stringPreferencesKey(CONVERSATION_ID_KEY))
    }

    internal suspend fun saveConversationId(id: String) {
        dataStore.edit { preferences ->
            preferences[stringPreferencesKey(CONVERSATION_ID_KEY)] = id
        }
    }
}