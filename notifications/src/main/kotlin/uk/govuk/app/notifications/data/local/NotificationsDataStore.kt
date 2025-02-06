package uk.govuk.app.notifications.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.firstOrNull
import uk.govuk.app.notifications.data.local.NotificationsPermissionState.DENIED
import uk.govuk.app.notifications.data.local.NotificationsPermissionState.GRANTED
import uk.govuk.app.notifications.data.local.NotificationsPermissionState.NOT_SET
import javax.inject.Inject
import javax.inject.Singleton

internal enum class NotificationsPermissionState {
    NOT_SET, GRANTED, DENIED
}

@Singleton
class NotificationsDataStore @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {

    companion object {
        internal const val NOTIFICATIONS_PERMISSION_KEY = "notifications_permission"
    }

    internal suspend fun getNotificationsPermissionState(): NotificationsPermissionState {
        return dataStore.data.firstOrNull()?.get(booleanPreferencesKey(NOTIFICATIONS_PERMISSION_KEY))?.let { enabled ->
            if (enabled) GRANTED else DENIED
        } ?: NOT_SET
    }

    internal suspend fun setNotificationsPermission(granted: Boolean) {
        dataStore.edit { preferences ->
            preferences[booleanPreferencesKey(NOTIFICATIONS_PERMISSION_KEY)] = granted
        }
    }
}
