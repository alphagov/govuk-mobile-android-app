package uk.govuk.app.biometrics.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import uk.govuk.app.biometrics.ui.BiometricsRoute

const val BIOMETRICS_GRAPH_ROUTE = "biometrics_graph_route"
private const val BIOMETRICS_LOGIN_ROUTE = "biometrics_login_route"

fun NavGraphBuilder.biometricsGraph(
    onSuccess: () -> Unit
) {
    navigation(
        route = BIOMETRICS_GRAPH_ROUTE,
        startDestination = BIOMETRICS_LOGIN_ROUTE
    ) {
        composable(BIOMETRICS_LOGIN_ROUTE) {
            BiometricsRoute(onSuccess)
        }
    }
}