package uk.govuk.app.topics.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import uk.govuk.app.design.ui.component.BodyRegularLabel
import uk.govuk.app.design.ui.component.ChildPageHeader
import uk.govuk.app.design.ui.component.ExternalLinkListItem
import uk.govuk.app.design.ui.component.InternalLinkListItem
import uk.govuk.app.design.ui.component.LargeVerticalSpacer
import uk.govuk.app.design.ui.component.ListHeadingLabel
import uk.govuk.app.design.ui.component.MediumVerticalSpacer
import uk.govuk.app.design.ui.component.SmallVerticalSpacer
import uk.govuk.app.design.ui.theme.GovUkTheme
import uk.govuk.app.topics.R
import uk.govuk.app.topics.TopicViewModel
import uk.govuk.app.topics.ui.model.TopicUi

@Composable
internal fun TopicRoute(
    onBack: () -> Unit,
    onExternalLink: (url: String) -> Unit,
    onStepByStepSeeAll: () -> Unit,
    onSubtopic: (ref: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: TopicViewModel = hiltViewModel()
    val topic by viewModel.topic.collectAsState()

    TopicScreen(
        topic = topic,
        onPageView = { title -> viewModel.onPageView(title) },
        onBack = onBack,
        onExternalLink = { section, text, url ->
            viewModel.onContentClick(
                section = section,
                text = text,
                url = url
            )
            onExternalLink(url)
        },
        onStepByStepSeeAll = { section, text ->
            onStepByStepSeeAll()
            viewModel.onSeeAllClick(
                section = section,
                text = text
            )
        },
        onSubtopic = { text, ref ->
            viewModel.onSubtopicClick(text)
            onSubtopic(ref)
        },
        modifier = modifier
    )
}

@Composable
private fun TopicScreen(
    topic: TopicUi?,
    onPageView: (String) -> Unit,
    onBack: () -> Unit,
    onExternalLink: (section: String, text: String, url: String) -> Unit,
    onStepByStepSeeAll: (section: String, text: String) -> Unit,
    onSubtopic: (text: String, ref: String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier.fillMaxSize()) {
        if (topic != null) {
            LaunchedEffect(Unit) {
                onPageView(topic.title)
            }

            ChildPageHeader(
                text = topic.title,
                onBack = onBack
            )

            LazyColumn(Modifier.padding(horizontal = GovUkTheme.spacing.medium)) {
                if (topic.description != null) {
                    item {
                        BodyRegularLabel(topic.description)
                    }

                    item {
                        MediumVerticalSpacer()
                    }
                }

                popularPages(
                    popularPages = topic.popularPages,
                    onClick = onExternalLink
                )

                stepBySteps(
                    stepBySteps = topic.stepBySteps,
                    displaySeeAll = topic.displayStepByStepSeeAll,
                    onClick = onExternalLink,
                    onSeeAll = onStepByStepSeeAll
                )

                subtopics(
                    subtopics = topic.subtopics,
                    onClick = onSubtopic
                )
            }
        }
    }
}

private fun LazyListScope.popularPages(
    popularPages: List<TopicUi.TopicContent>,
    onClick: (section: String, text:String, url:String) -> Unit,
) {
    lateinit var section: String

    if (popularPages.isNotEmpty()) {
        item {
            section = stringResource(R.string.popularPagesTitle)
            ListHeadingLabel(section)
        }

        item {
            SmallVerticalSpacer()
        }

        itemsIndexed(popularPages) { index, content ->
            ExternalLinkListItem(
                title = content.title,
                onClick = { onClick(section, content.title, content.url) },
                isFirst = index == 0,
                isLast = index == popularPages.lastIndex
            )
        }

        item {
            LargeVerticalSpacer()
        }
    }
}

private fun LazyListScope.stepBySteps(
    stepBySteps: List<TopicUi.TopicContent>,
    displaySeeAll: Boolean,
    onClick: (section: String, text:String, url:String) -> Unit,
    onSeeAll: (section: String, text:String) -> Unit
) {
    if (stepBySteps.isNotEmpty()) {
        lateinit var section: String

        item {
            section = stringResource(R.string.stepByStepGuidesTitle)
            ListHeadingLabel(section)
        }

        item {
            SmallVerticalSpacer()
        }

        var lastIndex = stepBySteps.lastIndex
        if (displaySeeAll) lastIndex += 1

        itemsIndexed(stepBySteps) { index, content ->
            ExternalLinkListItem(
                title = content.title,
                onClick = { onClick(section, content.title, content.url) },
                isFirst = index == 0,
                isLast = index == lastIndex
            )
        }

        if (displaySeeAll) {
            item {
                val title = stringResource(R.string.seeAllButton)
                InternalLinkListItem(
                    title = title,
                    onClick = { onSeeAll(section, title) },
                    isFirst = lastIndex == 0,
                    isLast = true
                )
            }
        }

        item {
            LargeVerticalSpacer()
        }
    }
}

private fun LazyListScope.subtopics(
    subtopics: List<TopicUi.Subtopic>,
    onClick: (text:String, ref: String) -> Unit,
) {
    if (subtopics.isNotEmpty()) {

        item {
            ListHeadingLabel(stringResource(R.string.browseTitle))
        }

        item {
            SmallVerticalSpacer()
        }

        itemsIndexed(subtopics) { index, subtopic ->
            InternalLinkListItem(
                title = subtopic.title,
                onClick = { onClick(subtopic.title, subtopic.ref) },
                isFirst = index == 0,
                isLast = index == subtopics.lastIndex
            )
        }

        item {
            LargeVerticalSpacer()
        }
    }
}
