package uk.govuk.app.search.data.local

import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class SearchDataStore @Inject constructor(
    private val dataStore: androidx.datastore.core.DataStore<androidx.datastore.preferences.core.Preferences>
) {

    companion object {
        internal const val REALM_SEARCH_KEY = "realm_search_key"
        internal const val REALM_SEARCH_IV = "realm_search_iv"
    }

    internal suspend fun getRealmSearchKey(): String? {
        return dataStore.data.firstOrNull()?.get(stringPreferencesKey(REALM_SEARCH_KEY))
    }

    internal suspend fun saveRealmSearchKey(key: String) {
        dataStore.edit { preferences ->
            preferences[stringPreferencesKey(REALM_SEARCH_KEY)] = key
        }
    }

    internal suspend fun getRealmSearchIv(): String? {
        return dataStore.data.firstOrNull()?.get(stringPreferencesKey(REALM_SEARCH_IV))
    }

    internal suspend fun saveRealmSearchIv(iv: String) {
        dataStore.edit { preferences ->
            preferences[stringPreferencesKey(REALM_SEARCH_IV)] = iv
        }
    }
}