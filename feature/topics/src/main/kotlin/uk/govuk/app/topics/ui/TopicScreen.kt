package uk.govuk.app.topics.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import uk.govuk.app.design.ui.component.ChildPageHeader
import uk.govuk.app.design.ui.component.ExternalLinkListItem
import uk.govuk.app.design.ui.component.ExtraLargeVerticalSpacer
import uk.govuk.app.design.ui.component.InternalLinkListItem
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
    onStepByStepSeeAll: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: TopicViewModel = hiltViewModel()
    val topic by viewModel.topic.collectAsState()

    TopicScreen(
        topic = topic,
        onBack = onBack,
        onExternalLink = onExternalLink,
        onStepByStepSeeAll = onStepByStepSeeAll,
        modifier = modifier
    )
}

@Composable
private fun TopicScreen(
    topic: TopicUi?,
    onBack: () -> Unit,
    onExternalLink: (String) -> Unit,
    onStepByStepSeeAll: () -> Unit,
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
                    displaySeeAll = topic.displayStepByStepSeeAll,
                    onClick = onExternalLink,
                    onSeeAll = onStepByStepSeeAll
                )

                item {
                    MediumVerticalSpacer()
                }

                subtopics(topic.subtopics)

                item {
                    ExtraLargeVerticalSpacer()
                }
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
    displaySeeAll: Boolean,
    onClick: (String) -> Unit,
    onSeeAll: () -> Unit
) {
    if (stepBySteps.isNotEmpty()) {
        item {
            ListHeadingLabel("Step by step guides") // Todo - extract string
        }

        item {
            SmallVerticalSpacer()
        }

        var lastIndex = stepBySteps.lastIndex
        if (displaySeeAll) lastIndex += 1

        itemsIndexed(stepBySteps) { index, content ->
            ExternalLinkListItem(
                title = content.title,
                onClick = { onClick(content.url) },
                index = index,
                lastIndex = lastIndex
            )
        }

        if (displaySeeAll) {
            item {
                InternalLinkListItem(
                    title = "See all", // Todo - extract string
                    onClick = onSeeAll,
                    index = lastIndex,
                    lastIndex = lastIndex
                )
            }
        }
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

        itemsIndexed(subtopics) { index, subtopic ->
            InternalLinkListItem(
                title = subtopic.title,
                onClick = { }, // Todo - handle click
                index = index,
                lastIndex = subtopics.lastIndex
            )
        }
    }
}
