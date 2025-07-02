package uk.gov.govuk.login.navigation

import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import uk.gov.govuk.login.ui.BiometricRoute
import uk.gov.govuk.login.ui.BiometricSettingsRoute
import uk.gov.govuk.login.ui.ErrorRoute
import uk.gov.govuk.login.ui.LoginRoute
import uk.gov.govuk.login.ui.LoginSuccessRoute

const val LOGIN_GRAPH_ROUTE = "login_graph_route"
const val LOGIN_ROUTE = "login_route"
private const val LOGIN_SUCCESS_ROUTE = "login_success_route"
private const val BIOMETRIC_ROUTE = "biometric_route"
const val BIOMETRIC_SETTINGS_ROUTE = "biometric_settings_route"
private const val ERROR_ROUTE = "login_error_route"

fun NavGraphBuilder.loginGraph(
    navController: NavController,
    onLoginCompleted: () -> Unit,
    modifier: Modifier = Modifier
) {
    navigation(
        route = LOGIN_GRAPH_ROUTE,
        startDestination = LOGIN_ROUTE
    ) {
        composable(route = LOGIN_ROUTE) {
            LoginRoute(
                onLoginCompleted = { loginEvent ->
                    if (loginEvent.isBiometricLogin) {
                        onLoginCompleted()
                    } else {
                        navController.popBackStack()
                        navController.navigate(LOGIN_SUCCESS_ROUTE)
                    }
                },
                onError = {
                    navController.navigate(ERROR_ROUTE)
                },
                modifier = modifier,
            )
        }
        composable(route = LOGIN_SUCCESS_ROUTE) {
            // Todo - not sure I like the login success screen knowing/caring about biometrics!!!
            LoginSuccessRoute(
                onLoginSuccessCompleted = { isBiometricsEnabled ->
                    if (isBiometricsEnabled) {
                        navController.popBackStack()
                        navController.navigate(BIOMETRIC_ROUTE)
                    } else {
                        onLoginCompleted()
                    }
                },
                modifier = modifier
            )
        }
        composable(BIOMETRIC_ROUTE) {
            BiometricRoute(
                onCompleted = { onLoginCompleted() },
                modifier = modifier
            )
        }
        composable(BIOMETRIC_SETTINGS_ROUTE) {
            BiometricSettingsRoute(
                onBack = { navController.popBackStack() },
                modifier = modifier
            )
        }
        composable(route = ERROR_ROUTE) {
            ErrorRoute(
                onBack = { navController.popBackStack() },
                modifier = modifier
            )
        }
    }
}