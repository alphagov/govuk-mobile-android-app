package uk.gov.govuk.chat.navigation

import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import uk.gov.govuk.chat.ui.ChatRoute
import uk.gov.govuk.chat.ui.OnboardingPageOneRoute
import uk.gov.govuk.chat.ui.OnboardingPageTwoRoute

const val CHAT_GRAPH_ROUTE = "chat_graph_route"
private const val CHAT_ROUTE = "chat_route"
private const val CHAT_ONBOARDING_PAGE_1_ROUTE = "chat_onboarding_1_route"
private const val CHAT_ONBOARDING_PAGE_2_ROUTE = "chat_onboarding_2_route"

fun NavGraphBuilder.chatGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    // TODO: If chat is 'seen' then startDestination is CHAT_ROUTE else CHAT_ONBOARDING_PAGE_1_ROUTE
    navigation(
        route = CHAT_GRAPH_ROUTE,
        startDestination = CHAT_ONBOARDING_PAGE_1_ROUTE
    ) {
        composable(CHAT_ROUTE) {
            ChatRoute(
                modifier = modifier
            )
        }
        composable(CHAT_ONBOARDING_PAGE_1_ROUTE) {
            OnboardingPageOneRoute(
                navController = navController,
                modifier = modifier
            )
        }
        composable(CHAT_ONBOARDING_PAGE_2_ROUTE) {
            OnboardingPageTwoRoute(
                navController = navController,
                modifier = modifier
            )
        }
    }
}

fun NavController.navigateToChat() {
    navigate(CHAT_ROUTE)
}

fun NavController.navigateToOnboardingPageOne() {
    navigate(CHAT_ONBOARDING_PAGE_1_ROUTE)
}

fun NavController.navigateToOnboardingPageTwo() {
    navigate(CHAT_ONBOARDING_PAGE_2_ROUTE)
}
