package uk.govuk.app.topics.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class TopicsDataStore @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {

    companion object {
        internal const val TOPICS_CUSTOMISED = "topics_customised"
    }

    internal suspend fun isTopicsCustomised(): Boolean {
        return dataStore.data.firstOrNull()?.get(booleanPreferencesKey(TOPICS_CUSTOMISED)) == true
    }

    internal suspend fun topicsCustomised() {
        dataStore.edit { preferences ->
            preferences[booleanPreferencesKey(TOPICS_CUSTOMISED)] = true
        }
    }
}