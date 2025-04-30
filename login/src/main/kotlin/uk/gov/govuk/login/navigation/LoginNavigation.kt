package uk.gov.govuk.login.navigation

import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import uk.gov.govuk.login.ui.BiometricRoute
import uk.gov.govuk.login.ui.LoginRoute

const val LOGIN_GRAPH_ROUTE = "login_graph_route"
private const val POST_SIGN_OUT_ARG = "isPostSignOut"
private const val LOGIN_ROUTE = "login_route?$POST_SIGN_OUT_ARG={$POST_SIGN_OUT_ARG}"
const val BIOMETRIC_ROUTE = "biometric_route?$POST_SIGN_OUT_ARG={$POST_SIGN_OUT_ARG}"

fun NavGraphBuilder.loginGraph(
    navController: NavController,
    onCompleted: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    navigation(
        route = LOGIN_GRAPH_ROUTE,
        startDestination = LOGIN_ROUTE
    ) {
        composable(
            route = LOGIN_ROUTE,
            arguments = listOf(
                navArgument(POST_SIGN_OUT_ARG) {
                    type = NavType.BoolType
                    defaultValue = false
                    nullable = false
                }
            )
        ) { backStackEntry ->
            val isPostSignOut = backStackEntry.arguments?.getBoolean(POST_SIGN_OUT_ARG) ?: false
            LoginRoute(
                isPostSignOut = isPostSignOut,
                onLogin = { authenticationEnabled ->
                    if (authenticationEnabled) {
                        navController.popBackStack()
                        navController.navigate(
                            BIOMETRIC_ROUTE
                                .replace("{$POST_SIGN_OUT_ARG}", isPostSignOut.toString())
                        )
                    } else {
                        onCompleted(isPostSignOut)
                    }
                },
                modifier = modifier,
            )
        }
        composable(
            route = BIOMETRIC_ROUTE,
            arguments = listOf(
                navArgument(POST_SIGN_OUT_ARG) {
                    type = NavType.BoolType
                    defaultValue = false
                    nullable = false
                }
            )
        ) { backStackEntry ->
            val isPostSignOut = backStackEntry.arguments?.getBoolean(POST_SIGN_OUT_ARG) ?: false

            BiometricRoute(
                onCompleted = { onCompleted(isPostSignOut) },
                modifier = modifier
            )
        }
    }
}

fun NavController.navigateToLoginPostSignOut() {
    navigate(LOGIN_ROUTE.replace("{$POST_SIGN_OUT_ARG}", "true")) {
        popUpTo(0) { inclusive = true }
    }
}