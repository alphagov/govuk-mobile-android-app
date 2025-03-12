package uk.gov.govuk.topics.ui.widget

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import uk.gov.govuk.design.ui.component.BodyBoldLabel
import uk.gov.govuk.design.ui.component.BodyRegularLabel
import uk.gov.govuk.design.ui.component.CompactButton
import uk.gov.govuk.design.ui.component.Title3BoldLabel
import uk.gov.govuk.design.ui.component.error.ProblemMessage
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.gov.govuk.topics.R
import uk.gov.govuk.topics.TopicsWidgetUiState
import uk.gov.govuk.topics.TopicsWidgetViewModel
import uk.gov.govuk.topics.ui.component.TopicVerticalCard
import uk.gov.govuk.topics.ui.component.TopicsGrid
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
            onAllClick = onAllClick,
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
    onAllClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(Unit) {
        onPageView(uiState.topics)
    }

    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val title = if (uiState.isCustomised) {
                stringResource(R.string.customisedTopicsWidgetTitle)
            } else {
                stringResource(R.string.topicsWidgetTitle)
            }
            Title3BoldLabel(
                text = title,
                modifier = Modifier.semantics { heading() }
            )

            Spacer(Modifier.weight(1f))

            if (!uiState.isError) {
                val editButtonText = stringResource(R.string.editButton)
                val editButtonAltText = stringResource(R.string.editButtonAltText)

                TextButton(
                    onClick = { onEditClick(editButtonText) }
                ) {
                    BodyBoldLabel(
                        text = editButtonText,
                        color = GovUkTheme.colourScheme.textAndIcons.link,
                        modifier = Modifier.semantics {
                            contentDescription = editButtonAltText
                        }
                    )
                }
            }
        }

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
            val seeAllButtonText = stringResource(R.string.allTopicsButton)
            CompactButton(
                text = seeAllButtonText,
                onClick = { onAllClick(seeAllButtonText) },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
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
            onEditClick = { },
            onAllClick = { }
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
            onEditClick = { },
            onAllClick = { }
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
            onEditClick = { },
            onAllClick = { }
        )
    }
}
