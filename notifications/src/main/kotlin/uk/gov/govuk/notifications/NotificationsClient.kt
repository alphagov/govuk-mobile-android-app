package uk.gov.govuk.notifications

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import androidx.core.net.toUri
import com.onesignal.OneSignal
import com.onesignal.notifications.INotificationClickEvent
import com.onesignal.notifications.INotificationClickListener
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uk.gov.govuk.notifications.model.asAdditionalData
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

    fun addClickListener(context: Context) {
        OneSignal.Notifications.addClickListener(object : INotificationClickListener {
            override fun onClick(event: INotificationClickEvent) {
                val additionalData =
                    event.notification.additionalData.toString().asAdditionalData()

                Intent(Intent.ACTION_VIEW).apply {
                    data = additionalData.deepLink.toUri()
                    flags = FLAG_ACTIVITY_NEW_TASK
                }.also { intent ->
                    context.startActivity(intent)
                }
            }
        })
    }
}
