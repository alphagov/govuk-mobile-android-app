package uk.gov.govuk.chat.navigation

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import uk.gov.govuk.chat.ui.ChatOptInRoute
import uk.gov.govuk.chat.ui.ChatRoute
import uk.gov.govuk.chat.ui.OnboardingPageOneRoute
import uk.gov.govuk.chat.ui.OnboardingPageTwoRoute

const val CHAT_GRAPH_ROUTE = "chat_graph_route"
private const val CHAT_ROUTE = "chat_route"
private const val CHAT_ONBOARDING_PAGE_1_ROUTE = "chat_onboarding_1_route"
private const val CHAT_ONBOARDING_PAGE_2_ROUTE = "chat_onboarding_2_route"
const val CHAT_OPT_IN_GRAPH_ROUTE = "chat_opt_in_graph_route"
const val CHAT_OPT_IN_ROUTE = "chat_opt_in_route"
private const val HOME_ROUTE = "home_route"

fun NavGraphBuilder.chatGraph(
    navController: NavHostController,
    launchBrowser: (url: String) -> Unit,
    onAuthError: () -> Unit,
    modifier: Modifier = Modifier
) {
    navigation(
        route = CHAT_GRAPH_ROUTE,
        startDestination = CHAT_ROUTE
    ) {
        composable(CHAT_ROUTE) {
            ChatRoute(
                onShowOnboarding = {
                    navController.navigateToOnboardingPageOne()
                },
                onClearDone = {
                    navController.navigateToChat()
                },
                launchBrowser = launchBrowser,
                onAuthError = onAuthError,
                modifier = modifier
            )
        }
        composable(CHAT_ONBOARDING_PAGE_1_ROUTE) {
            OnboardingPageOneRoute(
                onClick = {
                    navController.navigateToOnboardingPageTwo()
                },
                onCancel = {
                    popToChatEntryScreen(navController)
                },
                modifier = modifier
                    .windowInsetsPadding(WindowInsets.statusBars)
            )
        }
        composable(CHAT_ONBOARDING_PAGE_2_ROUTE) {
            OnboardingPageTwoRoute(
                onClick = {
                    navController.navigateToChat()
                },
                onCancel = {
                    popToChatEntryScreen(navController)
                },
                onBack = {
                    navController.navigateToOnboardingPageOne()
                },
                modifier = modifier
                    .windowInsetsPadding(WindowInsets.statusBars)
            )
        }
    }
}

fun NavGraphBuilder.chatOptInGraph(
    navController: NavHostController,
    launchBrowser: (url: String) -> Unit,
    modifier: Modifier = Modifier
) {
    navigation(
        route = CHAT_OPT_IN_GRAPH_ROUTE,
        startDestination = CHAT_OPT_IN_ROUTE
    ) {
        composable(CHAT_OPT_IN_ROUTE) {
            ChatOptInRoute(
                launchBrowser = launchBrowser,
                navigateToHome = {
                    navController.navigateHome()
                },
                modifier = modifier
                    .windowInsetsPadding(WindowInsets.statusBars)
            )
        }
    }
}

private fun popToChatEntryScreen(navController: NavController) {
    navController.popBackStack(route = "chat_route", inclusive = true)
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

fun NavController.navigateHome() {
    navigate(HOME_ROUTE)
}
