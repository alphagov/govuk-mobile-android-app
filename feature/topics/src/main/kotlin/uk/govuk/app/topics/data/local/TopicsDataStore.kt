package uk.govuk.app.topics.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class TopicsDataStore @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {

    companion object {
        internal const val REALM_TOPICS_KEY = "realm_topics_key"
        internal const val REALM_TOPICS_IV = "realm_topics_iv"
    }

    internal suspend fun getRealmTopicsKey(): String? {
        return dataStore.data.firstOrNull()?.get(stringPreferencesKey(REALM_TOPICS_KEY))
    }

    internal suspend fun saveRealmTopicsKey(key: String) {
        dataStore.edit { preferences ->
            preferences[stringPreferencesKey(REALM_TOPICS_KEY)] = key
        }
    }

    internal suspend fun getRealmTopicsIv(): String? {
        return dataStore.data.firstOrNull()?.get(stringPreferencesKey(REALM_TOPICS_IV))
    }

    internal suspend fun saveRealmTopicsIv(iv: String) {
        dataStore.edit { preferences ->
            preferences[stringPreferencesKey(REALM_TOPICS_IV)] = iv
        }
    }
}