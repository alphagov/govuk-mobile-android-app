package uk.govuk.app.topics.ui.widget

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import uk.govuk.app.design.ui.component.MediumVerticalSpacer
import uk.govuk.app.design.ui.component.SmallHorizontalSpacer
import uk.govuk.app.design.ui.component.SmallVerticalSpacer
import uk.govuk.app.design.ui.component.Title3BoldLabel
import uk.govuk.app.design.ui.component.TopicCard
import uk.govuk.app.design.ui.theme.GovUkTheme
import uk.govuk.app.topics.TopicsViewModel
import uk.govuk.app.topics.data.remote.model.TopicItem

@Composable
fun TopicsWidget(
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: TopicsViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()

    Column(modifier = modifier) {
        Title3BoldLabel("Topics")

        SmallVerticalSpacer()

        uiState?.topics?.let{ topics ->
            TopicsGrid(
                topics = topics,
                onClick = onClick
            )
        }
    }
}

@Composable
private fun TopicsGrid(
    topics: List<TopicItem>,
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // Todo - ideally this would be a lazy column to gain from performance optimizations, however
    //  nested lazy columns are not allowed without a non-trivial workaround (some widgets will
    //  themselves contain a lazy column/grid). The performance impact should be negligible with
    //  the amount of items currently displayed on the home screen but we may have to re-visit
    //  this in the future.
    Column(modifier) {
        // Todo - handle empty topics???
        if (topics.isNotEmpty()){
            val topicsCount = topics.size
            val columnCount = 2
            var rowCount = topicsCount / columnCount
            if (topicsCount.mod(columnCount) > 0) {
                rowCount += 1
            }

            for (rowIndex in 0 until rowCount) {
                Row(
                    Modifier.height(intrinsicSize = IntrinsicSize.Max)
                ) {
                    for (columnIndex in 0 until columnCount) {
                        if (columnIndex > 0) {
                            SmallHorizontalSpacer()
                        }

                        val topicIndex = (rowIndex * columnCount) + columnIndex
                        if (topicIndex < topicsCount) {
                            TopicCard(
                                title = topics[topicIndex].title,
                                onClick = onClick,
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .weight(1f)
                            )
                        } else {
                            Box(Modifier.weight(1f)) {  }
                        }

                        if (columnIndex < columnCount - 1) {
                            SmallHorizontalSpacer()
                        }
                    }
                }
                MediumVerticalSpacer()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TopicsGridPreview() {
    GovUkTheme {
        TopicsGrid(
            topics = listOf(
                TopicItem(
                    "",
                    "A really really really really really really long topic title"
                ),
                TopicItem(
                    "",
                    "Benefits"
                ),
                TopicItem(
                    "",
                    "Driving"
                ),
                TopicItem(
                    "",
                    "Tax"
                ),
                TopicItem(
                    "",
                    "Child Benefit"
                ),
            ),
            onClick = { }
        )
    }
}