package uk.gov.govuk.onboarding.navigation

import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import uk.gov.govuk.onboarding.ui.OnboardingRoute

const val ONBOARDING_GRAPH_ROUTE = "onboarding_graph_route"
private const val ONBOARDING_ROUTE = "onboarding_route"

fun NavGraphBuilder.onboardingGraph(
    navController: NavHostController,
    onboardingCompleted: () -> Unit,
    onLoginCompleted: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    navigation(
        route = ONBOARDING_GRAPH_ROUTE,
        startDestination = ONBOARDING_ROUTE
    ) {
        composable(ONBOARDING_ROUTE) {
            OnboardingRoute(
                navController = navController,
                onboardingCompleted = onboardingCompleted,
                onLoginCompleted = onLoginCompleted,
                modifier = modifier,
            )
        }
    }
}
