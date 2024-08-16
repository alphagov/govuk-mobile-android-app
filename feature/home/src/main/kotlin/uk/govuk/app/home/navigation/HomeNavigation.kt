package uk.govuk.app.home.navigation

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import androidx.navigation.navigation
import uk.govuk.app.home.ui.HomeRoute

const val HOME_GRAPH_ROUTE = "home_graph_route"
private const val HOME_ROUTE = "home_route"
const val HOME_GRAPH_START_DESTINATION = HOME_ROUTE

fun NavGraphBuilder.homeGraph(
    widgets: List<@Composable (Modifier) -> Unit>,
    modifier: Modifier = Modifier
) {
    navigation(
        route = HOME_GRAPH_ROUTE,
        startDestination = HOME_GRAPH_START_DESTINATION
    ) {
        composable(
            HOME_ROUTE,
            deepLinks = listOf(
                navDeepLink {
                    uriPattern = "/home"
                    action = Intent.ACTION_VIEW
                }
            )
        ) {
            HomeRoute(
                widgets = widgets,
                modifier = modifier
            )
        }
    }
}
