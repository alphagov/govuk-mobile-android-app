package uk.gov.govuk.notifications

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import androidx.core.net.toUri
import com.onesignal.OneSignal
import com.onesignal.notifications.INotificationClickEvent
import com.onesignal.notifications.INotificationClickListener
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
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
                handleAdditionalData(context, event.notification.additionalData)
            }
        })
    }

    internal fun handleAdditionalData(
        context: Context,
        additionalDataJson: JSONObject?,
        intent: Intent? = context.packageManager.getLaunchIntentForPackage(context.packageName)
    ) {
        // If additional data couldn't be parsed, send an empty Uri for the app to handle
        val deepLink = additionalDataJson.asAdditionalData()?.deepLink ?: ""
        intent?.let {
            it.data = deepLink.toUri()
            it.flags = FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TASK
            context.startActivity(it)
        }
    }
}
