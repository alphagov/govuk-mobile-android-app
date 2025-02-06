package uk.govuk.app.notifications.navigation

import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import uk.govuk.app.notifications.ui.NotificationsPermissionRoute

const val NOTIFICATIONS_GRAPH_ROUTE = "notifications_graph_route"
private const val NOTIFICATIONS_PERMISSION_ROUTE = "notifications_permission_route"

fun NavGraphBuilder.notificationsGraph(
    notificationsPermissionCompleted: () -> Unit,
    modifier: Modifier = Modifier
) {
    navigation(
        route = NOTIFICATIONS_GRAPH_ROUTE,
        startDestination = NOTIFICATIONS_PERMISSION_ROUTE
    ) {
        composable(NOTIFICATIONS_PERMISSION_ROUTE) {
            NotificationsPermissionRoute(
                notificationsPermissionCompleted = notificationsPermissionCompleted,
                modifier = modifier
            )
        }
    }
}
