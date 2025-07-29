package uk.gov.govuk.chat.navigation

import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import uk.gov.govuk.chat.ui.AboutRoute
import uk.gov.govuk.chat.ui.ChatRoute

const val CHAT_GRAPH_ROUTE = "chat_graph_route"
private const val CHAT_ROUTE = "chat_route"
private const val CHAT_ABOUT_ROUTE = "chat_about_route"

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
                navController = navController,
                modifier = modifier
            )
        }
        composable(CHAT_ABOUT_ROUTE) {
            AboutRoute(
                navController = navController,
                modifier = modifier
            )
        }
    }
}

fun NavController.navigateToAbout() {
    navigate(CHAT_ABOUT_ROUTE)
}

fun NavController.navigateToChat() {
    navigate(CHAT_ROUTE)
}
