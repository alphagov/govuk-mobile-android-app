package uk.govuk.app.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import kotlinx.coroutines.flow.firstOrNull
import uk.govuk.app.home.HomeWidget
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class AppDataStore @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        internal const val ONBOARDING_COMPLETED_KEY = "onboarding_completed"
        internal const val TOPIC_SELECTION_COMPLETED_KEY = "topic_selection_completed"
        internal const val SUPPRESSED_HOME_WIDGETS = "suppressed_home_widgets"
    }

    internal suspend fun isOnboardingCompleted(): Boolean {
        return dataStore.data.firstOrNull()
            ?.get(booleanPreferencesKey(ONBOARDING_COMPLETED_KEY)) == true
    }

    internal suspend fun onboardingCompleted() {
        dataStore.edit { prefs ->
            prefs[booleanPreferencesKey(ONBOARDING_COMPLETED_KEY)] = true
        }
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

    internal suspend fun isHomeWidgetInSuppressedList(widget: HomeWidget): Boolean {
        return getSuppressedHomeWidgets()?.contains(widget.name) ?: false
    }

    internal suspend fun addHomeWidgetToSuppressedList(widget: HomeWidget) {
        val mutableWidgets = getSuppressedHomeWidgets()?.toMutableSet() ?: mutableSetOf()
        mutableWidgets.add(widget.name)
        dataStore.edit { prefs ->
            prefs[stringSetPreferencesKey(SUPPRESSED_HOME_WIDGETS)] = mutableWidgets.toSet()
        }
    }

    private suspend fun getSuppressedHomeWidgets() = dataStore.data.firstOrNull()
        ?.get(stringSetPreferencesKey(SUPPRESSED_HOME_WIDGETS))
}
