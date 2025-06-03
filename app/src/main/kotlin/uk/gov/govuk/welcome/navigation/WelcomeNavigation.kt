package uk.gov.govuk.welcome.navigation

import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import uk.gov.govuk.welcome.ui.WelcomeRoute

const val WELCOME_GRAPH_ROUTE = "welcome_graph_route"
private const val WELCOME_ROUTE = "welcome_route"

fun NavGraphBuilder.welcomeGraph(
    navController: NavHostController,
    onboardingCompleted: () -> Unit,
    onLoginCompleted: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    navigation(
        route = WELCOME_GRAPH_ROUTE,
        startDestination = WELCOME_ROUTE
    ) {
        composable(WELCOME_ROUTE) {
            WelcomeRoute(
                navController = navController,
                onboardingCompleted = onboardingCompleted,
                onLoginCompleted = onLoginCompleted,
                modifier = modifier,
            )
        }
    }
}
