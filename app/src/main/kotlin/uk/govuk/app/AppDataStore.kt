package uk.govuk.app

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

internal class AppDataStore @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {

    companion object {
        internal const val ONBOARDING_COMPLETE_KEY = "onboarding_completed"
    }

    internal suspend fun isOnboardingCompleted(): Boolean {
        return dataStore.data.firstOrNull()
            ?.get(booleanPreferencesKey(ONBOARDING_COMPLETE_KEY)) ?: false
    }

    internal suspend fun onboardingCompleted() {
        dataStore.edit { onboardingPreferences ->
            onboardingPreferences[booleanPreferencesKey(ONBOARDING_COMPLETE_KEY)] = true
        }
    }
}