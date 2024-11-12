package uk.govuk.app.visited.navigation

import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import uk.govuk.app.visited.ui.VisitedRoute

const val VISITED_GRAPH_ROUTE = "visited_graph_route"
const val VISITED_ROUTE = "visited_route"

fun NavGraphBuilder.visitedGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    navigation(
        route = VISITED_GRAPH_ROUTE,
        startDestination = VISITED_ROUTE
    ) {
        composable(
            VISITED_ROUTE,
        ) {
            VisitedRoute(
                onBack = { navController.popBackStack() },
                modifier = modifier
            )
        }
    }
}
