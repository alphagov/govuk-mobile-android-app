package uk.govuk.app.local.navigation

import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import uk.govuk.app.local.ui.EditLocalRoute
import uk.govuk.app.local.ui.LocalRoute

const val LOCAL_GRAPH_ROUTE = "local_graph_route"
private const val LOCAL_ROUTE = "local_route"
const val EDIT_LOCAL_ROUTE = "edit_local_route"

fun NavGraphBuilder.localGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    navigation(
        route = LOCAL_GRAPH_ROUTE,
        startDestination = LOCAL_ROUTE
    ) {
        composable(
            LOCAL_ROUTE,
        ) {
            LocalRoute(
                navController = navController,
                onBack = { navController.popBackStack() },
                modifier = modifier,
            )
        }
        composable(EDIT_LOCAL_ROUTE) {
            EditLocalRoute(
                onBack = { navController.popBackStack() },
                modifier = modifier
            )
        }
    }
}

fun NavController.navigateToEditLocal() {
    navigate(EDIT_LOCAL_ROUTE)
}
