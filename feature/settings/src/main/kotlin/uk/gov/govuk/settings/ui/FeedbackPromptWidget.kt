package uk.gov.govuk.settings.ui

import androidx.compose.foundation.focusable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.tooling.preview.Preview
import uk.gov.govuk.design.ui.component.HomeNavigationCardLegacy
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.gov.govuk.settings.R

@Composable
fun FeedbackPromptWidget(
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val title = stringResource(R.string.feedback_prompt_widget_title)
    val altText = "$title. ${stringResource(R.string.link_opens_in)}"

    HomeNavigationCardLegacy(
        title = title,
        onClick = { onClick(title) },
        modifier = modifier
            .focusable()
            .clearAndSetSemantics {
                contentDescription = altText
                onClick(label = null, action = null)
            }
    )
}

@Preview
@Composable
private fun FeedbackPromptWidgetPreview() {
    GovUkTheme {
        FeedbackPromptWidget(
            onClick = { }
        )
    }
}