package uk.govuk.app.topics.ui

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
import uk.govuk.app.design.ui.component.ChildPageHeader
import uk.govuk.app.design.ui.component.MediumVerticalSpacer
import uk.govuk.app.design.ui.component.TopicHorizontalCard
import uk.govuk.app.design.ui.theme.GovUkTheme
import uk.govuk.app.topics.AllTopicsViewModel
import uk.govuk.app.topics.R
import uk.govuk.app.topics.ui.model.TopicUi

@Composable
internal fun AllTopicsRoute(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: AllTopicsViewModel = hiltViewModel()
    val topics by viewModel.topics.collectAsState()

    AllTopicsScreen(
        topics = topics,
        onPageView = { title -> viewModel.onPageView(title) },
        onBack = onBack,
        modifier = modifier
    )
}

@Composable
private fun AllTopicsScreen(
    topics: List<TopicUi>?,
    onPageView: (String) -> Unit,
    onBack: () -> Unit,
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
                items(topics) { topic ->
                    Column {
                        TopicHorizontalCard(
                            icon = topic.icon,
                            title = topic.title,
                            onClick = { }
                        )
                        MediumVerticalSpacer()
                    }
                }
            }
        }
    }
}