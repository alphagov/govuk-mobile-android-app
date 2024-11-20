package uk.govuk.app.topics.ui

import androidx.annotation.StringRes
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
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
import uk.govuk.app.networking.ui.component.OfflineMessage
import uk.govuk.app.networking.ui.component.ServiceNotRespondingMessage
import uk.govuk.app.topics.R
import uk.govuk.app.topics.TopicUiState
import uk.govuk.app.topics.TopicViewModel
import uk.govuk.app.topics.extension.toTopicName
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
    val uiState by viewModel.uiState.collectAsState()

    uiState?.let {
        when (it) {
            is TopicUiState.Default -> {
                TopicScreen(
                    topic = it.topicUi,
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

            is TopicUiState.Offline -> OfflineScreen(
                topicReference = it.topicReference,
                onPageView = { title -> viewModel.onPageView(title) },
                onBack = onBack,
                onTryAgainClick = { viewModel.getTopic() }
            )

            is TopicUiState.ServiceError -> ServiceNotRespondingMessage()
        }
    }
}

@Composable
private fun TopicScreen(
    topic: TopicUi,
    onPageView: (String) -> Unit,
    onBack: () -> Unit,
    onExternalLink: (section: String, text: String, url: String) -> Unit,
    onStepByStepSeeAll: (section: String, text: String) -> Unit,
    onSubtopic: (text: String, ref: String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier.fillMaxSize()) {
        LaunchedEffect(Unit) {
            onPageView(topic.title)
        }

        ChildPageHeader(
            text = topic.title,
            onBack = onBack
        )

        val popularPagesTitle = stringResource(R.string.popularPagesTitle)
        val stepByStepsTitle = stringResource(R.string.stepByStepGuidesTitle)
        val servicesTitle = stringResource(R.string.servicesTitle)

        LazyColumn(Modifier.padding(horizontal = GovUkTheme.spacing.medium)) {
            item {
                Column {
                    MediumVerticalSpacer()
                    topic.description?.let { description ->
                        BodyRegularLabel(description)
                        MediumVerticalSpacer()
                    }
                }
            }

            contentItems(
                contentItems = topic.popularPages,
                section = popularPagesTitle,
                onClick = onExternalLink
            )

            contentItems(
                contentItems = topic.stepBySteps,
                section = stepByStepsTitle,
                onClick = onExternalLink,
                displaySeeAll = topic.displayStepByStepSeeAll,
                onSeeAll = onStepByStepSeeAll
            )

            contentItems(
                contentItems = topic.services,
                section = servicesTitle,
                onClick = onExternalLink
            )

            subtopics(
                subtopics = topic.subtopics,
                title = topic.subtopicsTitle,
                onClick = onSubtopic
            )
        }
    }
}

private fun LazyListScope.contentItems(
    contentItems: List<TopicUi.TopicContent>,
    section: String,
    onClick: (section: String, text: String, url: String) -> Unit,
    displaySeeAll: Boolean = false,
    onSeeAll: (section: String, text: String) -> Unit = { _, _ -> }
) {
    if (contentItems.isNotEmpty()) {
        item {
            ListHeadingLabel(section)
        }

        item {
            SmallVerticalSpacer()
        }

        var lastIndex = contentItems.lastIndex
        if (displaySeeAll) lastIndex += 1

        itemsIndexed(contentItems) { index, content ->
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
    @StringRes title: Int,
    onClick: (text: String, ref: String) -> Unit,
) {
    if (subtopics.isNotEmpty()) {

        item {
            ListHeadingLabel(stringResource(title))
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

@Composable
private fun OfflineScreen(
    topicReference: String,
    onPageView: (String) -> Unit,
    onBack: () -> Unit,
    onTryAgainClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val topicName = topicReference.toTopicName(LocalContext.current)

    Column(modifier.fillMaxSize()) {
        LaunchedEffect(Unit) {
            onPageView(topicName)
        }

        ChildPageHeader(
            text = topicName,
            onBack = onBack
        )
        OfflineMessage(onTryAgainClick = onTryAgainClick)
    }
}

@Preview
@Composable
private fun OfflineScreenPreview() {
    GovUkTheme {
        OfflineScreen(
            "benefits", {}, {}, {}
        )
    }
}
