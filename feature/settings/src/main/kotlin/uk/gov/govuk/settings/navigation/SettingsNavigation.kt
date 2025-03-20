package uk.gov.govuk.settings.navigation

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import androidx.navigation.navigation
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import uk.gov.govuk.notifications.navigation.NOTIFICATIONS_ONBOARDING_NO_SKIP_ROUTE
import uk.gov.govuk.settings.BuildConfig.ACCESSIBILITY_STATEMENT_URL
import uk.gov.govuk.settings.BuildConfig.HELP_AND_FEEDBACK_URL
import uk.gov.govuk.settings.BuildConfig.PRIVACY_POLICY_URL
import uk.gov.govuk.settings.BuildConfig.TERMS_AND_CONDITIONS_URL
import uk.gov.govuk.settings.ui.SettingsRoute
import java.net.URLEncoder


const val SETTINGS_GRAPH_ROUTE = "settings_graph_route"
private const val SETTINGS_ROUTE = "settings_route"

fun NavGraphBuilder.settingsGraph(
    navigateTo: (String) -> Unit,
    appVersion: String,
    modifier: Modifier = Modifier
) {
    navigation(
        route = SETTINGS_GRAPH_ROUTE,
        startDestination = SETTINGS_ROUTE
    ) {
        composable(
            SETTINGS_ROUTE, deepLinks = listOf(
                navDeepLink {
                    uriPattern = "govuk://app/settings"
                }
            )
        ) {
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
                    navigateTo(NOTIFICATIONS_ONBOARDING_NO_SKIP_ROUTE)
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
