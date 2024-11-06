package uk.govuk.app.topics.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import uk.govuk.app.design.ui.component.ChildPageHeader
import uk.govuk.app.design.ui.component.MediumVerticalSpacer
import uk.govuk.app.design.ui.component.SmallHorizontalSpacer
import uk.govuk.app.design.ui.theme.GovUkTheme
import uk.govuk.app.topics.ui.component.TopicSelectionCard
import uk.govuk.app.topics.ui.model.TopicItemUi

@Composable
internal fun TopicSelectionRoute(
    onBack: () -> Unit,
    onDone: () -> Unit,
    onSkip: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: TopicSelectionViewModel = hiltViewModel()
    val topics by viewModel.topics.collectAsState()

    TopicSelectionScreen(
        topics = topics,
        onPageView = { title -> viewModel.onPageView(title) },
        onBack = onBack,
        onClick = { title ->
//            onClick(title)
//            viewModel.onClick(title)
        },
        onDone = onDone,
        onSkip = onSkip,
        modifier = modifier
    )
}

@Composable
private fun TopicSelectionScreen(
    topics: List<TopicItemUi>?,
    onPageView: (String) -> Unit,
    onBack: () -> Unit,
    onClick: (String) -> Unit,
    onDone: () -> Unit,
    onSkip: () -> Unit,
    modifier: Modifier = Modifier
) {
    val title = "Select relevant topics"

    LaunchedEffect(Unit) {
        onPageView(title)
    }

    Column(modifier) {
        ChildPageHeader(
            text = title,
            onBack = onBack
        )

        if (!topics.isNullOrEmpty()) {
            TopicsGrid(
                topics = topics,
                onClick = { _, _ -> }
            )
        }
    }
}

@Composable
private fun TopicsGrid(
    topics: List<TopicItemUi>,
    onClick: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val columnCount = 2

    LazyColumn(
        modifier.padding(horizontal = GovUkTheme.spacing.medium),
    ) {
        item {
            MediumVerticalSpacer()
        }

        items(
            getRowCount(
                topicsCount = topics.size,
                columnCount = columnCount
            )
        ) { rowIndex ->
            TopicsRow(
                topics = topics,
                columnCount = columnCount,
                rowIndex = rowIndex,
                onClick = onClick
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
    onClick: (String, String) -> Unit,
    modifier: Modifier = Modifier
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
                TopicSelectionCard(
                    icon = topic.icon,
                    title =  topic.title,
                    description = topic.description,
                    isSelected = false,
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
}

private fun getRowCount(topicsCount: Int, columnCount: Int): Int {
    var rowCount = topicsCount / columnCount
    if (topicsCount.mod(columnCount) > 0) {
        rowCount += 1
    }
    return rowCount
}