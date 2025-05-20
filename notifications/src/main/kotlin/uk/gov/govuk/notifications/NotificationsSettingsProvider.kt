package uk.gov.govuk.notifications

import android.content.Context
import android.content.Intent
import android.provider.Settings

internal fun openDeviceSettings(
    context: Context,
    intent: Intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
) {
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        .putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
        .also {
            context.startActivity(intent)
        }
}
