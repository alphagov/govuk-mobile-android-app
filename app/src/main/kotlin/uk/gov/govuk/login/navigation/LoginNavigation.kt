package uk.gov.govuk.login.navigation

import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import uk.gov.govuk.auth.navigation.authenticatedComposable
import uk.gov.govuk.login.ui.BiometricRoute
import uk.gov.govuk.login.ui.BiometricSettingsRoute
import uk.gov.govuk.login.ui.ErrorRoute
import uk.gov.govuk.login.ui.LoginRoute
import uk.gov.govuk.login.ui.LoginSuccessRoute

const val BIOMETRIC_GRAPH_ROUTE = "biometric_graph_route"
private const val BIOMETRIC_ROUTE = "biometric_route"
const val LOGIN_GRAPH_ROUTE = "login_graph_route"
const val LOGIN_ROUTE = "login_route"
private const val LOGIN_SUCCESS_ROUTE = "login_success_route"
const val BIOMETRIC_SETTINGS_ROUTE = "biometric_settings_route"
const val ERROR_ROUTE = "login_error_route"

fun NavGraphBuilder.biometricGraph(
    onBiometricSetupCompleted: () -> Unit,
    showLogin: () -> Unit,
    modifier: Modifier = Modifier
) {
    navigation(
        route = BIOMETRIC_GRAPH_ROUTE,
        startDestination = BIOMETRIC_ROUTE
    ) {
        authenticatedComposable(
            route = BIOMETRIC_ROUTE,
            showLogin = showLogin
        ) {
            BiometricRoute(
                onCompleted = { onBiometricSetupCompleted() },
                modifier = modifier
            )
        }
    }
}

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
            LoginSuccessRoute(
                onContinue = onLoginCompleted,
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