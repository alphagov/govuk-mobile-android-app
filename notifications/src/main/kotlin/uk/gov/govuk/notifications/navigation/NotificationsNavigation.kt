package uk.gov.govuk.notifications.navigation

import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import uk.gov.govuk.notifications.ui.NotificationsOnboardingNoSkipRoute
import uk.gov.govuk.notifications.ui.NotificationsOnboardingRoute

const val NOTIFICATIONS_GRAPH_ROUTE = "notifications_graph_route"
private const val NOTIFICATIONS_ONBOARDING_ROUTE = "notifications_onboarding_route"
const val NOTIFICATIONS_ONBOARDING_NO_SKIP_ROUTE = "notifications_onboarding_no_skip_route"

fun NavGraphBuilder.notificationsGraph(
    notificationsOnboardingCompleted: () -> Unit,
    modifier: Modifier = Modifier
) {
    navigation(
        route = NOTIFICATIONS_GRAPH_ROUTE,
        startDestination = NOTIFICATIONS_ONBOARDING_ROUTE
    ) {
        composable(NOTIFICATIONS_ONBOARDING_ROUTE) {
            NotificationsOnboardingRoute(
                notificationsOnboardingCompleted = notificationsOnboardingCompleted,
                modifier = modifier
            )
        }

        composable(NOTIFICATIONS_ONBOARDING_NO_SKIP_ROUTE) {
            NotificationsOnboardingNoSkipRoute(
                notificationsOnboardingCompleted = notificationsOnboardingCompleted,
                modifier = modifier
            )
        }
    }
}
