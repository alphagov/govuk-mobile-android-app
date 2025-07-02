package uk.gov.govuk.visited.navigation

import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import uk.gov.govuk.visited.ui.EditVisitedRoute
import uk.gov.govuk.visited.ui.VisitedRoute

const val VISITED_GRAPH_ROUTE = "visited_graph_route"
const val VISITED_ROUTE = "visited_route"
const val EDIT_VISITED_ROUTE = "edit_visited_route"

val visitedDeepLinks = mapOf(
    "/visited" to VISITED_ROUTE,
    "/visited/edit" to EDIT_VISITED_ROUTE
)

fun NavGraphBuilder.visitedGraph(
    navController: NavHostController,
    launchBrowser: (url: String) -> Unit,
    modifier: Modifier = Modifier
) {
    navigation(
        route = VISITED_GRAPH_ROUTE,
        startDestination = VISITED_ROUTE
    ) {
        composable(VISITED_ROUTE) {
            VisitedRoute(
                navController = navController,
                onBack = { navController.popBackStack() },
                launchBrowser = launchBrowser,
                modifier = modifier
            )
        }
        composable(EDIT_VISITED_ROUTE) {
            EditVisitedRoute(
                onBack = { navController.popBackStack() },
                modifier = modifier
            )
        }
    }
}

fun NavController.navigateToEditVisited() {
    navigate(EDIT_VISITED_ROUTE)
}
