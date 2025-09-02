package uk.gov.govuk.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import uk.gov.govuk.BuildConfig
import uk.gov.govuk.alertbanner.ui.AlertBanner
import uk.gov.govuk.config.data.remote.model.AlertBanner
import uk.gov.govuk.design.ui.component.LargeVerticalSpacer
import uk.gov.govuk.settings.ui.FeedbackPromptWidget
import uk.gov.govuk.topics.navigation.navigateToTopic
import uk.gov.govuk.topics.navigation.navigateToTopicsAll
import uk.gov.govuk.topics.navigation.navigateToTopicsEdit
import uk.gov.govuk.topics.ui.widget.TopicsWidget
import uk.gov.govuk.ui.model.HomeWidget
import uk.gov.govuk.visited.navigation.VISITED_GRAPH_ROUTE
import uk.gov.govuk.visited.ui.widget.VisitedWidget
import uk.govuk.app.local.navigation.LOCAL_GRAPH_ROUTE
import uk.govuk.app.local.navigation.LOCAL_LOOKUP_ROUTE
import uk.govuk.app.local.ui.LocalWidget

internal fun List<HomeWidget>?.contains(widget: HomeWidget) = this?.contains(widget) == true

internal fun homeWidgets(
    navController: NavHostController,
    homeWidgets: List<HomeWidget>?,
    alertBanner: AlertBanner?,
    onInternalClick: (String) -> Unit,
    onExternalClick: (String, String?) -> Unit,
    onSuppressClick: (id: String) -> Unit,
    launchBrowser: (url: String) -> Unit
): List<@Composable (Modifier) -> Unit> {
    val widgets = mutableListOf<@Composable (Modifier) -> Unit>()
    homeWidgets?.forEach {
        when (it) {
            HomeWidget.ALERT_BANNER -> {
                alertBanner?.let { alertBanner ->
                    widgets.add { modifier ->
                        AlertBanner(
                            alertBanner = alertBanner,
                            onClick = { text ->
                                onExternalClick(text, null)
                            },
                            launchBrowser = launchBrowser,
                            onSuppressClick = onSuppressClick,
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
                            onExternalClick(text, null)
                            launchBrowser(BuildConfig.VERSION_NAME_USER_FACING)
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
                            onInternalClick(text)
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
                            onInternalClick(title)
                            navController.navigateToTopic(ref)
                        },
                        onEditClick = { text ->
                            onInternalClick(text)
                            navController.navigateToTopicsEdit()
                        },
                        onAllClick = { text ->
                            onInternalClick(text)
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
                        onLookupClick = { text ->
                            onInternalClick(text)
                            navController.navigate(LOCAL_GRAPH_ROUTE)
                        },
                        onLocalAuthorityClick = { text, url ->
                            onExternalClick(text, url)
                        },
                        onEditClick = { text ->
                            onInternalClick(text)
                            navController.navigate(LOCAL_LOOKUP_ROUTE)
                        },
                        launchBrowser = launchBrowser,
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
