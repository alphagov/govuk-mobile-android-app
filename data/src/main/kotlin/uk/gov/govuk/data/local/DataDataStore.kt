package uk.gov.govuk.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class DataDataStore @Inject constructor(
    @Named("database_prefs") private val dataStore: DataStore<Preferences>
) {
    companion object {
        internal const val REALM_KEY = "realm_key"
        internal const val REALM_IV = "realm_iv"
        internal const val SUB_ID = "sub_id"
    }

    internal suspend fun getRealmKey(): String? {
        return dataStore.data.firstOrNull()?.get(stringPreferencesKey(REALM_KEY))
    }

    internal suspend fun saveRealmKey(key: String) {
        dataStore.edit { preferences ->
            preferences[stringPreferencesKey(REALM_KEY)] = key
        }
    }

    internal suspend fun getRealmIv(): String? {
        return dataStore.data.firstOrNull()?.get(stringPreferencesKey(REALM_IV))
    }

    internal suspend fun saveRealmIv(iv: String) {
        dataStore.edit { preferences ->
            preferences[stringPreferencesKey(REALM_IV)] = iv
        }
    }

    internal suspend fun getSubId(): String? {
        return dataStore.data.firstOrNull()?.get(stringPreferencesKey(SUB_ID))
    }

    internal suspend fun saveSubId(subId: String) {
        dataStore.edit { preferences ->
            preferences[stringPreferencesKey(SUB_ID)] = subId
        }
    }
}
