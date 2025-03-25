package uk.gov.govuk.topics.navigation

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import androidx.navigation.navigation
import uk.gov.govuk.topics.ui.AllStepByStepRoute
import uk.gov.govuk.topics.ui.AllTopicsRoute
import uk.gov.govuk.topics.ui.EditTopicsRoute
import uk.gov.govuk.topics.ui.TopicRoute
import uk.gov.govuk.topics.ui.TopicSelectionRoute

const val TOPICS_GRAPH_ROUTE = "topics_graph_route"
const val TOPIC_SELECTION_ROUTE = "topic_selection_route"
const val TOPIC_ROUTE = "topic_route"
internal const val TOPIC_REF_ARG = "ref"
internal const val TOPIC_SUBTOPIC_ARG = "isSubtopic"
private const val TOPICS_EDIT_ROUTE = "topics_edit_route"
const val TOPICS_ALL_ROUTE = "topics_all_route"
const val TOPICS_ALL_STEP_BY_STEPS_ROUTE = "topics_all_step_by_steps_route"

fun NavGraphBuilder.topicsGraph(
    navController: NavController,
    topicSelectionCompleted: () -> Unit,
    modifier: Modifier = Modifier
) {
    navigation(
        route = TOPICS_GRAPH_ROUTE,
        startDestination = TOPICS_ALL_ROUTE
    ) {
        composable(TOPIC_SELECTION_ROUTE) {
            TopicSelectionRoute(
                onDone = topicSelectionCompleted,
                onSkip = topicSelectionCompleted
            )
        }
        val topicPath = "/{$TOPIC_REF_ARG}?$TOPIC_SUBTOPIC_ARG={$TOPIC_SUBTOPIC_ARG}"
        composable(
            "$TOPIC_ROUTE$topicPath",
            arguments = listOf(
                navArgument(TOPIC_REF_ARG) { type = NavType.StringType },
                navArgument(TOPIC_SUBTOPIC_ARG) { type = NavType.BoolType },
            ), deepLinks = listOf(
                navDeepLink {
                    uriPattern = "govuk://app/topic$topicPath"
                }
            )
        ) {
            val context = LocalContext.current

            TopicRoute(
                onBack = { navController.popBackStack() },
                onExternalLink = { url, _ -> launchExternalLink(context, url) },
                onStepByStepSeeAll = { navController.navigate(TOPICS_ALL_STEP_BY_STEPS_ROUTE) },
                onSubtopic = { ref -> navController.navigateToTopic(ref, true) },
                modifier = modifier
            )
        }
        composable(
            TOPICS_EDIT_ROUTE, deepLinks = listOf(
                navDeepLink {
                    uriPattern = "govuk://app/topics/edit"
                }
            )) {
            EditTopicsRoute(
                onBack = { navController.popBackStack() }
            )
        }
        composable(
            TOPICS_ALL_ROUTE, deepLinks = listOf(
                navDeepLink {
                    uriPattern = "govuk://app/topics/all"
                }
            )) {
            AllTopicsRoute(
                onBack = { navController.popBackStack() },
                onClick = { title -> navController.navigateToTopic(title) },
                modifier = modifier
            )
        }
        composable(
            TOPICS_ALL_STEP_BY_STEPS_ROUTE
        ) {
            val context = LocalContext.current

            AllStepByStepRoute(
                onBack = { navController.popBackStack()},
                onClick = { url -> launchExternalLink(context, url) },
                modifier = modifier
            )
        }
    }
}

private fun launchExternalLink(context: Context, url: String) {
    val intent = Intent(Intent.ACTION_VIEW)
    intent.data = Uri.parse(url)
    context.startActivity(intent)
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
