package uk.gov.govuk.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import uk.gov.govuk.ui.model.HomeWidget
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
internal class AppDataStore @Inject constructor(
    @Named("app_prefs") private val dataStore: DataStore<Preferences>
) {
    companion object {
        internal const val TOPIC_SELECTION_COMPLETED_KEY = "topic_selection_completed"
        internal const val SUPPRESSED_HOME_WIDGETS = "suppressed_home_widgets"
    }

    internal suspend fun isTopicSelectionCompleted(): Boolean {
        return dataStore.data.firstOrNull()
            ?.get(booleanPreferencesKey(TOPIC_SELECTION_COMPLETED_KEY)) == true
    }

    internal suspend fun topicSelectionCompleted() {
        dataStore.edit { prefs ->
            prefs[booleanPreferencesKey(TOPIC_SELECTION_COMPLETED_KEY)] = true
        }
    }

    internal val suppressedHomeWidgets = dataStore.data
        .map { preferences ->
            preferences[stringSetPreferencesKey(SUPPRESSED_HOME_WIDGETS)] ?: emptySet()
        }
        .distinctUntilChanged()

    internal suspend fun suppressHomeWidget(widget: HomeWidget) {
        val mutableWidgets = getSuppressedHomeWidgets()?.toMutableSet() ?: mutableSetOf()
        mutableWidgets.add(widget.serializedName)
        dataStore.edit { prefs -> prefs[stringSetPreferencesKey(SUPPRESSED_HOME_WIDGETS)] = mutableWidgets.toSet() }
    }

    private suspend fun getSuppressedHomeWidgets() = dataStore.data.firstOrNull()
        ?.get(stringSetPreferencesKey(SUPPRESSED_HOME_WIDGETS))

    internal suspend fun clear() {
        dataStore.edit { prefs ->
            prefs.remove(booleanPreferencesKey(TOPIC_SELECTION_COMPLETED_KEY))
            prefs.remove(stringSetPreferencesKey(SUPPRESSED_HOME_WIDGETS))
        }
    }
}
