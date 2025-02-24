package uk.govuk.app.settings.navigation

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import uk.govuk.app.notifications.navigation.NOTIFICATIONS_GRAPH_ROUTE
import uk.govuk.app.notifications.notificationsPermissionShouldShowRationale
import uk.govuk.app.settings.BuildConfig.ACCESSIBILITY_STATEMENT_URL
import uk.govuk.app.settings.BuildConfig.HELP_AND_FEEDBACK_URL
import uk.govuk.app.settings.BuildConfig.PRIVACY_POLICY_URL
import uk.govuk.app.settings.BuildConfig.TERMS_AND_CONDITIONS_URL
import uk.govuk.app.settings.R
import uk.govuk.app.settings.ui.SettingsRoute
import java.net.URLEncoder


const val SETTINGS_GRAPH_ROUTE = "settings_graph_route"
private const val SETTINGS_ROUTE = "settings_route"

fun NavGraphBuilder.settingsGraph(
    navController: NavController,
    appVersion: String,
    modifier: Modifier = Modifier
) {
    navigation(
        route = SETTINGS_GRAPH_ROUTE,
        startDestination = SETTINGS_ROUTE
    ) {
        composable(SETTINGS_ROUTE) {
            val context = LocalContext.current

            SettingsRoute(
                appVersion = appVersion,
                onHelpClick = {
                    navigateToHelpAndFeedback(context, appVersion)
                },
                onPrivacyPolicyClick = {
                    openInBrowser(context, PRIVACY_POLICY_URL)
                },
                onAccessibilityStatementClick = {
                    openInBrowser(context, ACCESSIBILITY_STATEMENT_URL)
                },
                onTermsAndConditionsClick = {
                    openInBrowser(context, TERMS_AND_CONDITIONS_URL)
                },
                onOpenSourceLicenseClick = {
                    val intent = Intent(context, OssLicensesMenuActivity::class.java)
                    context.startActivity(intent)
                },
                onNotificationsClick = {
                    handleNotificationsClick(context, navController)
                },
                modifier = modifier
            )
        }
    }
}

fun navigateToHelpAndFeedback(
    context: Context,
    appVersion: String
) {
    val deviceInfo = "${Build.MANUFACTURER} ${Build.MODEL} ${Build.VERSION.RELEASE}"
    val url = "$HELP_AND_FEEDBACK_URL?" +
            "app_version=$appVersion&" +
            "phone=${URLEncoder.encode(deviceInfo, "UTF-8")}"
    openInBrowser(context, url)
}

private fun openInBrowser(context: Context, url: String) {
    val intent = Intent(Intent.ACTION_VIEW)
    intent.data = Uri.parse(url)
    context.startActivity(intent)
}

private fun openDeviceSettings(context: Context, setting: String) {
    val intent: Intent = Intent(setting)
        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        .putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
    context.startActivity(intent)
}

private fun showAlertDialog(
    context: Context,
    title: String,
    message: String,
    positiveTitle: String,
    onPositiveClick: (() -> Unit)? = null
) {
    val builder = AlertDialog.Builder(context)
    builder.setTitle(title)
    builder.setMessage(message)
    onPositiveClick?.let { positiveClicked ->
        builder.setPositiveButton(positiveTitle) { dialog, _ ->
            positiveClicked()
            dialog.dismiss()
        }
    }
    builder.show()
}

private fun handleNotificationsClick(context: Context, navController: NavController) {
    if (notificationsPermissionShouldShowRationale(context as Activity)) {
        navController.navigate(NOTIFICATIONS_GRAPH_ROUTE)
        return
    }

    val alertBody =
        if (context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)
            == PackageManager.PERMISSION_GRANTED
        ) {
            R.string.notifications_granted_alert_dialog_body
        } else {
            R.string.notifications_denied_alert_dialog_body
        }
    showAlertDialog(
        context,
        context.getString(R.string.notifications_title),
        context.getString(alertBody),
        context.getString(R.string.open_settings)
    ) {
        openDeviceSettings(context, Settings.ACTION_APP_NOTIFICATION_SETTINGS)
    }
}
