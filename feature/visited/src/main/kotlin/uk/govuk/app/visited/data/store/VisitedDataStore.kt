package uk.govuk.app.visited.data.store

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class VisitedDataStore @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        internal const val REALM_VISITED_KEY = "realm_visited_key"
        internal const val REALM_VISITED_IV = "realm_visited_iv"
    }

    internal suspend fun getRealmVisitedKey(): String? {
        return dataStore.data.firstOrNull()?.get(stringPreferencesKey(REALM_VISITED_KEY))
    }

    internal suspend fun saveRealmVisitedKey(key: String) {
        dataStore.edit { preferences ->
            preferences[stringPreferencesKey(REALM_VISITED_KEY)] = key
        }
    }

    internal suspend fun getRealmVisitedIv(): String? {
        return dataStore.data.firstOrNull()?.get(stringPreferencesKey(REALM_VISITED_IV))
    }

    internal suspend fun saveRealmVisitedIv(iv: String) {
        dataStore.edit { preferences ->
            preferences[stringPreferencesKey(REALM_VISITED_IV)] = iv
        }
    }
}
