package uk.govuk.app.home.ui.navigation

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import androidx.navigation.navigation
import uk.govuk.app.home.ui.HomeRoute

const val HOME_GRAPH_ROUTE = "home_graph_route"
private const val HOME_ROUTE = "home_route"

fun NavGraphBuilder.homeGraph(
    widget: @Composable () -> Unit
) {
    navigation(
        route = HOME_GRAPH_ROUTE,
        startDestination = HOME_ROUTE
    ) {
        composable(HOME_ROUTE,
            deepLinks = listOf(
                navDeepLink {
                    uriPattern = "/home"
                    action = Intent.ACTION_VIEW
                }
            )
        ) { HomeRoute(widget) }
    }
}
