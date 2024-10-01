package uk.govuk.app.topics.navigation

import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import uk.govuk.app.topics.ui.TopicRoute

const val TOPICS_GRAPH_ROUTE = "topics_graph_route"
private const val TOPIC_ROUTE = "topic_route"
private const val TOPIC_TITLE_ARG = "title"

fun NavGraphBuilder.topicsGraph(
    modifier: Modifier = Modifier
) {
    navigation(
        route = TOPICS_GRAPH_ROUTE,
        startDestination = TOPIC_ROUTE
    ) {
        composable(
            "$TOPIC_ROUTE/{$TOPIC_TITLE_ARG}",
            /* Todo - deepLinks = listOf(
                navDeepLink {
                    uriPattern = "/search"
                    action = Intent.ACTION_VIEW
                }
            )*/
        ) { backStackEntry ->
            val title = backStackEntry.arguments?.getString(TOPIC_TITLE_ARG)
            TopicRoute(
                title = title ?: "",
                modifier = modifier
            )
        }
    }
}

fun NavController.navigateToTopic(title: String) {
    navigate("$TOPIC_ROUTE/$title")
}