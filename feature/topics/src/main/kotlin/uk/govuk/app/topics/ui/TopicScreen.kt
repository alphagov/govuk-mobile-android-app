package uk.govuk.app.topics.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import uk.govuk.app.design.ui.component.BodyRegularLabel
import uk.govuk.app.design.ui.component.ChildPageHeader
import uk.govuk.app.design.ui.component.ExternalLinkListItem
import uk.govuk.app.design.ui.component.InternalLinkListItem
import uk.govuk.app.design.ui.component.LargeTitleBoldLabel
import uk.govuk.app.design.ui.component.LargeVerticalSpacer
import uk.govuk.app.design.ui.component.ListHeader
import uk.govuk.app.design.ui.component.MediumVerticalSpacer
import uk.govuk.app.design.ui.component.error.OfflineMessage
import uk.govuk.app.design.ui.component.error.ProblemMessage
import uk.govuk.app.design.ui.theme.GovUkTheme
import uk.govuk.app.topics.R
import uk.govuk.app.topics.TopicUiState
import uk.govuk.app.topics.TopicViewModel
import uk.govuk.app.topics.extension.toTopicName
import uk.govuk.app.topics.ui.model.TopicUi

@Composable
internal fun TopicRoute(
    onBack: () -> Unit,
    onExternalLink: (url: String, onExternalLink: Int) -> Unit,
    onStepByStepSeeAll: () -> Unit,
    onSubtopic: (ref: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: TopicViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()

    Box(modifier.fillMaxSize()) {
        uiState?.let {
            when (it) {
                is TopicUiState.Default -> {
                    TopicScreen(
                        topic = it.topicUi,
                        onPageView = { title -> viewModel.onPageView(
                            topicUi = it.topicUi,
                            title = title
                        ) },
                        onBack = onBack,
                        onExternalLink = { section, text, url, selectedItemIndex ->
                            viewModel.onContentClick(
                                title = it.topicUi.title,
                                section = section,
                                text = text,
                                url = url,
                                selectedItemIndex = selectedItemIndex
                            )
                            onExternalLink(url, selectedItemIndex)
                        },
                        onStepByStepSeeAll = { section, text, selectedItemIndex ->
                            onStepByStepSeeAll()
                            viewModel.onSeeAllClick(
                                section = section,
                                text = text,
                                selectedItemIndex = selectedItemIndex
                            )
                        },
                        onSubtopic = { text, ref, selectedItemIndex ->
                            viewModel.onSubtopicClick(text, selectedItemIndex)
                            onSubtopic(ref)
                        }
                    )
                }

                is TopicUiState.Offline -> ErrorScreen(
                    topicReference = it.topicReference,
                    onPageView = { title -> viewModel.onPageView(title = title) },
                    onBack = onBack,
                    content = { OfflineMessage({ viewModel.getTopic() }) }
                )


                is TopicUiState.ServiceError -> ErrorScreen(
                    topicReference = it.topicReference,
                    onPageView = { title -> viewModel.onPageView(title = title) },
                    onBack = onBack,
                    content = { ProblemMessage() }
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
    onExternalLink: (section: String, text: String, url: String, selectedItemIndex: Int) -> Unit,
    onStepByStepSeeAll: (section: String, text: String, selectedItemIndex: Int) -> Unit,
    onSubtopic: (text: String, ref: String, selectedItemIndex: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier.fillMaxWidth()) {
        LaunchedEffect(Unit) {
            onPageView(topic.title)
        }

        ChildPageHeader(
            onBack = onBack
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

        var currentItemIndex = 1

        LazyColumn {
            item {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .background(GovUkTheme.colourScheme.surfaces.homeHeader)
                        .padding(horizontal = GovUkTheme.spacing.medium)
                ) {
                    LargeTitleBoldLabel(
                        text = topic.title,
                        modifier = Modifier.semantics { heading() },
                        color = GovUkTheme.colourScheme.textAndIcons.header
                    )
                    MediumVerticalSpacer()
                    topic.description?.let { description ->
                        BodyRegularLabel(
                            text = description,
                            color = GovUkTheme.colourScheme.textAndIcons.header
                        )
                        MediumVerticalSpacer()
                    }
                }
            }

            item {
                MediumVerticalSpacer()
            }

            contentItems(
                currentItemIndex = currentItemIndex,
                contentItems = topic.popularPages,
                section = popularPagesSection,
                onClick = onExternalLink
            )

            if (topic.popularPages.isNotEmpty()) currentItemIndex += topic.popularPages.size

            contentItems(
                currentItemIndex = currentItemIndex,
                contentItems = topic.stepBySteps,
                section = stepByStepSection,
                onClick = onExternalLink,
                displaySeeAll = topic.displayStepByStepSeeAll,
                onSeeAll = onStepByStepSeeAll
            )

            if (topic.stepBySteps.isNotEmpty()) currentItemIndex += topic.stepBySteps.size
            if (topic.displayStepByStepSeeAll) { currentItemIndex += 1 }

            contentItems(
                currentItemIndex = currentItemIndex,
                contentItems = topic.services,
                section = servicesSection,
                onClick = onExternalLink
            )

            if (topic.services.isNotEmpty()) currentItemIndex += topic.services.size

            subtopics(
                currentItemIndex = currentItemIndex,
                subtopics = topic.subtopics,
                section = topic.subtopicsSection,
                onClick = onSubtopic
            )
        }
    }
}

private fun LazyListScope.contentItems(
    currentItemIndex: Int,
    contentItems: List<TopicUi.TopicContent>,
    section: TopicUi.Section,
    onClick: (section: String, text: String, url: String, selectedItemIndex: Int) -> Unit,
    displaySeeAll: Boolean = false,
    onSeeAll: (section: String, text: String, selectedItemIndex: Int) -> Unit = { _, _, _ -> }
) {
    if (contentItems.isNotEmpty()) {
        item {
            ListHeader(
                title = section.title,
                icon = section.icon,
                modifier = Modifier.padding(horizontal = GovUkTheme.spacing.medium)
            )
        }

        var lastIndex = contentItems.lastIndex
        if (displaySeeAll) lastIndex += 1

        itemsIndexed(contentItems) { index, content ->
            val sectionTitle = stringResource(section.title)
            ExternalLinkListItem(
                title = content.title,
                onClick = { onClick(sectionTitle, content.title, content.url, currentItemIndex + index) },
                modifier = Modifier.padding(horizontal = GovUkTheme.spacing.medium),
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
                    onClick = { onSeeAll(sectionTitle, title, currentItemIndex + contentItems.size) },
                    modifier = Modifier.padding(horizontal = GovUkTheme.spacing.medium),
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
    currentItemIndex: Int,
    subtopics: List<TopicUi.Subtopic>,
    section: TopicUi.Section,
    onClick: (text: String, ref: String, selectedItemIndex: Int) -> Unit,
) {
    if (subtopics.isNotEmpty()) {
        item {
            ListHeader(
                title = section.title,
                icon = section.icon,
                modifier = Modifier.padding(horizontal = GovUkTheme.spacing.medium)
            )
        }

        itemsIndexed(subtopics) { index, subtopic ->
            InternalLinkListItem(
                title = subtopic.title,
                onClick = { onClick(subtopic.title, subtopic.ref, currentItemIndex + index) },
                modifier = Modifier.padding(horizontal = GovUkTheme.spacing.medium),
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
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val topicName = topicReference.toTopicName(LocalContext.current)

    Column(modifier.verticalScroll(rememberScrollState())) {
        LaunchedEffect(Unit) {
            onPageView(topicName)
        }

        ChildPageHeader(
            text = topicName,
            onBack = onBack
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
            content = { OfflineMessage({}) }
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
