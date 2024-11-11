package uk.govuk.app.navigation

import androidx.navigation.NavHostController
import uk.govuk.app.AppUiState
import uk.govuk.app.analytics.navigation.ANALYTICS_GRAPH_ROUTE
import uk.govuk.app.home.navigation.HOME_GRAPH_ROUTE
import uk.govuk.app.onboarding.navigation.ONBOARDING_GRAPH_ROUTE
import uk.govuk.app.topics.navigation.TOPICS_GRAPH_ROUTE
import java.util.Stack

internal class AppLaunchNavigation(
    private val navController: NavHostController,
    uiState: AppUiState
) {
    val routeStack: Stack<String> = Stack()
    val startDestination: String
        get() {
            return routeStack.pop()
        }

    init {
        routeStack.push(HOME_GRAPH_ROUTE)

        if (uiState.shouldDisplayTopicSelection) {
            routeStack.push(TOPICS_GRAPH_ROUTE)
        }

        if (uiState.shouldDisplayOnboarding) {
            routeStack.push(ONBOARDING_GRAPH_ROUTE)
        }

        if (uiState.shouldDisplayAnalyticsConsent) {
            routeStack.push(ANALYTICS_GRAPH_ROUTE)
        }

        if (uiState.shouldDisplayRecommendUpdate) {
            routeStack.push(RECOMMEND_UPDATE_GRAPH_ROUTE)
        }

        if (uiState.shouldDisplayAppUnavailable) {
            routeStack.push(APP_UNAVAILABLE_GRAPH_ROUTE)
        }
    }

    fun next() {
        navController.popBackStack()
        navController.navigate(routeStack.pop())
    }
}