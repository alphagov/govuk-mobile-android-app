package uk.gov.govuk.login.navigation

import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import uk.gov.govuk.login.ui.BiometricRoute

const val LOGIN_GRAPH_ROUTE = "login_graph_route"
private const val BIOMETRIC_ROUTE = "biometric_route"

fun NavGraphBuilder.loginGraph(
    onComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    navigation(
        route = LOGIN_GRAPH_ROUTE,
        startDestination = BIOMETRIC_ROUTE
    ) {
        composable(
            BIOMETRIC_ROUTE,
        ) {
            BiometricRoute(
                onComplete = onComplete,
                modifier = modifier
            )
        }
    }
}