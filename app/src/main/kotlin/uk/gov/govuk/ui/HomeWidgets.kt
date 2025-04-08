package uk.gov.govuk.ui

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import uk.gov.govuk.BuildConfig
import uk.gov.govuk.design.ui.component.LargeVerticalSpacer
import uk.gov.govuk.home.HomeWidget
import uk.gov.govuk.notifications.ui.NotificationsPromptWidget
import uk.gov.govuk.notifications.ui.notificationsPermissionShouldShowRationale
import uk.gov.govuk.settings.navigation.navigateToHelpAndFeedback
import uk.gov.govuk.settings.ui.FeedbackPromptWidget
import uk.gov.govuk.topics.navigation.navigateToTopic
import uk.gov.govuk.topics.navigation.navigateToTopicsAll
import uk.gov.govuk.topics.navigation.navigateToTopicsEdit
import uk.gov.govuk.topics.ui.widget.TopicsWidget
import uk.gov.govuk.visited.navigation.VISITED_GRAPH_ROUTE
import uk.gov.govuk.visited.ui.widget.VisitedWidget
import uk.govuk.app.local.navigation.LOCAL_GRAPH_ROUTE
import uk.govuk.app.local.ui.LocalWidget

internal fun List<HomeWidget>?.contains(widget: HomeWidget) = this?.contains(widget) == true

internal fun homeWidgets(
    context: Context,
    navController: NavHostController,
    homeWidgets: List<HomeWidget>?,
    onClick: (String, Boolean) -> Unit,
    onSuppressClick: (String, HomeWidget) -> Unit
): List<@Composable (Modifier) -> Unit> {
    val widgets = mutableListOf<@Composable (Modifier) -> Unit>()
    homeWidgets?.forEach {
        when (it) {
            HomeWidget.NOTIFICATIONS -> {
                widgets.add { modifier ->
                    if (notificationsPermissionShouldShowRationale()) {
                        NotificationsPromptWidget(
                            onClick = { text ->
                                onClick(text, true)
                            },
                            onSuppressClick = { text ->
                                onSuppressClick(text, HomeWidget.NOTIFICATIONS)
                            },
                            modifier = modifier
                        )
                        LargeVerticalSpacer()
                    }
                }
            }

            HomeWidget.FEEDBACK_PROMPT -> {
                widgets.add { modifier ->
                    FeedbackPromptWidget(
                        onClick = { text ->
                            onClick(text, true)
                            navigateToHelpAndFeedback(context, BuildConfig.VERSION_NAME)
                        },
                        modifier = modifier
                    )
                    LargeVerticalSpacer()
                }
            }

            HomeWidget.RECENT_ACTIVITY -> {
                widgets.add { modifier ->
                    VisitedWidget(
                        onClick = { text ->
                            onClick(text, false)
                            navController.navigate(VISITED_GRAPH_ROUTE)
                        },
                        modifier = modifier
                    )
                    LargeVerticalSpacer()
                }
            }

            HomeWidget.TOPICS -> {
                widgets.add { modifier ->
                    TopicsWidget(
                        onTopicClick = { ref, title ->
                            onClick(title, false)
                            navController.navigateToTopic(ref)
                        },
                        onEditClick = { text ->
                            onClick(text, false)
                            navController.navigateToTopicsEdit()
                        },
                        onAllClick = { text ->
                            onClick(text, false)
                            navController.navigateToTopicsAll()
                        },
                        modifier = modifier
                    )
                    LargeVerticalSpacer()
                }
            }

            HomeWidget.LOCAL -> {
                widgets.add { modifier ->
                    LocalWidget(
                        onClick = {
                            onClick
                            navController.navigate(LOCAL_GRAPH_ROUTE)
                        },
                        modifier = modifier
                    )
                    LargeVerticalSpacer()
                }
            }

            else -> { /* Do nothing */ }
        }
    }
    return widgets
}