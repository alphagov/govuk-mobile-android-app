package uk.govuk.app.topics.navigation

import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import uk.govuk.app.topics.ui.AllTopicsRoute
import uk.govuk.app.topics.ui.EditTopicsRoute
import uk.govuk.app.topics.ui.TopicRoute

const val TOPICS_GRAPH_ROUTE = "topics_graph_route"
private const val TOPIC_ROUTE = "topic_route"
private const val TOPIC_TITLE_ARG = "title"
private const val TOPICS_EDIT_ROUTE = "topics_edit_route"
const val TOPICS_ALL_ROUTE = "topics_all_route"

fun NavGraphBuilder.topicsGraph(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    navigation(
        route = TOPICS_GRAPH_ROUTE,
        startDestination = TOPIC_ROUTE
    ) {
        composable(
            "$TOPIC_ROUTE/{$TOPIC_TITLE_ARG}"
        ) { backStackEntry ->
            val title = backStackEntry.arguments?.getString(TOPIC_TITLE_ARG)
            TopicRoute(
                title = title ?: "",
                modifier = modifier
            )
        }
        composable(TOPICS_EDIT_ROUTE) {
            EditTopicsRoute(
                onBack = { navController.popBackStack() }
            )
        }
        composable(TOPICS_ALL_ROUTE) {
            AllTopicsRoute(
                onBack = { navController.popBackStack() },
                modifier = modifier
            )
        }
    }
}

fun NavController.navigateToTopic(title: String) {
    navigate("$TOPIC_ROUTE/$title")
}

fun NavController.navigateToTopicsEdit() {
    navigate(TOPICS_EDIT_ROUTE)
}

fun NavController.navigateToTopicsAll() {
    navigate(TOPICS_ALL_ROUTE)
}