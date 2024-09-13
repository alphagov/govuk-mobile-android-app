package uk.govuk.app.analytics

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.firstOrNull
import uk.govuk.app.analytics.AnalyticsEnabledState.DISABLED
import uk.govuk.app.analytics.AnalyticsEnabledState.ENABLED
import uk.govuk.app.analytics.AnalyticsEnabledState.NOT_SET
import javax.inject.Inject

enum class AnalyticsEnabledState {
    NOT_SET, ENABLED, DISABLED
}

class AnalyticsDataStore @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {

    companion object {
        internal const val ANALYTICS_ENABLED_KEY = "analytics_enabled"
    }

    internal suspend fun getAnalyticsEnabledState(): AnalyticsEnabledState {
        return dataStore.data.firstOrNull()?.get(booleanPreferencesKey(ANALYTICS_ENABLED_KEY))?.let { enabled ->
            if (enabled) ENABLED else DISABLED
        } ?: NOT_SET
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