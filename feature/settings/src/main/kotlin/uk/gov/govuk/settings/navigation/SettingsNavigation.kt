package uk.gov.govuk.settings.navigation

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import androidx.navigation.NavDeepLink
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import uk.gov.govuk.notifications.navigation.NOTIFICATIONS_ONBOARDING_NO_SKIP_ROUTE
import uk.gov.govuk.settings.BuildConfig.ACCESSIBILITY_STATEMENT_URL
import uk.gov.govuk.settings.BuildConfig.ACCOUNT_URL
import uk.gov.govuk.settings.BuildConfig.HELP_AND_FEEDBACK_URL
import uk.gov.govuk.settings.BuildConfig.PRIVACY_POLICY_URL
import uk.gov.govuk.settings.BuildConfig.TERMS_AND_CONDITIONS_URL
import uk.gov.govuk.settings.ui.SettingsRoute
import uk.gov.govuk.settings.ui.SettingsRouteActions
import java.net.URLEncoder


const val SETTINGS_GRAPH_ROUTE = "settings_graph_route"
private const val SETTINGS_ROUTE = "settings_route"

fun NavGraphBuilder.settingsGraph(
    navigateTo: (String) -> Unit,
    appVersion: String,
    deepLinks: (path: String) -> List<NavDeepLink>,
    modifier: Modifier = Modifier
) {
    navigation(
        route = SETTINGS_GRAPH_ROUTE,
        startDestination = SETTINGS_ROUTE
    ) {
        composable(
            SETTINGS_ROUTE, deepLinks = deepLinks("/settings")
        ) {
            val context = LocalContext.current

            SettingsRoute(
                appVersion = appVersion,
                actions = SettingsRouteActions(
                    onAccountClick = {
                        openInBrowser(context, ACCOUNT_URL)
                    },
                    onNotificationsClick = {
                        navigateTo(NOTIFICATIONS_ONBOARDING_NO_SKIP_ROUTE)
                    },
                    onPrivacyPolicyClick = {
                        openInBrowser(context, PRIVACY_POLICY_URL)
                    },
                    onHelpClick = {
                        navigateToHelpAndFeedback(context, appVersion)
                    },
                    onAccessibilityStatementClick = {
                        openInBrowser(context, ACCESSIBILITY_STATEMENT_URL)
                    },
                    onOpenSourceLicenseClick = {
                        val intent = Intent(context, OssLicensesMenuActivity::class.java)
                        context.startActivity(intent)
                    },
                    onTermsAndConditionsClick = {
                        openInBrowser(context, TERMS_AND_CONDITIONS_URL)
                    }
                ),
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
    intent.data = url.toUri()
    context.startActivity(intent)
}
