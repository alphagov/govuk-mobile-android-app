package uk.govuk.app.notifications

import android.content.Context
import com.onesignal.OneSignal
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Singleton

@Singleton
class NotificationsClient {

    private companion object {
        const val ONE_SIGNAL_APP_ID = "4c235189-5c5f-4a71-8385-2549fc36419f"
    }

    fun initialise(context: Context) {
        OneSignal.initWithContext(context, ONE_SIGNAL_APP_ID)

        CoroutineScope(Dispatchers.IO).launch {
            OneSignal.Notifications.requestPermission(false)
        }
    }
}
