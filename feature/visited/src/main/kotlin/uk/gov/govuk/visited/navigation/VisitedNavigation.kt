package uk.gov.govuk.visited.navigation

import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavDeepLink
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import uk.gov.govuk.auth.navigation.authenticatedComposable
import uk.gov.govuk.visited.ui.EditVisitedRoute
import uk.gov.govuk.visited.ui.VisitedRoute

const val VISITED_GRAPH_ROUTE = "visited_graph_route"
const val VISITED_ROUTE = "visited_route"
const val EDIT_VISITED_ROUTE = "edit_visited_route"

fun NavGraphBuilder.visitedGraph(
    navController: NavHostController,
    deepLinks: (path: String) -> List<NavDeepLink>,
    launchBrowser: (url: String) -> Unit,
    showLogin: () -> Unit,
    modifier: Modifier = Modifier
) {
    navigation(
        route = VISITED_GRAPH_ROUTE,
        startDestination = VISITED_ROUTE
    ) {
        authenticatedComposable(
            route = VISITED_ROUTE,
            deepLinks = deepLinks("/visited"),
            showLogin = showLogin
        ) {
            VisitedRoute(
                navController = navController,
                onBack = { navController.popBackStack() },
                launchBrowser = launchBrowser,
                modifier = modifier
            )
        }
        authenticatedComposable(
            route = EDIT_VISITED_ROUTE,
            deepLinks = deepLinks("/visited/edit"),
            showLogin = showLogin
        ) {
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
