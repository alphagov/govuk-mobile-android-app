package uk.gov.govuk.topics.navigation

import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavDeepLink
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import uk.gov.govuk.topics.ui.AllStepByStepRoute
import uk.gov.govuk.topics.ui.AllTopicsRoute
import uk.gov.govuk.topics.ui.EditTopicsRoute
import uk.gov.govuk.topics.ui.TopicRoute
import uk.gov.govuk.topics.ui.TopicSelectionRoute

const val TOPIC_SELECTION_GRAPH_ROUTE = "topic_selection_graph_route"
private const val TOPIC_SELECTION_ROUTE = "topic_selection_route"
const val TOPICS_GRAPH_ROUTE = "topics_graph_route"
const val TOPIC_ROUTE = "topic_route"
internal const val TOPIC_REF_ARG = "ref"
internal const val TOPIC_SUBTOPIC_ARG = "isSubtopic"
private const val TOPICS_EDIT_ROUTE = "topics_edit_route"
const val TOPICS_ALL_ROUTE = "topics_all_route"
const val TOPICS_ALL_STEP_BY_STEPS_ROUTE = "topics_all_step_by_steps_route"

fun NavGraphBuilder.topicSelectionGraph(
    topicSelectionCompleted: () -> Unit,
) {
    navigation(
        route = TOPIC_SELECTION_GRAPH_ROUTE,
        startDestination = TOPIC_SELECTION_ROUTE
    ) {
        composable(TOPIC_SELECTION_ROUTE) {
            TopicSelectionRoute(
                onDone = topicSelectionCompleted,
                onSkip = topicSelectionCompleted
            )
        }
    }
}

fun NavGraphBuilder.topicsGraph(
    navController: NavController,
    deepLinks: (path: String) -> List<NavDeepLink>,
    launchBrowser: (url: String) -> Unit,
    modifier: Modifier = Modifier
) {
    navigation(
        route = TOPICS_GRAPH_ROUTE,
        startDestination = TOPICS_ALL_ROUTE
    ) {
        val topicPath = "/{$TOPIC_REF_ARG}?$TOPIC_SUBTOPIC_ARG={$TOPIC_SUBTOPIC_ARG}"
        composable(
            "$TOPIC_ROUTE$topicPath",
            arguments = listOf(
                navArgument(TOPIC_REF_ARG) { type = NavType.StringType },
                navArgument(TOPIC_SUBTOPIC_ARG) { type = NavType.BoolType },
            ), deepLinks = deepLinks("/topics$topicPath")
        ) {
            TopicRoute(
                onBack = { navController.popBackStack() },
                onExternalLink = { url, _ ->
                    launchBrowser(url)
                },
                onStepByStepSeeAll = { navController.navigate(TOPICS_ALL_STEP_BY_STEPS_ROUTE) },
                onSubtopic = { ref -> navController.navigateToTopic(ref, true) },
                modifier = modifier
            )
        }
        composable(
            TOPICS_EDIT_ROUTE, deepLinks = deepLinks("/topics/edit")
        ) {
            EditTopicsRoute(
                onBack = { navController.popBackStack() }
            )
        }
        composable(
            TOPICS_ALL_ROUTE, deepLinks = deepLinks("/topics/all")
        ) {
            AllTopicsRoute(
                onBack = { navController.popBackStack() },
                onClick = { title -> navController.navigateToTopic(title) },
                modifier = modifier
            )
        }
        composable(
            TOPICS_ALL_STEP_BY_STEPS_ROUTE
        ) {
            AllStepByStepRoute(
                onBack = { navController.popBackStack()},
                onClick = { url ->
                    launchBrowser(url)
                 },
                modifier = modifier
            )
        }
    }
}

fun NavController.navigateToTopic(ref: String, isSubtopic: Boolean = false) {
    navigate("$TOPIC_ROUTE/$ref?$TOPIC_SUBTOPIC_ARG=$isSubtopic")
}

fun NavController.navigateToTopicsEdit() {
    navigate(TOPICS_EDIT_ROUTE)
}

fun NavController.navigateToTopicsAll() {
    navigate(TOPICS_ALL_ROUTE)
}
