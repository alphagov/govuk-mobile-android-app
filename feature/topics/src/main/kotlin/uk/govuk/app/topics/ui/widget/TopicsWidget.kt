package uk.govuk.app.topics.ui.widget

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import uk.govuk.app.design.ui.component.BodyBoldLabel
import uk.govuk.app.design.ui.component.CompactButton
import uk.govuk.app.design.ui.component.Title3BoldLabel
import uk.govuk.app.design.ui.theme.GovUkTheme
import uk.govuk.app.topics.R
import uk.govuk.app.topics.TopicsWidgetViewModel
import uk.govuk.app.topics.ui.component.TopicVerticalCard
import uk.govuk.app.topics.ui.component.TopicsGrid
import uk.govuk.app.topics.ui.model.TopicItemUi

@Composable
fun TopicsWidget(
    onTopicClick: (String, String) -> Unit,
    onEditClick: (String) -> Unit,
    onAllClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: TopicsWidgetViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()

    // Todo - handle empty topics
    uiState?.let {
        TopicsWidgetContent(
            topics = it.topics,
            isCustomised = it.isCustomised,
            displayShowAll = it.displayShowAll,
            onTopicClick = onTopicClick,
            onEditClick = onEditClick,
            onAllClick = onAllClick,
            modifier = modifier
        )
    }
}

@Composable
private fun TopicsWidgetContent(
    topics: List<TopicItemUi>,
    isCustomised: Boolean,
    displayShowAll: Boolean,
    onTopicClick: (String, String) -> Unit,
    onEditClick: (String) -> Unit,
    onAllClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val title = if (isCustomised) {
                stringResource(R.string.customisedTopicsWidgetTitle)
            } else {
                stringResource(R.string.topicsWidgetTitle)
            }
            Title3BoldLabel(
                text = title,
                modifier = Modifier.semantics { heading() }
            )

            Spacer(Modifier.weight(1f))

            val editButtonText = stringResource(R.string.editButton)

            TextButton(
                onClick = { onEditClick(editButtonText) }
            ) {
                BodyBoldLabel(
                    text = editButtonText,
                    color = GovUkTheme.colourScheme.textAndIcons.link
                )
            }
        }

        TopicsGrid(
            topics = topics,
        ) { modifier, topic ->
            TopicVerticalCard(
                icon = topic.icon,
                title = topic.title,
                onClick = { onTopicClick(topic.ref, topic.title) },
                modifier = modifier
            )
        }

        if (displayShowAll) {
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
            isCustomised = true,
            displayShowAll = true,
            onTopicClick = { _, _ -> },
            onEditClick = { },
            onAllClick = { }
        )
    }
}