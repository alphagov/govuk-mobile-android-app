package uk.govuk.app.local.navigation

import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import uk.govuk.app.local.ui.LocalEntryRoute
import uk.govuk.app.local.ui.LocalRoute

const val LOCAL_GRAPH_ROUTE = "local_graph_route"
private const val LOCAL_ROUTE = "local_route"
const val LOCAL_EDIT_ROUTE = "local_entry_route"

fun NavGraphBuilder.localGraph(
    navController: NavHostController,
    onCancel: () -> Unit,
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
        composable(
            LOCAL_EDIT_ROUTE,
        ) {
            LocalEntryRoute(
                onBack = { navController.popBackStack() },
                onCancel = onCancel,
                modifier = modifier
            )
        }
    }
}

fun NavController.navigateToLocalEdit() {
    navigate(LOCAL_EDIT_ROUTE)
}

