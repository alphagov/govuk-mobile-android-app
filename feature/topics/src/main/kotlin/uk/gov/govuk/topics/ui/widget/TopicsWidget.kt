package uk.gov.govuk.topics.ui.widget

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedCard
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import uk.gov.govuk.design.ui.component.CardListItem
import uk.gov.govuk.design.ui.component.ConnectedButton
import uk.gov.govuk.design.ui.component.ConnectedButtonGroup
import uk.gov.govuk.design.ui.component.IconLinkListItem
import uk.gov.govuk.design.ui.component.SectionHeadingLabel
import uk.gov.govuk.design.ui.component.error.ProblemMessage
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

    Column(modifier = modifier) {
        Header(
            displayEdit = uiState.allTopics.isNotEmpty(),
            onEditClick = onEditClick
        )
        if (uiState.allTopics.isNotEmpty()) {
            TopicsCard(
                uiState = uiState,
                onEditClick = onEditClick,
                onTopicClick = onTopicClick
            )
        } else {
            ErrorCard()
        }
    }
}

@Composable
private fun Header(
    displayEdit: Boolean,
    onEditClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val editButtonText = stringResource(R.string.edit_button)
    SectionHeadingLabel(
        modifier = modifier,
        title3 = stringResource(R.string.topics_widget_title),
        button = if (displayEdit) {
            SectionHeadingLabelButton(
                title = editButtonText,
                altText = stringResource(R.string.edit_button_alt_text),
                onClick = { onEditClick(editButtonText) }
            )
        } else null
    )
}

@Composable
private fun TopicsCard(
    uiState: TopicsWidgetUiState,
    onEditClick: (String) -> Unit,
    onTopicClick: (String, String, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var activeButtonState by rememberSaveable { mutableStateOf( ConnectedButton.FIRST) }

    LaunchedEffect(uiState.yourTopics.isEmpty()) {
        if (uiState.yourTopics.isEmpty()) {
            activeButtonState = ConnectedButton.SECOND
        }
    }
    val coroutineScope = rememberCoroutineScope()

    Column(modifier) {
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
                    firstText = stringResource(R.string.your_topics),
                    secondText = stringResource(R.string.all_topics),
                    onActiveStateChange = { activeButton ->
                        coroutineScope.launch {
                            if (activeButton == ConnectedButton.FIRST &&
                                uiState.yourTopics.isEmpty()
                            ) {
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
        TopicsList(
            uiState = uiState,
            activeButtonState = activeButtonState,
            onClick = onTopicClick
        )
    }
}

@Suppress("UnusedReceiverParameter")
@Composable
private fun ColumnScope.TopicsList(
    uiState: TopicsWidgetUiState,
    activeButtonState: ConnectedButton,
    onClick: (String, String, Int) -> Unit
) {
    val topics = when (activeButtonState) {
        ConnectedButton.FIRST -> {
            uiState.yourTopics.ifEmpty {
                uiState.allTopics
            }
        }
        ConnectedButton.SECOND -> uiState.allTopics
    }

    topics.forEachIndexed { index, topic ->
        IconLinkListItem(
            title = topic.title,
            icon = topic.icon,
            onClick = {
                // Todo - do we need to identify your topics vs all topics for analytics???
                onClick(
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

@Composable
private fun ErrorCard(modifier: Modifier = Modifier) {
    OutlinedCard(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = GovUkTheme.colourScheme.surfaces.list),
        border = BorderStroke(width = 0.dp, color = Color.Unspecified)
    ) {
        ProblemMessage(description = stringResource(R.string.topics_error_message))
    }
}

@Preview(showBackground = true)
@Composable
private fun TopicsWidgetPreview() {
    val topics = listOf(
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
        )
    )

    GovUkTheme {
        TopicsWidgetContent(
            uiState = TopicsWidgetUiState(
                yourTopics = topics,
                allTopics = topics
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
                yourTopics = emptyList()
            ),
            onPageView = { },
            onTopicClick = { _, _, _ -> },
            onEditClick = { }
        )
    }
}