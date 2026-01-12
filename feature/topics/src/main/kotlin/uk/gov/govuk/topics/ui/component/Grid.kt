package uk.gov.govuk.topics.ui.component

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import uk.gov.govuk.design.ui.component.SmallHorizontalSpacer
import uk.gov.govuk.design.ui.model.SINGLE_COLUMN_THRESHOLD_DP
import uk.gov.govuk.topics.ui.model.TopicItemUi

private const val COMPACT_THRESHOLD_DP = 600
private const val THREE_COLUMN_THRESHOLD_DP = 650
private const val MEDIUM_THRESHOLD_DP = 840
private const val FONT_SCALE_THRESHOLD = 1f

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