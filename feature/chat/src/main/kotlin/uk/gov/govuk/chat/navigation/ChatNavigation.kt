package uk.gov.govuk.chat.navigation

import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import uk.gov.govuk.chat.ui.ChatRoute

const val CHAT_GRAPH_ROUTE = "chat_graph_route"
private const val CHAT_ROUTE = "chat_route"

fun NavGraphBuilder.chatGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    navigation(
        route = CHAT_GRAPH_ROUTE,
        startDestination = CHAT_ROUTE
    ) {
        composable(CHAT_ROUTE) {
            ChatRoute(
                onBack = { navController.popBackStack() },
                modifier = modifier
            )
        }
    }
}
