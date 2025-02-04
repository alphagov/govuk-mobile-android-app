package uk.govuk.app.topics.ui.component

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import uk.govuk.app.design.ui.component.MediumVerticalSpacer
import uk.govuk.app.design.ui.component.SmallHorizontalSpacer
import uk.govuk.app.topics.ui.model.TopicItemUi

private const val SINGLE_COLUMN_THRESHOLD_DP = 380
private const val COMPACT_THRESHOLD_DP = 600
private const val THREE_COLUMN_THRESHOLD_DP = 650
private const val MEDIUM_THRESHOLD_DP = 840
private const val FONT_SCALE_THRESHOLD = 1f

@Composable
internal fun TopicsGrid(
    topics: List<TopicItemUi>,
    modifier: Modifier = Modifier,
    content: @Composable (Modifier, TopicItemUi) -> Unit
) {

    // Todo - ideally this would be a lazy grid to gain from performance optimizations, however
    //  nested lazy components are not allowed without a non-trivial workaround. The performance
    //  impact should be negligible with the amount of items currently being displayed but we may
    //  have to re-visit this in the future.
    Column(modifier) {
        val columnCount = getColumnCount(LocalConfiguration.current)
        val rowCount = getRowCount(
            topicsCount = topics.size,
            columnCount = columnCount
        )

        for (rowIndex in 0 until rowCount) {
            TopicsRow(
                topics = topics,
                columnCount = columnCount,
                rowIndex = rowIndex,
                content = content
            )
            MediumVerticalSpacer()
        }
    }
}

@Composable
private fun TopicsRow(
    topics: List<TopicItemUi>,
    columnCount: Int,
    rowIndex: Int,
    modifier: Modifier = Modifier,
    content: @Composable (Modifier, TopicItemUi) -> Unit
) {
    Row(
        modifier.height(intrinsicSize = IntrinsicSize.Max)
    ) {
        for (columnIndex in 0 until columnCount) {
            if (columnIndex > 0) {
                SmallHorizontalSpacer()
            }

            val topicIndex = (rowIndex * columnCount) + columnIndex
            if (topicIndex < topics.size) {
                val topic = topics[topicIndex]
                content(
                    Modifier
                        .fillMaxHeight()
                        .weight(1f),
                    topic
                )
            } else {
                Box(Modifier.weight(1f)) {  }
            }

            if (columnIndex < columnCount - 1) {
                SmallHorizontalSpacer()
            }
        }
    }
}

private fun getRowCount(topicsCount: Int, columnCount: Int): Int {
    var rowCount = topicsCount / columnCount
    if (topicsCount.mod(columnCount) > 0) {
        rowCount += 1
    }
    return rowCount
}

private fun getColumnCount(
    configuration: Configuration
): Int {
    val width = configuration.screenWidthDp
    val fontScale = configuration.fontScale

    return when {
        width < SINGLE_COLUMN_THRESHOLD_DP ||
                (width < COMPACT_THRESHOLD_DP && fontScale > FONT_SCALE_THRESHOLD) -> 1
        width < COMPACT_THRESHOLD_DP -> 2
        width < THREE_COLUMN_THRESHOLD_DP ||
                (width < MEDIUM_THRESHOLD_DP && fontScale > FONT_SCALE_THRESHOLD) -> 3
        else -> 4
    }
}