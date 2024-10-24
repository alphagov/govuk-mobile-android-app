package uk.govuk.app.topics.navigation

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import uk.govuk.app.design.ui.component.ChildPageHeader
import uk.govuk.app.topics.R
import uk.govuk.app.topics.ui.AllTopicsRoute
import uk.govuk.app.topics.ui.EditTopicsRoute
import uk.govuk.app.topics.ui.TopicRoute

const val TOPICS_GRAPH_ROUTE = "topics_graph_route"
const val TOPIC_ROUTE = "topic_route"
internal const val TOPIC_REF_ARG = "ref"
private const val TOPICS_EDIT_ROUTE = "topics_edit_route"
const val TOPICS_ALL_ROUTE = "topics_all_route"
const val TOPICS_ALL_STEP_BY_STEPS_ROUTE = "topics_all_step_by_steps_route"

fun NavGraphBuilder.topicsGraph(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    navigation(
        route = TOPICS_GRAPH_ROUTE,
        startDestination = TOPIC_ROUTE
    ) {
        composable(
            "$TOPIC_ROUTE/{$TOPIC_REF_ARG}",
            arguments = listOf(navArgument(TOPIC_REF_ARG) { type = NavType.StringType })
        ) {
            val context = LocalContext.current

            TopicRoute(
                onBack = { navController.popBackStack() },
                onExternalLink = { url ->
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse(url)
                    context.startActivity(intent)
                },
                onStepByStepSeeAll = { navController.navigate(TOPICS_ALL_STEP_BY_STEPS_ROUTE) },
                onSubtopic = { ref -> navController.navigateToTopic(ref) },
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
        composable(TOPICS_ALL_STEP_BY_STEPS_ROUTE) {
            Column {
                ChildPageHeader(
                    text = stringResource(R.string.stepByStepGuidesTitle),
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}

fun NavController.navigateToTopic(ref: String) {
    navigate("$TOPIC_ROUTE/$ref")
}

fun NavController.navigateToTopicsEdit() {
    navigate(TOPICS_EDIT_ROUTE)
}

fun NavController.navigateToTopicsAll() {
    navigate(TOPICS_ALL_ROUTE)
}