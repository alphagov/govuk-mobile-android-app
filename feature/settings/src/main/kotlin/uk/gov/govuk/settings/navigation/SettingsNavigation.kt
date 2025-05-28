package uk.gov.govuk.settings.navigation

import android.content.Intent
import android.os.Build
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.NavDeepLink
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import uk.gov.govuk.design.ui.component.rememberCustomTabsLauncher
import uk.gov.govuk.notifications.navigation.NOTIFICATIONS_PERMISSION_GRAPH_ROUTE
import uk.gov.govuk.settings.BuildConfig.ACCESSIBILITY_STATEMENT_URL
import uk.gov.govuk.settings.BuildConfig.ACCOUNT_URL
import uk.gov.govuk.settings.BuildConfig.HELP_AND_FEEDBACK_URL
import uk.gov.govuk.settings.BuildConfig.PRIVACY_POLICY_URL
import uk.gov.govuk.settings.BuildConfig.TERMS_AND_CONDITIONS_URL
import uk.gov.govuk.settings.ui.SettingsRoute
import uk.gov.govuk.settings.ui.SettingsRouteActions
import uk.gov.govuk.settings.ui.SignOutErrorRoute
import uk.gov.govuk.settings.ui.SignOutRoute
import java.net.URLEncoder


const val SETTINGS_GRAPH_ROUTE = "settings_graph_route"
private const val SETTINGS_ROUTE = "settings_route"

const val SIGN_OUT_GRAPH_ROUTE = "sign_out_graph_route"
private const val SIGN_OUT_ROUTE = "sign_out_route"

const val SIGN_OUT_ERROR_ROUTE = "sign_out_error_route"

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
            val customTabsLauncher = rememberCustomTabsLauncher()
            SettingsRoute(
                appVersion = appVersion,
                actions = SettingsRouteActions(
                    onAccountClick = {
                        customTabsLauncher.launch(context, ACCOUNT_URL)
                    },
                    onSignOutClick = {
                        navigateTo(SIGN_OUT_GRAPH_ROUTE)
                    },
                    onNotificationsClick = {
                        navigateTo(NOTIFICATIONS_PERMISSION_GRAPH_ROUTE)
                    },
                    onPrivacyPolicyClick = {
                        customTabsLauncher.launch(context, PRIVACY_POLICY_URL)
                    },
                    onHelpClick = {
                        val url = getHelpAndFeedbackUrl(appVersion)
                        customTabsLauncher.launch(context, url)
                    },
                    onAccessibilityStatementClick = {
                        customTabsLauncher.launch(context, ACCESSIBILITY_STATEMENT_URL)
                    },
                    onOpenSourceLicenseClick = {
                        val intent = Intent(context, OssLicensesMenuActivity::class.java)
                        context.startActivity(intent)
                    },
                    onTermsAndConditionsClick = {
                        customTabsLauncher.launch(context, TERMS_AND_CONDITIONS_URL)
                    }
                ),
                modifier = modifier
            )
        }
    }
}

fun NavGraphBuilder.signOutGraph(
    navController: NavController,
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier
) {
    navigation(
        route = SIGN_OUT_GRAPH_ROUTE,
        startDestination = SIGN_OUT_ROUTE
    ) {
        composable(SIGN_OUT_ROUTE) {
            SignOutRoute(
                navController = navController,
                onBack = { navController.popBackStack() },
                onSignOut = onSignOut
            )
        }
        composable(
            route = SIGN_OUT_ERROR_ROUTE,
        ) {
            SignOutErrorRoute(
                onBack = { navController.navigate(SETTINGS_ROUTE) },
                modifier = modifier
            )
        }
    }
}

fun NavController.navigateToErrorScreen() {
    navigate(SIGN_OUT_ERROR_ROUTE)
}

fun getHelpAndFeedbackUrl(
    appVersion: String
): String {
    val deviceInfo = "${Build.MANUFACTURER} ${Build.MODEL} ${Build.VERSION.RELEASE}"
    return "$HELP_AND_FEEDBACK_URL?" +
            "app_version=$appVersion&" +
            "phone=${URLEncoder.encode(deviceInfo, "UTF-8")}"
}
