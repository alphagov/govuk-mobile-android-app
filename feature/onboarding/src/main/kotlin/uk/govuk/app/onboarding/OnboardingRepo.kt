package uk.govuk.app.onboarding

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.first
import javax.inject.Inject

internal class OnboardingRepo @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {

    companion object {
        private const val ONBOARDING_COMPLETE_KEY = "onboarding_completed"
    }

    internal suspend fun isOnboardingCompleted(): Boolean {
        return dataStore.data.first()[booleanPreferencesKey(ONBOARDING_COMPLETE_KEY)] ?: false
    }

    internal suspend fun onboardingCompleted() {
        dataStore.edit { onboardingPreferences ->
            onboardingPreferences[booleanPreferencesKey(ONBOARDING_COMPLETE_KEY)] = true
        }
    }
}