package uk.govuk.app.notifications

import android.content.Context
import com.onesignal.OneSignal
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uk.govuk.app.notifications.data.local.NotificationsDataStore
import uk.govuk.app.notifications.data.local.NotificationsPermissionState
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationsClient @Inject constructor(private val dataStore: NotificationsDataStore) {

    fun initialise(context: Context, oneSignalAppId: String) {
        OneSignal.initWithContext(context, oneSignalAppId)
    }

    fun requestPermission(dispatcher: CoroutineDispatcher = Dispatchers.IO) {
        CoroutineScope(dispatcher).launch {
            val granted = OneSignal.Notifications.requestPermission(false)
            if (granted) {
                dataStore.setNotificationsPermissionGranted()
            } else {
                dataStore.setNotificationsPermissionDenied()
            }
        }
    }

    suspend fun permissionDetermined(): Boolean {
        return (dataStore.getNotificationsPermissionState() != NotificationsPermissionState.NOT_SET)
                || OneSignal.Notifications.permission
    }
}
