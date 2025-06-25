package uk.gov.govuk.search.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.ui.Modifier
import androidx.navigation.NavDeepLink
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import uk.gov.govuk.auth.navigation.authenticatedComposable
import uk.gov.govuk.search.ui.SearchRoute

const val SEARCH_GRAPH_ROUTE = "search_graph_route"
private const val SEARCH_ROUTE = "search_route"

fun NavGraphBuilder.searchGraph(
    navController: NavHostController,
    deepLinks: (path: String) -> List<NavDeepLink>,
    launchBrowser: (url: String) -> Unit,
    showLogin: () -> Unit,
    modifier: Modifier = Modifier
) {
    navigation(
        route = SEARCH_GRAPH_ROUTE,
        startDestination = SEARCH_ROUTE
    ) {
        authenticatedComposable(
            SEARCH_ROUTE, deepLinks = deepLinks("/search"),
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
            popEnterTransition = { EnterTransition.None },
            popExitTransition = { ExitTransition.None },
            showLogin = showLogin
        ) {
            SearchRoute(
                onBack = { navController.popBackStack() },
                launchBrowser = launchBrowser,
                modifier = modifier
            )
        }
    }
}
