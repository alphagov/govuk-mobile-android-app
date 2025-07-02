package uk.gov.govuk.notifications.ui

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.core.app.NotificationManagerCompat
import uk.gov.govuk.notifications.R

internal fun showNotificationsAlert(
    context: Context,
    onContinueButtonClick: (String) -> Unit,
    onCancelButtonClick: (String) -> Unit
) {
    val isNotificationsOn = NotificationManagerCompat.from(context).areNotificationsEnabled()
    val alertTitle =
        if (isNotificationsOn) R.string.notifications_alert_title_off else R.string.notifications_alert_title_on
    val alertMessage =
        if (isNotificationsOn) R.string.notifications_alert_message_off else R.string.notifications_alert_message_on
    val cancelButton = context.getString(R.string.cancel_button)
    val continueButton = context.getString(R.string.continue_button)

    AlertDialog.Builder(context).apply {
        setTitle(context.getString(alertTitle))
        setMessage(context.getString(alertMessage))
        setNeutralButton(cancelButton) { dialog, _ ->
            onCancelButtonClick(cancelButton)
            dialog.dismiss()
        }
        setPositiveButton(continueButton) { dialog, _ ->
            onContinueButtonClick(continueButton)
            openDeviceNotificationsSettings(context)
            dialog.dismiss()
        }
    }.also { notificationsAlert ->
        notificationsAlert.show()
    }
}

private fun openDeviceNotificationsSettings(
    context: Context
) {
    Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
    }.also { intent ->
        context.startActivity(intent)
    }
}
