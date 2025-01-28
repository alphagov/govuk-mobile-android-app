package uk.govuk.app.notifications

import android.content.Context
import com.onesignal.OneSignal
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationsClient @Inject constructor() {

    fun initialise(
        context: Context,
        oneSignalAppId: String,
        dispatcher: CoroutineDispatcher = Dispatchers.IO
    ) {
        OneSignal.initWithContext(context, oneSignalAppId)

        CoroutineScope(dispatcher).launch {
            OneSignal.Notifications.requestPermission(false)
        }
    }
}
