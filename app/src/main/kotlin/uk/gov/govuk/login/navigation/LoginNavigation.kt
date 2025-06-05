package uk.gov.govuk.login.navigation

import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import uk.gov.govuk.login.ui.BiometricRoute
import uk.gov.govuk.login.ui.ErrorRoute
import uk.gov.govuk.login.ui.WelcomeRoute

const val WELCOME_GRAPH_ROUTE = "welcome_graph_route"
private const val WELCOME_ROUTE = "welcome_route"
const val BIOMETRIC_ROUTE = "biometric_route"
const val ERROR_ROUTE = "login_error_route"

fun NavGraphBuilder.loginGraph(
    navController: NavController,
    isComplete: () -> Unit,
    onLoginCompleted: (Boolean) -> Unit,
    onBiometricSetupCompleted: () -> Unit,
    modifier: Modifier = Modifier
) {
    navigation(
        route = WELCOME_GRAPH_ROUTE,
        startDestination = WELCOME_ROUTE
    ) {
        composable(WELCOME_ROUTE) {
            WelcomeRoute(
                navController = navController,
                isComplete = isComplete,
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

fun NavController.navigateToWelcomeScreen() {
    navigate(WELCOME_ROUTE)
}

fun NavController.navigateToErrorScreen() {
    navigate(ERROR_ROUTE)
}
