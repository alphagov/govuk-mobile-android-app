package uk.govuk.app.topics.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import uk.govuk.app.design.ui.component.Title1BoldLabel

@Composable
internal fun TopicRoute(
    title: String,
    modifier: Modifier = Modifier
) {
    TopicScreen(
        title = title,
        modifier = modifier
    )
}

@Composable
private fun TopicScreen(
    title: String,
    modifier: Modifier = Modifier
) {
    Title1BoldLabel(
        text = title,
        modifier = modifier
    )
}