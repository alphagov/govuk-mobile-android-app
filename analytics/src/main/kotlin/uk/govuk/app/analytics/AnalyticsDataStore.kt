package uk.govuk.app.analytics

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class AnalyticsDataStore @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {

    companion object {
        internal const val ANALYTICS_ENABLED_KEY = "analytics_enabled"
    }

    internal suspend fun isAnalyticsEnabled(): Boolean {
        return dataStore.data.firstOrNull()
            ?.get(booleanPreferencesKey(ANALYTICS_ENABLED_KEY)) ?: true
    }

    internal suspend fun analyticsEnabled() {
        dataStore.edit { preferences ->
            preferences[booleanPreferencesKey(ANALYTICS_ENABLED_KEY)] = true
        }
    }

    internal suspend fun analyticsDisabled() {
        dataStore.edit { preferences ->
            preferences[booleanPreferencesKey(ANALYTICS_ENABLED_KEY)] = false
        }
    }
}