package uk.gov.govuk.topics.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.delay
import uk.gov.govuk.design.ui.component.BodyRegularLabel
import uk.gov.govuk.design.ui.component.ChildPageHeader
import uk.gov.govuk.design.ui.component.ExternalLinkListItemLegacy
import uk.gov.govuk.design.ui.component.FocusableCard
import uk.gov.govuk.design.ui.component.InternalLinkListItemLegacy
import uk.gov.govuk.design.ui.component.LargeTitleBoldLabel
import uk.gov.govuk.design.ui.component.LargeVerticalSpacer
import uk.gov.govuk.design.ui.component.ListHeaderLegacy
import uk.gov.govuk.design.ui.component.MediumVerticalSpacer
import uk.gov.govuk.design.ui.component.SectionHeadingLabel
import uk.gov.govuk.design.ui.component.SmallHorizontalSpacer
import uk.gov.govuk.design.ui.component.error.OfflineMessage
import uk.gov.govuk.design.ui.component.error.ProblemMessage
import uk.gov.govuk.design.ui.model.CardListItem
import uk.gov.govuk.design.ui.model.SectionHeadingLabelButton
import uk.gov.govuk.design.ui.model.HeaderDismissStyle
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.gov.govuk.topics.R
import uk.gov.govuk.topics.TopicUiState
import uk.gov.govuk.topics.TopicViewModel
import uk.gov.govuk.topics.extension.toTopicName
import uk.gov.govuk.topics.ui.model.TopicUi

@Composable
internal fun TopicRoute(
    onBack: () -> Unit,
    onExternalLink: (url: String, onExternalLink: Int) -> Unit,
    onStepByStepSeeAll: () -> Unit,
    onPopularPagesSeeAll: () -> Unit,
    onSubtopic: (ref: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: TopicViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()
    val focusRequester = remember { FocusRequester() }

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
                        onPopularPagesSeeAll = { section, text, selectedItemIndex ->
                            onPopularPagesSeeAll()
                            viewModel.onSeeAllClick(
                                section = section,
                                text = text,
                                selectedItemIndex = selectedItemIndex
                            )
                        },
                        onSubtopic = { text, ref, selectedItemIndex ->
                            viewModel.onSubtopicClick(text, selectedItemIndex)
                            onSubtopic(ref)
                        },
                        focusRequester = focusRequester
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
    onPopularPagesSeeAll: (section: String, text: String, selectedItemIndex: Int) -> Unit,
    onSubtopic: (text: String, ref: String, selectedItemIndex: Int) -> Unit,
    focusRequester: FocusRequester,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
    val lazyListState = rememberLazyListState()
    val fontScale = LocalDensity.current.fontScale
    val isFontScaledUp = fontScale > 1.0f

    Column(modifier.fillMaxWidth()) {

        ChildPageHeader(
            dismissStyle = HeaderDismissStyle.Back(onBack)
        )

        val stepByStepSection = TopicUi.Section(
            title = R.string.step_by_step_guides_title,
            icon = R.drawable.ic_topic_step_by_step
        )
        val servicesSection = TopicUi.Section(
            title = R.string.services_title,
            icon = R.drawable.ic_topic_services_and_info
        )

        var currentItemIndex = 1

        LazyColumn(state = lazyListState) {
            item {
                Column(
                    Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(GovUkTheme.colourScheme.surfaces.homeHeader)
                    ) {
                        LargeTitleBoldLabel(
                            text = topic.title,
                            modifier = Modifier
                                .padding(horizontal = GovUkTheme.spacing.medium)
                                .semantics { heading() }
                                .focusRequester(focusRequester)
                                .focusable(),
                            color = GovUkTheme.colourScheme.textAndIcons.header
                        )
                    }
                    topic.description?.let { description ->
                        MediumVerticalSpacer()
                        BodyRegularLabel(
                            text = description,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = GovUkTheme.spacing.medium)
                        )
                    }
                }
            }

            item {
                MediumVerticalSpacer()
            }

            item {
                val section = stringResource(R.string.popular_pages_title)
                val seeAllButton = stringResource(R.string.see_all_button)
                val cards = topic.popularPages.map {
                    CardListItem(
                        title = it.title,
                        onClick = {
                            onExternalLink(
                                section, it.title, it.url, currentItemIndex
                            )
                        }
                    )
                }

                SectionHeadingLabel(
                    modifier = modifier.padding(horizontal = GovUkTheme.spacing.medium),
                    title3 = stringResource(R.string.popular_pages_title),
                    button = if (!isFontScaledUp) {
                        SectionHeadingLabelButton(
                            title = seeAllButton,
                            altText = seeAllButton,
                            onClick = {
                                onPopularPagesSeeAll(section, seeAllButton, currentItemIndex)
                            }
                        )
                    } else null
                )

                val colors = mapOf(
                    "focusedBackgroundColor" to GovUkTheme.colourScheme.surfaces.cardCarouselFocused,
                    "unfocusedBackgroundColor" to GovUkTheme.colourScheme.surfaces.cardCarousel,
                    "focusedContentColor" to GovUkTheme.colourScheme.textAndIcons.cardCarouselFocused,
                    "unfocusedContentColor" to GovUkTheme.colourScheme.textAndIcons.cardCarousel
                )

                if (isFontScaledUp) {
                    // vertical list
                    Column(
                        modifier = modifier.fillMaxWidth()
                            .padding(horizontal = GovUkTheme.spacing.medium)
                    ) {
                        cards.forEach { item ->
                            FocusableCard(
                                item,
                                colors,
                                modifier = Modifier.padding(bottom = GovUkTheme.spacing.medium)
                            )
                        }
                    }
                } else {
                    // horizontal list
                    Box {
                        LazyRow(
                            state = lazyListState,
                            modifier = modifier.fillMaxWidth()
                                .padding(horizontal = GovUkTheme.spacing.medium),
                        ) {
                            itemsIndexed(cards) { index, item ->
                                FocusableCard(
                                    item,
                                    colors,
                                    modifier = Modifier.size(150.dp)
                                )

                                if (index < cards.size - 1) {
                                    SmallHorizontalSpacer()
                                }
                            }
                        }
                    }
                }

                MediumVerticalSpacer()
            }

            if (topic.popularPages.isNotEmpty()) currentItemIndex += topic.popularPages.size
            if (topic.displayPopularPagesSeeAll) { currentItemIndex += 1 }

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
    LaunchedEffect(Unit) {
        onPageView(topic.title)
        focusManager.clearFocus(true)
        delay(500)
        if (lazyListState.firstVisibleItemIndex == 0) {
            focusRequester.requestFocus()
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
            ListHeaderLegacy(
                title = section.title,
                icon = section.icon,
                modifier = Modifier.padding(horizontal = GovUkTheme.spacing.medium)
            )
        }

        var lastIndex = contentItems.lastIndex
        if (displaySeeAll) lastIndex += 1

        itemsIndexed(contentItems) { index, content ->
            val sectionTitle = stringResource(section.title)
            ExternalLinkListItemLegacy(
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
                val title = stringResource(R.string.see_all_button)
                InternalLinkListItemLegacy(
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
            ListHeaderLegacy(
                title = section.title,
                icon = section.icon,
                modifier = Modifier.padding(horizontal = GovUkTheme.spacing.medium)
            )
        }

        itemsIndexed(subtopics) { index, subtopic ->
            InternalLinkListItemLegacy(
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
            dismissStyle = HeaderDismissStyle.Back(onBack)
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
