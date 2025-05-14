package uk.gov.govuk.notifications.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class NotificationsDataStore @Inject constructor(
    @Named("notification_prefs") private val dataStore: DataStore<Preferences>
) {

    companion object {
        internal const val NOTIFICATIONS_ONBOARDING_SEEN_KEY = "notifications_onboarding_seen"
    }

    internal suspend fun isOnboardingSeen(): Boolean {
        return dataStore.data.firstOrNull()
            ?.get(booleanPreferencesKey(NOTIFICATIONS_ONBOARDING_SEEN_KEY)) == true
    }

    internal suspend fun onboardingSeen() {
        dataStore.edit { preferences -> preferences[booleanPreferencesKey(NOTIFICATIONS_ONBOARDING_SEEN_KEY)] = true
        }
    }
}
