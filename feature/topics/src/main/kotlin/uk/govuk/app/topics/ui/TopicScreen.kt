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
import uk.govuk.app.design.ui.component.ExternalLinkListItem
import uk.govuk.app.design.ui.component.ListHeadingLabel
import uk.govuk.app.design.ui.component.MediumVerticalSpacer
import uk.govuk.app.design.ui.component.SmallVerticalSpacer
import uk.govuk.app.design.ui.theme.GovUkTheme
import uk.govuk.app.topics.TopicViewModel
import uk.govuk.app.topics.ui.model.TopicUi

@Composable
internal fun TopicRoute(
    onBack: () -> Unit,
    onExternalLink: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: TopicViewModel = hiltViewModel()
    val topic by viewModel.topic.collectAsState()

    TopicScreen(
        topic = topic,
        onBack = onBack,
        onExternalLink = onExternalLink,
        modifier = modifier
    )
}

@Composable
private fun TopicScreen(
    topic: TopicUi?,
    onBack: () -> Unit,
    onExternalLink: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier.fillMaxSize()) {
        if (topic != null) {
            ChildPageHeader(
                text = topic.title,
                onBack = onBack
            )

            LazyColumn(Modifier.padding(horizontal = GovUkTheme.spacing.medium)) {
                popularPages(
                    popularPages = topic.popularPages,
                    onClick = onExternalLink
                )

                item {
                    MediumVerticalSpacer()
                }

                stepBySteps(
                    stepBySteps = topic.stepBySteps,
                    displayStepByStepSeeAll = topic.displayStepByStepSeeAll,
                    onClick = onExternalLink
                )

                item {
                    MediumVerticalSpacer()
                }

                subtopics(topic.subtopics)
            }
        }
    }
}

private fun LazyListScope.popularPages(
    popularPages: List<TopicUi.TopicContent>,
    onClick: (String) -> Unit
) {
    if (popularPages.isNotEmpty()) {
        item {
            ListHeadingLabel("Popular pages in this topic") // Todo - extract string
        }

        item {
            SmallVerticalSpacer()
        }

        itemsIndexed(popularPages) { index, content ->
            ExternalLinkListItem(
                title = content.title,
                onClick = { onClick(content.url) },
                index = index,
                lastIndex = popularPages.lastIndex
            )
        }
    }
}

private fun LazyListScope.stepBySteps(
    stepBySteps: List<TopicUi.TopicContent>,
    displayStepByStepSeeAll: Boolean,
    onClick: (String) -> Unit
) {
    if (stepBySteps.isNotEmpty()) {
        item {
            ListHeadingLabel("Step by step guides") // Todo - extract string
        }

        item {
            SmallVerticalSpacer()
        }

        itemsIndexed(stepBySteps) { index, content ->
            ExternalLinkListItem(
                title = content.title,
                onClick = { onClick(content.url) },
                index = index,
                lastIndex = stepBySteps.lastIndex
            )
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
