package uk.gov.govuk.topics.ui.widget

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import uk.gov.govuk.design.ui.component.CardListItem
import uk.gov.govuk.design.ui.component.ConnectedButton
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
    // Todo - do we need to send every time the user toggles between your topics and all topics???
    LaunchedEffect(Unit) {
        onPageView(uiState.yourTopics)
    }

    var activeButtonState by rememberSaveable { mutableStateOf( ConnectedButton.FIRST) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(uiState.yourTopics.isEmpty()) {
        if (uiState.yourTopics.isEmpty()) {
            activeButtonState = ConnectedButton.SECOND
        }
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
                        firstText = "Your topics", // Todo - extract string
                        secondText = "All topics", // Todo - extract string
                        onActiveStateChange = { activeButton ->
                            coroutineScope.launch {
                                if (activeButton == ConnectedButton.FIRST &&
                                    uiState.yourTopics.isEmpty()) {
                                    onEditClick("") // Todo - what should text be???
                                    delay(500)
                                }

                                activeButtonState = activeButton
                            }
                        },
                        activeButton = activeButtonState
                    )
                }
            }

            val topics = when (activeButtonState) {
                ConnectedButton.FIRST -> {
                    uiState.yourTopics.ifEmpty {
                        uiState.allTopics
                    }
                }
                ConnectedButton.SECOND -> uiState.allTopics
            }

            // Todo - handle error/empty topics!!!
            topics.forEachIndexed { index, topic ->
                IconLinkListItem(
                    title = topic.title,
                    icon = topic.icon,
                    onClick = {
                        // Todo - do we need to identify your topics vs all topics for analytics???
                        onTopicClick(
                            topic.ref,
                            topic.title,
                            index + 1
                        )
                    },
                    isFirst = false,
                    isLast = index == topics.lastIndex
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TopicsWidgetPreview() {
    GovUkTheme {
        TopicsWidgetContent(
            uiState = TopicsWidgetUiState(
                yourTopics = listOf(
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
                allTopics = emptyList(),
                isError = false
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
                allTopics = emptyList(),
                yourTopics = emptyList(),
                isError = false
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
                allTopics = emptyList(),
                yourTopics = emptyList(),
                isError = true
            ),
            onPageView = { },
            onTopicClick = { _, _, _ -> },
            onEditClick = { }
        )
    }
}
