package uk.govuk.app.topics.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import uk.govuk.app.design.ui.component.BodyRegularLabel
import uk.govuk.app.design.ui.component.CardListItem
import uk.govuk.app.design.ui.component.ChildPageHeader
import uk.govuk.app.design.ui.component.ListHeadingLabel
import uk.govuk.app.design.ui.component.MediumVerticalSpacer
import uk.govuk.app.design.ui.component.SmallVerticalSpacer
import uk.govuk.app.design.ui.theme.GovUkTheme
import uk.govuk.app.topics.TopicViewModel
import uk.govuk.app.topics.ui.model.TopicUi

@Composable
internal fun TopicRoute(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: TopicViewModel = hiltViewModel()
    val topic by viewModel.topic.collectAsState()

    TopicScreen(
        topic = topic,
        onBack = onBack,
        modifier = modifier
    )
}

@Composable
private fun TopicScreen(
    topic: TopicUi?,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier.fillMaxSize()) {
        if (topic != null) {
            ChildPageHeader(
                text = topic.title,
                onBack = onBack
            )

            LazyColumn(Modifier.padding(horizontal = GovUkTheme.spacing.medium)) {
                popularPages(topic.popularPages)

                item {
                    MediumVerticalSpacer()
                }

                stepBySteps(
                    stepBySteps = topic.stepBySteps,
                    displayStepByStepSeeAll = topic.displayStepByStepSeeAll
                )

                item {
                    MediumVerticalSpacer()
                }

                subtopics(topic.subtopics)
            }
        }
    }
}

private fun LazyListScope.popularPages(popularPages: List<TopicUi.TopicContent>) {
    if (popularPages.isNotEmpty()) {
        item {
            ListHeadingLabel("Popular pages in this topic") // Todo - extract string
        }

        item {
            SmallVerticalSpacer()
        }

        itemsIndexed(popularPages) { index, content ->
            CardListItem(index, popularPages.lastIndex) {
                // Todo - extract into design module
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BodyRegularLabel(
                        text = content.title,
                    )
                }
            }
        }
    }
}

private fun LazyListScope.stepBySteps(
    stepBySteps: List<TopicUi.TopicContent>,
    displayStepByStepSeeAll: Boolean
) {
    if (stepBySteps.isNotEmpty()) {
        item {
            ListHeadingLabel("Step by step guides") // Todo - extract string
        }

        item {
            SmallVerticalSpacer()
        }

        itemsIndexed(stepBySteps) { index, content ->
            CardListItem(index, stepBySteps.lastIndex) {
                // Todo - extract into design module
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BodyRegularLabel(
                        text = content.title,
                    )
                }
            }
        }

        // Todo - see all button
    }
}

private fun LazyListScope.subtopics(subtopics: List<TopicUi.Subtopic>) {
    if (subtopics.isNotEmpty()) {
        item {
            ListHeadingLabel("Browse") // Todo - extract string
        }

        item {
            SmallVerticalSpacer()
        }

        itemsIndexed(subtopics) { index, content ->
            CardListItem(index, subtopics.lastIndex) {
                // Todo - extract into design module
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BodyRegularLabel(
                        text = content.title,
                    )
                }
            }
        }
    }
}
