package uk.govuk.app.topics.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import uk.govuk.app.design.ui.component.ChildPageHeader
import uk.govuk.app.design.ui.component.MediumVerticalSpacer
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
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                Modifier.padding(horizontal = GovUkTheme.spacing.medium),
            ) {
                item {
                    MediumVerticalSpacer()
                }

                items(topics) { topic ->
                    TopicSelectionCard(
                        icon = topic.icon,
                        title = topic.title,
                        description = topic.description,
//                        onClick = { onClick(topic.title) }
                        isSelected = false
                    )
                }
            }
        }
    }
}