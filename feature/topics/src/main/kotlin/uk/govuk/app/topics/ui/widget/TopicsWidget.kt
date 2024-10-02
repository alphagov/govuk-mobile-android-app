package uk.govuk.app.topics.ui.widget

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.window.core.layout.WindowWidthSizeClass
import uk.govuk.app.design.R
import uk.govuk.app.design.ui.component.MediumVerticalSpacer
import uk.govuk.app.design.ui.component.SmallHorizontalSpacer
import uk.govuk.app.design.ui.component.SmallVerticalSpacer
import uk.govuk.app.design.ui.component.Title3BoldLabel
import uk.govuk.app.design.ui.component.TopicCard
import uk.govuk.app.design.ui.theme.GovUkTheme
import uk.govuk.app.topics.TopicUi
import uk.govuk.app.topics.TopicsViewModel

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
    topics: List<TopicUi>,
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val windowWidthSizeClass = currentWindowAdaptiveInfo().windowSizeClass.windowWidthSizeClass

    // Todo - ideally this would be a lazy grid to gain from performance optimizations, however
    //  nested lazy components are not allowed without a non-trivial workaround. The performance
    //  impact should be negligible with the amount of items currently being displayed but we may
    //  have to re-visit this in the future.
    Column(modifier) {
        // Todo - handle empty topics
        if (topics.isNotEmpty()){
            val topicsCount = topics.size
            val columnCount = if (windowWidthSizeClass == WindowWidthSizeClass.COMPACT) 2 else 4
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
                            val topic = topics[topicIndex]
                            TopicCard(
                                icon = topic.icon,
                                title = topic.title,
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
                TopicUi(
                    "",
                    R.drawable.ic_topic_default,
                    "A really really really really really really long topic title"
                ),
                TopicUi(
                    "",
                    R.drawable.ic_topic_benefits,
                    "Benefits"
                ),
                TopicUi(
                    "",
                    R.drawable.ic_topic_transport,
                    "Driving"
                ),
                TopicUi(
                    "",
                    R.drawable.ic_topic_money,
                    "Tax"
                ),
                TopicUi(
                    "",
                    R.drawable.ic_topic_parenting,
                    "Child Benefit"
                ),
            ),
            onClick = { }
        )
    }
}