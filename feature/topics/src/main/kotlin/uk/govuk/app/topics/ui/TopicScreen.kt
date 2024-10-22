package uk.govuk.app.topics.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import uk.govuk.app.design.ui.component.Title1BoldLabel
import uk.govuk.app.topics.TopicViewModel

@Composable
internal fun TopicRoute(
    modifier: Modifier = Modifier
) {
    val viewModel: TopicViewModel = hiltViewModel()

    TopicScreen(
        modifier = modifier
    )
}

@Composable
private fun TopicScreen(
    modifier: Modifier = Modifier
) {
    Title1BoldLabel(
        text = "Blah blah blah",
        modifier = modifier
    )
}