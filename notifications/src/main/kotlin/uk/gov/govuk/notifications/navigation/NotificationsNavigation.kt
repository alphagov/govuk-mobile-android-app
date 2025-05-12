package uk.gov.govuk.notifications.navigation

import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import uk.gov.govuk.notifications.ui.NotificationsOnboardingFromSettingsRoute
import uk.gov.govuk.notifications.ui.NotificationsOnboardingRoute

const val NOTIFICATIONS_GRAPH_ROUTE = "notifications_graph_route"
private const val NOTIFICATIONS_ONBOARDING_ROUTE = "notifications_onboarding_route"
const val NOTIFICATIONS_ONBOARDING_FROM_SETTINGS_ROUTE = "notifications_onboarding_from_settings_route"

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

        composable(NOTIFICATIONS_ONBOARDING_FROM_SETTINGS_ROUTE) {
            NotificationsOnboardingFromSettingsRoute(
                notificationsOnboardingCompleted = notificationsOnboardingCompleted,
                modifier = modifier
            )
        }
    }
}
