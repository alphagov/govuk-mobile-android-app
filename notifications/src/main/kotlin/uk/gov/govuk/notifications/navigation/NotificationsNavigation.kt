package uk.gov.govuk.notifications.navigation

import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import uk.gov.govuk.notifications.ui.NotificationsConsentRoute
import uk.gov.govuk.notifications.ui.NotificationsOnboardingRoute
import uk.gov.govuk.notifications.ui.NotificationsPermissionRoute

const val NOTIFICATIONS_ONBOARDING_GRAPH_ROUTE = "notifications_onboarding_graph_route"
const val NOTIFICATIONS_GRAPH_ROUTE = "notifications_graph_route"
const val NOTIFICATIONS_ONBOARDING_ROUTE = "notifications_onboarding_route"
const val NOTIFICATIONS_PERMISSION_ROUTE = "notifications_permission_route"
const val NOTIFICATIONS_CONSENT_ROUTE = "notifications_consent_route"

fun NavGraphBuilder.notificationsOnboardingGraph(
    notificationsOnboardingCompleted: () -> Unit,
    modifier: Modifier = Modifier
) {
    navigation(
        route = NOTIFICATIONS_ONBOARDING_GRAPH_ROUTE,
        startDestination = NOTIFICATIONS_ONBOARDING_ROUTE
    ) {
        composable(NOTIFICATIONS_ONBOARDING_ROUTE) {
            NotificationsOnboardingRoute(
                notificationsOnboardingCompleted = notificationsOnboardingCompleted,
                modifier = modifier
            )
        }
    }
}

fun NavGraphBuilder.notificationsGraph(
    notificationsCompleted: () -> Unit,
    modifier: Modifier = Modifier
) {
    navigation(
        route = NOTIFICATIONS_GRAPH_ROUTE,
        startDestination = NOTIFICATIONS_PERMISSION_ROUTE
    ) {
        composable(NOTIFICATIONS_PERMISSION_ROUTE) {
            NotificationsPermissionRoute(
                notificationsCompleted = notificationsCompleted,
                modifier = modifier
            )
        }
        composable(NOTIFICATIONS_CONSENT_ROUTE) {
            NotificationsConsentRoute(
                notificationsCompleted = notificationsCompleted,
                modifier = modifier
            )
        }
    }
}
