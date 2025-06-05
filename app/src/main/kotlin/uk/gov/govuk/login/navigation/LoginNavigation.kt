package uk.gov.govuk.login.navigation

import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import uk.gov.govuk.login.ui.BiometricRoute
import uk.gov.govuk.login.ui.ErrorRoute
import uk.gov.govuk.login.ui.LoginRoute

const val LOGIN_GRAPH_ROUTE = "login_graph_route"
const val LOGIN_ROUTE = "login_route"
const val BIOMETRIC_ROUTE = "biometric_route"
const val ERROR_ROUTE = "login_error_route"

fun NavGraphBuilder.loginGraph(
    navController: NavController,
    onLoginCompleted: (Boolean) -> Unit,
    onBiometricSetupCompleted: () -> Unit,
    modifier: Modifier = Modifier
) {
    navigation(
        route = LOGIN_GRAPH_ROUTE,
        startDestination = LOGIN_ROUTE
    ) {
        composable(route = LOGIN_ROUTE) {
            LoginRoute(
                navController = navController,
                onLoginCompleted = { isDifferentUser ->
                    onLoginCompleted(isDifferentUser)
                },
                modifier = modifier,
            )
        }
        composable(BIOMETRIC_ROUTE) {
            BiometricRoute(
                onCompleted = { onBiometricSetupCompleted() },
                modifier = modifier
            )
        }
        composable(
            route = ERROR_ROUTE,
        ) {
            ErrorRoute(
                onBack = { navController.popBackStack() },
                modifier = modifier
            )
        }
    }
}

fun NavController.navigateToErrorScreen() {
    navigate(ERROR_ROUTE)
}
