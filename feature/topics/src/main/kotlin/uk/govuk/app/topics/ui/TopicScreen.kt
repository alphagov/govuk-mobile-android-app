package uk.govuk.app.topics.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import uk.govuk.app.design.ui.component.Title1BoldLabel

@Composable
internal fun TopicRoute(
    ref: String?,
    modifier: Modifier = Modifier
) {
    TopicScreen(
        ref = ref,
        modifier = modifier
    )
}

@Composable
private fun TopicScreen(
    ref: String?,
    modifier: Modifier = Modifier
) {
    Title1BoldLabel(
        text = ref ?: "",
        modifier = modifier
    )
}