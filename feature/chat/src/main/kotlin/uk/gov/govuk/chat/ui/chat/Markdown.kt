package uk.gov.govuk.chat.ui.chat

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import dev.jeziellago.compose.markdowntext.MarkdownText
import uk.gov.govuk.design.ui.theme.GovUkTheme

@Composable
fun Markdown(
    text: String,
    talkbackText: String,
    modifier: Modifier = Modifier
) {
    MarkdownText(
        markdown = text,
        linkColor = GovUkTheme.colourScheme.textAndIcons.link,
        style = TextStyle(
            color = GovUkTheme.colourScheme.textAndIcons.primary,
            fontSize = GovUkTheme.typography.bodyRegular.fontSize,
            fontFamily = GovUkTheme.typography.bodyRegular.fontFamily,
            fontWeight = GovUkTheme.typography.bodyRegular.fontWeight
        ),
        enableSoftBreakAddsNewLine = false,
        enableUnderlineForLink = false,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = GovUkTheme.spacing.medium)
            .semantics {
                contentDescription = talkbackText
            }
    )
}
