package uk.govuk.app.analytics.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.firstOrNull
import uk.govuk.app.analytics.data.local.AnalyticsEnabledState.DISABLED
import uk.govuk.app.analytics.data.local.AnalyticsEnabledState.ENABLED
import uk.govuk.app.analytics.data.local.AnalyticsEnabledState.NOT_SET
import javax.inject.Inject
import javax.inject.Singleton

enum class AnalyticsEnabledState {
    NOT_SET, ENABLED, DISABLED
}

@Singleton
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