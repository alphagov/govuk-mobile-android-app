package uk.govuk.app.onboarding.ui.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import uk.govuk.app.onboarding.ui.OnboardingRoute

const val ONBOARDING_GRAPH_ROUTE = "onboarding_graph_route"
private const val ONBOARDING_ROUTE = "onboarding_route"

fun NavGraphBuilder.onboardingGraph(onboardingCompleted: () -> Unit) {
    navigation(
        route = ONBOARDING_GRAPH_ROUTE,
        startDestination = ONBOARDING_ROUTE
    ) {
        composable(ONBOARDING_ROUTE) { OnboardingRoute(onboardingCompleted) }
    }
}