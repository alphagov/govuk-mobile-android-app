package uk.govuk.app.topics.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.window.core.layout.WindowWidthSizeClass
import uk.govuk.app.design.ui.component.BodyRegularLabel
import uk.govuk.app.design.ui.component.HorizontalButtonGroup
import uk.govuk.app.design.ui.component.LargeTitleBoldLabel
import uk.govuk.app.design.ui.component.ListDivider
import uk.govuk.app.design.ui.component.MediumVerticalSpacer
import uk.govuk.app.design.ui.component.SmallHorizontalSpacer
import uk.govuk.app.design.ui.component.VerticalButtonGroup
import uk.govuk.app.design.ui.theme.GovUkTheme
import uk.govuk.app.topics.ui.component.TopicSelectionCard
import uk.govuk.app.topics.ui.model.TopicItemUi

@Composable
internal fun TopicSelectionRoute(
    onDone: () -> Unit,
    onSkip: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: TopicSelectionViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()

    TopicSelectionScreen(
        uiState = uiState,
        onPageView = { title -> viewModel.onPageView(title) },
        onClick = { ref, title ->
//            onClick(title)
            viewModel.onClick(ref, title)
        },
        onDone = onDone,
        onSkip = onSkip,
        modifier = modifier
    )
}

@Composable
private fun TopicSelectionScreen(
    uiState: TopicSelectionUiState?,
    onPageView: (String) -> Unit,
    onClick: (ref: String, title: String) -> Unit,
    onDone: () -> Unit,
    onSkip: () -> Unit,
    modifier: Modifier = Modifier
) {
    val windowWidthSizeClass = currentWindowAdaptiveInfo().windowSizeClass.windowWidthSizeClass
    val columnCount = getColumnCount(windowWidthSizeClass)

    val title = "Select relevant topics"

    LaunchedEffect(Unit) {
        onPageView(title)
    }

    Column(modifier) {
        LargeTitleBoldLabel(
            text = title,
            modifier = modifier
                .fillMaxWidth()
                .padding(top = GovUkTheme.spacing.medium)
                .padding(horizontal = GovUkTheme.spacing.medium)
        )

        TopicsGrid(
            topics = uiState?.topics ?: emptyList(),
            columnCount = columnCount,
            onClick = onClick,
            modifier = Modifier.weight(1f)
        )

        uiState?.let {
            ListDivider()

            val doneButtonText = "Done" // Todo - extract string
            val skipButtonText = "Skip" // Todo - extract string
            val isDoneEnabled = it.isDoneEnabled

            if (windowWidthSizeClass == WindowWidthSizeClass.COMPACT) {
                VerticalButtonGroup(
                    primaryText = doneButtonText,
                    onPrimary = onDone,
                    secondaryText = skipButtonText,
                    onSecondary = onSkip,
                    primaryEnabled = isDoneEnabled
                )
            } else {
                HorizontalButtonGroup(
                    primaryText = doneButtonText,
                    onPrimary = onDone,
                    secondaryText = skipButtonText,
                    onSecondary = onSkip,
                    primaryEnabled = isDoneEnabled
                )
            }
        }
    }
}

// Todo - very similar to topics widget, can we extract and re-use somehow???
@Composable
private fun TopicsGrid(
    topics: List<TopicItemUi>,
    columnCount: Int,
    onClick: (ref: String, title: String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier.padding(horizontal = GovUkTheme.spacing.medium),
    ) {
        item {
            Column {
                BodyRegularLabel(
                    text = "Topics you select will be shown on the app home page so you can find them more easily", // Todo - extract string
                )
                MediumVerticalSpacer()
            }
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
    onClick: (ref: String, title: String) -> Unit,
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
                    isSelected = topic.isSelected,
                    onClick = {
                        onClick(topic.ref, topic.title)
                    },
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

private fun getColumnCount(windowWidthSizeClass: WindowWidthSizeClass): Int {
    return if (windowWidthSizeClass == WindowWidthSizeClass.COMPACT) 2 else 4
}