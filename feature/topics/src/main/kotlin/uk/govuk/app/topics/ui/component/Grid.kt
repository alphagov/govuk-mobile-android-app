package uk.govuk.app.topics.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.window.core.layout.WindowWidthSizeClass
import uk.govuk.app.design.ui.component.MediumVerticalSpacer
import uk.govuk.app.design.ui.component.SmallHorizontalSpacer
import uk.govuk.app.topics.ui.model.TopicItemUi

@Composable
internal fun TopicsGrid(
    topics: List<TopicItemUi>,
    modifier: Modifier = Modifier,
    content: @Composable (Modifier, TopicItemUi) -> Unit
) {
    val windowWidthSizeClass = currentWindowAdaptiveInfo().windowSizeClass.windowWidthSizeClass

    // Todo - ideally this would be a lazy grid to gain from performance optimizations, however
    //  nested lazy components are not allowed without a non-trivial workaround. The performance
    //  impact should be negligible with the amount of items currently being displayed but we may
    //  have to re-visit this in the future.
    Column(modifier) {
        val columnCount = getColumnCount(windowWidthSizeClass)
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

private fun getColumnCount(windowWidthSizeClass: WindowWidthSizeClass): Int {
    return if (windowWidthSizeClass == WindowWidthSizeClass.COMPACT) 2 else 4
}