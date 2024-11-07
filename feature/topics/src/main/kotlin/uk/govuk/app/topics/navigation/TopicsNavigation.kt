package uk.govuk.app.topics.navigation

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
import androidx.navigation.navigation
import uk.govuk.app.topics.ui.AllStepByStepRoute
import uk.govuk.app.topics.ui.AllTopicsRoute
import uk.govuk.app.topics.ui.EditTopicsRoute
import uk.govuk.app.topics.ui.TopicRoute
import uk.govuk.app.topics.ui.TopicSelectionRoute

const val TOPICS_GRAPH_ROUTE = "topics_graph_route"
private const val TOPIC_SELECTION_ROUTE = "topic_selection_route"
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
        startDestination = TOPIC_SELECTION_ROUTE
    ) {
        composable(TOPIC_SELECTION_ROUTE) {
            TopicSelectionRoute(
                onBack = {
                    navController.popBackStack()
                },
                onDone = topicSelectionCompleted,
                onSkip = topicSelectionCompleted
            )
        }

        composable(
            "$TOPIC_ROUTE/{$TOPIC_REF_ARG}?$TOPIC_SUBTOPIC_ARG={$TOPIC_SUBTOPIC_ARG}",
            arguments = listOf(
                navArgument(TOPIC_REF_ARG) { type = NavType.StringType },
                navArgument(TOPIC_SUBTOPIC_ARG) { type = NavType.BoolType },
            )
        ) { backStackEntry ->
            val context = LocalContext.current

            TopicRoute(
                onBack = { navController.popBackStack() },
                onExternalLink = { url -> launchExternalLink(context, url) },
                onStepByStepSeeAll = {
                    backStackEntry.arguments?.getString(TOPIC_REF_ARG)?.let { ref ->
                        navController.navigateToAllStepBySteps(ref)
                    }
                },
                onSubtopic = { ref -> navController.navigateToTopic(ref, true) },
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
                onClick = { title -> navController.navigateToTopic(title) },
                modifier = modifier
            )
        }
        composable(
            "$TOPICS_ALL_STEP_BY_STEPS_ROUTE/{$TOPIC_REF_ARG}",
            arguments = listOf(
                navArgument(TOPIC_REF_ARG) { type = NavType.StringType }
            )
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

private fun NavController.navigateToAllStepBySteps(ref: String) {
    navigate("$TOPICS_ALL_STEP_BY_STEPS_ROUTE/$ref")
}

fun NavController.navigateToTopicsEdit() {
    navigate(TOPICS_EDIT_ROUTE)
}

fun NavController.navigateToTopicsAll() {
    navigate(TOPICS_ALL_ROUTE)
}