package uk.gov.govuk.navigation

import uk.gov.govuk.AppUiState
import uk.gov.govuk.analytics.navigation.ANALYTICS_GRAPH_ROUTE
import uk.gov.govuk.home.navigation.HOME_GRAPH_ROUTE
import uk.gov.govuk.notifications.navigation.NOTIFICATIONS_GRAPH_ROUTE
import uk.gov.govuk.onboarding.navigation.ONBOARDING_GRAPH_ROUTE
import uk.gov.govuk.topics.navigation.TOPICS_GRAPH_ROUTE
import uk.govuk.app.biometrics.navigation.BIOMETRICS_GRAPH_ROUTE
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

        launchRoutes.push(BIOMETRICS_GRAPH_ROUTE)

        if (uiState.shouldDisplayOnboarding) {
            launchRoutes.push(ONBOARDING_GRAPH_ROUTE)
        }

        if (uiState.shouldDisplayAnalyticsConsent) {
            launchRoutes.push(ANALYTICS_GRAPH_ROUTE)
        }
    }
}