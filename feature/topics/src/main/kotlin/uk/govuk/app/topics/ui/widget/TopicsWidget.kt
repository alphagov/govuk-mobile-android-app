package uk.govuk.app.topics.ui.widget

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import uk.govuk.app.design.ui.component.MediumVerticalSpacer
import uk.govuk.app.design.ui.component.SmallHorizontalSpacer
import uk.govuk.app.design.ui.component.SmallVerticalSpacer
import uk.govuk.app.design.ui.component.Title3BoldLabel
import uk.govuk.app.design.ui.theme.GovUkTheme
import uk.govuk.app.topics.TopicsViewModel
import uk.govuk.app.topics.data.remote.model.TopicItem

@Composable
fun TopicsWidget(
    modifier: Modifier = Modifier
) {
    val viewModel: TopicsViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()

    Column(modifier = modifier) {
        Title3BoldLabel("Topics")

        SmallVerticalSpacer()

        uiState?.topics?.let{ topics ->
            TopicsGrid(topics)
        }
    }
}

@Composable
private fun TopicsGrid(
    topics: List<TopicItem>,
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
                            TopicsCard(
                                topic = topics[topicIndex],
                                modifier = Modifier.weight(1f)
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

@Composable
private fun TopicsCard(
    topic: TopicItem,
    modifier: Modifier = Modifier
) {
    OutlinedCard(
        modifier.fillMaxHeight(),
        colors = CardDefaults.cardColors(
            containerColor = GovUkTheme.colourScheme.surfaces.card
        ),
        border = BorderStroke(
            width = 1.dp,
            color = GovUkTheme.colourScheme.strokes.listDivider
        )
    ) {
        Column(
            Modifier
                .padding(GovUkTheme.spacing.medium)
        ){
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = null,
                modifier = Modifier.size(25.dp),
                tint = GovUkTheme.colourScheme.surfaces.icon
            )
            MediumVerticalSpacer()
            Spacer(Modifier.weight(1f))
            Row {
                Text(
                    text = topic.title,
                    style = GovUkTheme.typography.bodyBold,
                    modifier = Modifier.weight(1f)
                )
                SmallHorizontalSpacer()
                Icon(
                    painterResource(uk.govuk.app.design.R.drawable.ic_chevron),
                    contentDescription = null,
                    modifier = Modifier.align(Alignment.Bottom),
                    tint = GovUkTheme.colourScheme.textAndIcons.trailingIcon
                )
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
            )
        )
    }
}