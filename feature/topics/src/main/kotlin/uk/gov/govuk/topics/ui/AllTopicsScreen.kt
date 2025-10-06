package uk.gov.govuk.topics.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import uk.gov.govuk.design.ui.component.ChildPageHeader
import uk.gov.govuk.design.ui.component.MediumVerticalSpacer
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.gov.govuk.topics.AllTopicsViewModel
import uk.gov.govuk.topics.R
import uk.gov.govuk.topics.ui.component.TopicHorizontalCard
import uk.gov.govuk.topics.ui.model.TopicItemUi

@Composable
internal fun AllTopicsRoute(
    onBack: () -> Unit,
    onClick: (ref: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: AllTopicsViewModel = hiltViewModel()
    val topics by viewModel.topics.collectAsState()

    AllTopicsScreen(
        topics = topics,
        onPageView = { title -> viewModel.onPageView(title) },
        onBack = onBack,
        onClick = { ref, title ->
            onClick(ref)
            viewModel.onClick(title)
        },
        modifier = modifier
    )
}

@Composable
private fun AllTopicsScreen(
    topics: List<TopicItemUi>?,
    onPageView: (String) -> Unit,
    onBack: () -> Unit,
    onClick: (ref: String, title: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val title = stringResource(R.string.allTopicsTitle)

    LaunchedEffect(Unit) {
        onPageView(title)
    }

    Column(modifier) {
        ChildPageHeader(
            text = title,
            onBack = onBack
        )

        if (!topics.isNullOrEmpty()) {
            LazyColumn(Modifier.padding(horizontal = GovUkTheme.spacing.medium)) {
                item {
                    MediumVerticalSpacer()
                }

                items(topics) { topic ->
                    Column {
                        TopicHorizontalCard(
                            icon = topic.icon,
                            title = topic.title,
                            onClick = { onClick(topic.ref, topic.title) }
                        )
                        MediumVerticalSpacer()
                    }
                }
            }
        }
    }
}
