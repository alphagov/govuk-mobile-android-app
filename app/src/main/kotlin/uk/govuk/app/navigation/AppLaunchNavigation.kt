package uk.govuk.app.navigation

import uk.govuk.app.AppUiState
import uk.govuk.app.analytics.navigation.ANALYTICS_GRAPH_ROUTE
import uk.govuk.app.home.navigation.HOME_GRAPH_ROUTE
import uk.govuk.app.notifications.navigation.NOTIFICATIONS_GRAPH_ROUTE
import uk.govuk.app.onboarding.navigation.ONBOARDING_GRAPH_ROUTE
import uk.govuk.app.topics.navigation.TOPICS_GRAPH_ROUTE
import java.util.ArrayDeque
import java.util.Deque

internal class AppLaunchNavigation(
    private val uiState: AppUiState.Default
) {
    val launchRoutes: Deque<String> = ArrayDeque()

    init {
        setLaunchRoutes()
    }

    private fun setLaunchRoutes() {
        launchRoutes.push(HOME_GRAPH_ROUTE)

        if (uiState.shouldDisplayNotificationsOnboarding) {
            launchRoutes.push(NOTIFICATIONS_GRAPH_ROUTE)
        }

        if (uiState.shouldDisplayTopicSelection) {
            launchRoutes.push(TOPICS_GRAPH_ROUTE)
        }

        if (uiState.shouldDisplayOnboarding) {
            launchRoutes.push(ONBOARDING_GRAPH_ROUTE)
        }

        if (uiState.shouldDisplayAnalyticsConsent) {
            launchRoutes.push(ANALYTICS_GRAPH_ROUTE)
        }
    }
}