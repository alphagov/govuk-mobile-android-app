package uk.gov.govuk.visited.navigation

import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import androidx.navigation.navigation
import uk.gov.govuk.visited.ui.EditVisitedRoute
import uk.gov.govuk.visited.ui.VisitedRoute

const val VISITED_GRAPH_ROUTE = "visited_graph_route"
const val VISITED_ROUTE = "visited_route"
const val EDIT_VISITED_ROUTE = "edit_visited_route"

fun NavGraphBuilder.visitedGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    navigation(
        route = VISITED_GRAPH_ROUTE,
        startDestination = VISITED_ROUTE
    ) {
        composable(VISITED_ROUTE, deepLinks = listOf(
            navDeepLink {
                uriPattern = "govuk://app/visited"
            }
        )) {
            VisitedRoute(
                navController = navController,
                onBack = { navController.popBackStack() },
                modifier = modifier
            )
        }
        composable(EDIT_VISITED_ROUTE, deepLinks = listOf(
            navDeepLink {
                uriPattern = "govuk://app/visited/edit"
            }
        )) {
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
