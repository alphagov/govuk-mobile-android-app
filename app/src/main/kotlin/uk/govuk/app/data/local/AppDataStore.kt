package uk.govuk.app.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import kotlinx.coroutines.flow.firstOrNull
import uk.govuk.app.HomeScreenWidget
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class AppDataStore @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        internal const val ONBOARDING_COMPLETED_KEY = "onboarding_completed"
        internal const val TOPIC_SELECTION_COMPLETED_KEY = "topic_selection_completed"
        internal const val SUPPRESSED_WIDGETS = "suppressed_widgets"
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

    internal suspend fun isWidgetSuppressed(widget: HomeScreenWidget): Boolean {
        val widgets = dataStore.data.firstOrNull()
            ?.get(stringSetPreferencesKey(SUPPRESSED_WIDGETS))
        return widgets?.contains(widget.name) ?: false
    }

    internal suspend fun suppressWidget(widget: HomeScreenWidget) {
        val widgets = dataStore.data.firstOrNull()
            ?.get(stringSetPreferencesKey(SUPPRESSED_WIDGETS))
        val mutableWidgets = widgets?.toMutableSet() ?: mutableSetOf()
        mutableWidgets.add(widget.name)
        dataStore.edit { prefs ->
            prefs[stringSetPreferencesKey(SUPPRESSED_WIDGETS)] = mutableWidgets.toSet()
        }
    }
}