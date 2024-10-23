package uk.govuk.app.topics.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import uk.govuk.app.design.ui.component.ChildPageHeader
import uk.govuk.app.design.ui.component.ListHeadingLabel
import uk.govuk.app.topics.TopicViewModel
import uk.govuk.app.topics.data.remote.model.RemoteTopic

@Composable
internal fun TopicRoute(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: TopicViewModel = hiltViewModel()
    val topic by viewModel.topic.collectAsState()

    TopicScreen(
        topic = topic,
        onBack = onBack,
        modifier = modifier
    )
}

@Composable
private fun TopicScreen(
    topic: RemoteTopic?,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier.fillMaxSize()) {
        topic?.let {
            ChildPageHeader(
                text = topic.title,
                onBack = onBack
            )

            LazyColumn {
                // Todo - only display if there are popular pages - should probably do this in the view model
                item {
                    ListHeadingLabel("Popular pages in this topic") // Todo - extract string
                }
            }
        }
    }
}