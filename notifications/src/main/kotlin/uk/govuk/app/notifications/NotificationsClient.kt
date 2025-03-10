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

    fun initialise(context: Context, oneSignalAppId: String) {
        OneSignal.consentRequired = true
        OneSignal.initWithContext(context, oneSignalAppId)
    }

    fun giveConsent() {
        OneSignal.consentGiven = true
    }

    fun requestPermission(
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
        onCompleted: (() -> Unit)? = null
    ) {
        CoroutineScope(dispatcher).launch {
            val permissionGranted = OneSignal.Notifications.requestPermission(false)
            OneSignal.consentGiven = permissionGranted
            onCompleted?.invoke()
        }
    }
}
