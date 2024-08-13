package uk.govuk.app.settings.navigation

import android.content.Intent
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import androidx.navigation.navigation
import uk.govuk.app.settings.ui.SettingsRoute
import uk.govuk.app.settings.ui.SettingsSubRoute

const val SETTINGS_GRAPH_ROUTE = "settings_graph_route"
private const val SETTINGS_ROUTE = "settings_route"
private const val SETTINGS_SUB_ROUTE = "settings_sub_route"

fun NavGraphBuilder.settingsGraph(navController: NavController) {
    navigation(
        route = SETTINGS_GRAPH_ROUTE,
        startDestination = SETTINGS_ROUTE
    ) {
        composable(
            SETTINGS_ROUTE,
            deepLinks = listOf(
                navDeepLink {
                    uriPattern = "/settings"
                    action = Intent.ACTION_VIEW
                }
            )
        ) {
            SettingsRoute {
                navController.navigateToSettingsSubScreen()
            }
        }
        composable(
            SETTINGS_SUB_ROUTE,
            deepLinks = listOf(
                navDeepLink {
                    uriPattern = "/subroute"
                    action = Intent.ACTION_VIEW
                }
            )
        ) { SettingsSubRoute() }
    }
}

private fun NavController.navigateToSettingsSubScreen() = navigate(SETTINGS_SUB_ROUTE)
