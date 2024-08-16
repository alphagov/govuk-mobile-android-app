package uk.govuk.app.search.navigation

import android.content.Intent
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import androidx.navigation.navigation
import uk.govuk.app.search.ui.SearchRoute

const val SEARCH_GRAPH_ROUTE = "search_graph_route"
private const val SEARCH_ROUTE = "search_route"

fun NavGraphBuilder.searchGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    navigation(
        route = SEARCH_GRAPH_ROUTE,
        startDestination = SEARCH_ROUTE
    ) {
        composable(
            SEARCH_ROUTE,
            deepLinks = listOf(
                navDeepLink {
                    uriPattern = "/search"
                    action = Intent.ACTION_VIEW
                }
            )
        ) {
            SearchRoute(
                onBack = { navController.popBackStack() },
                modifier = modifier
            )
        }
    }
}