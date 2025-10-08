package uk.gov.govuk.topics.ui.widget

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import uk.gov.govuk.design.ui.component.CardListItem
import uk.gov.govuk.design.ui.component.ConnectedButtonGroup
import uk.gov.govuk.design.ui.component.IconLinkListItem
import uk.gov.govuk.design.ui.component.SectionHeadingLabel
import uk.gov.govuk.design.ui.model.SectionHeadingLabelButton
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.gov.govuk.topics.R
import uk.gov.govuk.topics.TopicsWidgetUiState
import uk.gov.govuk.topics.TopicsWidgetViewModel
import uk.gov.govuk.topics.ui.model.TopicItemUi

@Composable
fun TopicsWidget(
    onTopicClick: (String, String) -> Unit,
    onEditClick: (String) -> Unit,
    onAllClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: TopicsWidgetViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()

    uiState?.let {
        TopicsWidgetContent(
            uiState = it,
            onPageView = { topics -> viewModel.onPageView(topics) },
            onTopicClick = { ref, title, index ->
                viewModel.onTopicSelectClick(ref, title, index)
                onTopicClick(ref, title)
            },
            onEditClick = onEditClick,
            modifier = modifier
        )
    }
}

@Composable
private fun TopicsWidgetContent(
    uiState: TopicsWidgetUiState,
    onPageView: (List<TopicItemUi>) -> Unit,
    onTopicClick: (String, String, Int) -> Unit,
    onEditClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(Unit) {
        onPageView(uiState.topics)
    }

    Column(modifier = modifier) {
        val editButtonText = stringResource(R.string.editButton)
        val editButton = if (uiState.isError) {
            null
        } else {
            SectionHeadingLabelButton(
                title = editButtonText,
                altText = stringResource(R.string.editButtonAltText),
                onClick = { onEditClick(editButtonText) }
            )
        }

        SectionHeadingLabel(
            title3 = stringResource(R.string.topicsWidgetTitle),
            button = editButton
        )

        Column {
            CardListItem(
                isLast = false,
                drawDivider = false
            ) {
                Box(
                    Modifier
                        .padding(top = GovUkTheme.spacing.medium)
                        .padding(horizontal = GovUkTheme.spacing.medium)
                ) {
                    ConnectedButtonGroup(
                        firstText = "Your topics",
                        onFirst = { },
                        firstActive = true,
                        secondText = "All topics",
                        onSecond = { },
                        secondActive = false
                    )
                }
            }

            // Todo - handle error/empty topics!!!
            uiState.topics.forEachIndexed { index, topic ->
                IconLinkListItem(
                    title = topic.title,
                    icon = topic.icon,
                    onClick = {
                        onTopicClick(
                            topic.ref,
                            topic.title,
                            uiState.topics.indexOf(topic) + 1
                        )
                    },
                    isFirst = false,
                    isLast = index == uiState.topics.lastIndex
                )
            }

            /*
            ConnectedButtonGroup(
                firstText = "Your topics",
                onFirst = { },
                firstActive = true,
                secondText = "All topics",
                onSecond = { },
                secondActive = false
            )
             */
        }

        /*
        when {
            uiState.isError -> {
                ProblemMessage(
                    description = stringResource(R.string.topics_error_message)
                )
            }
            uiState.topics.isEmpty() -> {
                BodyRegularLabel(
                    text = stringResource(R.string.empty_topics),
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(vertical = GovUkTheme.spacing.small)
                )
            }
            else -> {
                TopicsGrid(
                    topics = uiState.topics,
                ) { modifier, topic ->
                    TopicVerticalCard(
                        icon = topic.icon,
                        title = topic.title,
                        onClick = {
                            onTopicClick(
                                topic.ref,
                                topic.title,
                                uiState.topics.indexOf(topic) + 1
                             )
                        },
                        modifier = modifier
                    )
                }
            }
        }

        if (uiState.displayShowAll) {
            LargeVerticalSpacer()
            val seeAllButtonText = stringResource(R.string.allTopicsButton)
            CompactButton(
                text = seeAllButtonText,
                onClick = { onAllClick(seeAllButtonText) },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
         */
    }
}

@Preview(showBackground = true)
@Composable
private fun TopicsWidgetPreview() {
    GovUkTheme {
        TopicsWidgetContent(
            uiState = TopicsWidgetUiState(
                topics = listOf(
                    TopicItemUi(
                        "",
                        R.drawable.ic_topic_default,
                        "A really really really really really really long topic title",
                        "",
                        isSelected = true
                    ),
                    TopicItemUi(
                        "",
                        R.drawable.ic_topic_benefits,
                        "Benefits",
                        "",
                        isSelected = true
                    ),
                    TopicItemUi(
                        "",
                        R.drawable.ic_topic_transport,
                        "Driving",
                        "",
                        isSelected = true
                    ),
                    TopicItemUi(
                        "",
                        R.drawable.ic_topic_money,
                        "Tax",
                        "",
                        isSelected = true
                    ),
                    TopicItemUi(
                        "",
                        R.drawable.ic_topic_parenting,
                        "Child Benefit",
                        "",
                        isSelected = true
                    ),
                ),
                isError = false,
                isCustomised = true,
                displayShowAll = true
            ),
            onPageView = { },
            onTopicClick = { _, _, _ -> },
            onEditClick = { }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TopicsWidgetEmptyTopicsPreview() {
    GovUkTheme {
        TopicsWidgetContent(
            uiState = TopicsWidgetUiState(
                topics = emptyList(),
                isError = false,
                isCustomised = true,
                displayShowAll = true
            ),
            onPageView = { },
            onTopicClick = { _, _, _ -> },
            onEditClick = { }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TopicsWidgetErrorPreview() {
    GovUkTheme {
        TopicsWidgetContent(
            uiState = TopicsWidgetUiState(
                topics = emptyList(),
                isError = true,
                isCustomised = false,
                displayShowAll = false
            ),
            onPageView = { },
            onTopicClick = { _, _, _ -> },
            onEditClick = { }
        )
    }
}
