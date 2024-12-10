package uk.govuk.app.topics.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import uk.govuk.app.design.ui.component.ListHeader
import uk.govuk.app.design.ui.component.MediumVerticalSpacer
import uk.govuk.app.design.ui.theme.GovUkTheme
import uk.govuk.app.networking.ui.component.OfflineMessage
import uk.govuk.app.networking.ui.component.ProblemMessage
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

    Box(Modifier.fillMaxSize()) {
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

                is TopicUiState.Offline -> ErrorScreen(
                    topicReference = it.topicReference,
                    onPageView = { title -> viewModel.onPageView(title) },
                    onBack = onBack,
                    content = { OfflineMessage(modifier = modifier) { viewModel.getTopic() } }
                )


                is TopicUiState.ServiceError -> ErrorScreen(
                    topicReference = it.topicReference,
                    onPageView = { title -> viewModel.onPageView(title) },
                    onBack = onBack,
                    content = { ProblemMessage(modifier = modifier) }
                )
            }
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
    Column(modifier.fillMaxWidth()) {
        LaunchedEffect(Unit) {
            onPageView(topic.title)
        }

        ChildPageHeader(
            text = topic.title,
            onBack = onBack,
            onAction = null
        )

        val popularPagesSection = TopicUi.Section(
            title = R.string.popularPagesTitle,
            icon = R.drawable.ic_topic_popular
        )
        val stepByStepSection = TopicUi.Section(
            title = R.string.stepByStepGuidesTitle,
            icon = R.drawable.ic_topic_step_by_step
        )
        val servicesSection = TopicUi.Section(
            title = R.string.servicesTitle,
            icon = R.drawable.ic_topic_services_and_info
        )

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
                section = popularPagesSection,
                onClick = onExternalLink
            )

            contentItems(
                contentItems = topic.stepBySteps,
                section = stepByStepSection,
                onClick = onExternalLink,
                displaySeeAll = topic.displayStepByStepSeeAll,
                onSeeAll = onStepByStepSeeAll
            )

            contentItems(
                contentItems = topic.services,
                section = servicesSection,
                onClick = onExternalLink
            )

            subtopics(
                subtopics = topic.subtopics,
                section = topic.subtopicsSection,
                onClick = onSubtopic
            )
        }
    }
}

private fun LazyListScope.contentItems(
    contentItems: List<TopicUi.TopicContent>,
    section: TopicUi.Section,
    onClick: (section: String, text: String, url: String) -> Unit,
    displaySeeAll: Boolean = false,
    onSeeAll: (section: String, text: String) -> Unit = { _, _ -> }
) {
    if (contentItems.isNotEmpty()) {
        item {
            ListHeader(
                title = section.title,
                icon = section.icon
            )
        }

        var lastIndex = contentItems.lastIndex
        if (displaySeeAll) lastIndex += 1

        itemsIndexed(contentItems) { index, content ->
            val sectionTitle = stringResource(section.title)
            ExternalLinkListItem(
                title = content.title,
                onClick = { onClick(sectionTitle, content.title, content.url) },
                isFirst = false,
                isLast = index == lastIndex
            )
        }

        if (displaySeeAll) {
            item {
                val sectionTitle = stringResource(section.title)
                val title = stringResource(R.string.seeAllButton)
                InternalLinkListItem(
                    title = title,
                    onClick = { onSeeAll(sectionTitle, title) },
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
    section: TopicUi.Section,
    onClick: (text: String, ref: String) -> Unit,
) {
    if (subtopics.isNotEmpty()) {
        item {
            ListHeader(
                title = section.title,
                icon = section.icon
            )
        }

        itemsIndexed(subtopics) { index, subtopic ->
            InternalLinkListItem(
                title = subtopic.title,
                onClick = { onClick(subtopic.title, subtopic.ref) },
                isFirst = false,
                isLast = index == subtopics.lastIndex
            )
        }

        item {
            LargeVerticalSpacer()
        }
    }
}

@Composable
private fun ErrorScreen(
    topicReference: String,
    onPageView: (String) -> Unit,
    onBack: () -> Unit,
    content: @Composable () -> Unit
) {
    val topicName = topicReference.toTopicName(LocalContext.current)

    Column {
        LaunchedEffect(Unit) {
            onPageView(topicName)
        }

        ChildPageHeader(
            text = topicName,
            onBack = onBack,
            onAction = null
        )

        content()
    }
}

@Preview
@Composable
private fun ErrorScreenOfflinePreview() {
    GovUkTheme {
        ErrorScreen(
            topicReference = "benefits",
            onPageView = {},
            onBack = {},
            content = { OfflineMessage {} }
        )
    }
}

@Preview
@Composable
private fun ErrorScreenProblemPreview() {
    GovUkTheme {
        ErrorScreen(
            topicReference = "benefits",
            onPageView = {},
            onBack = {},
            content = { ProblemMessage() }
        )
    }
}
