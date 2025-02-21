package uk.govuk.app.notifications.navigation

import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import uk.govuk.app.notifications.ui.NotificationsOnboardingRoute

const val NOTIFICATIONS_GRAPH_ROUTE = "notifications_graph_route"
private const val NOTIFICATIONS_ONBOARDING_ROUTE = "notifications_onboarding_route"

fun NavGraphBuilder.notificationsGraph(
    canSkip: Boolean,
    notificationsOnboardingCompleted: () -> Unit,
    modifier: Modifier = Modifier
) {
    navigation(
        route = NOTIFICATIONS_GRAPH_ROUTE,
        startDestination = NOTIFICATIONS_ONBOARDING_ROUTE
    ) {
        composable(NOTIFICATIONS_ONBOARDING_ROUTE) {
            NotificationsOnboardingRoute(
                canSkip = canSkip,
                notificationsOnboardingCompleted = notificationsOnboardingCompleted,
                modifier = modifier
            )
        }
    }
}
