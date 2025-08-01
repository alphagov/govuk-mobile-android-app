package uk.gov.govuk.chat.ui.chat

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import uk.gov.govuk.chat.R
import uk.gov.govuk.design.ui.component.BodyBoldLabel
import uk.gov.govuk.design.ui.theme.GovUkTheme

@Composable
fun Answer(
    answer: String,
    modifier: Modifier = Modifier,
    showHeader: Boolean = true,
    sources: List<String>? = null
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = GovUkTheme.colourScheme.surfaces.chatBotMessageBackground,
            contentColor = GovUkTheme.colourScheme.textAndIcons.chatBotMessageText
        ),
        border = BorderStroke(1.dp, GovUkTheme.colourScheme.strokes.chatBotMessageBorder),
        modifier = Modifier
            .fillMaxWidth()
    ) {
        if (showHeader) {
            BodyBoldLabel(
                text = stringResource(id = R.string.bot_header_text),
                modifier = Modifier.padding(GovUkTheme.spacing.medium)
            )
        }

        Markdown(
            text = answer,
            talkbackText = answer,
            modifier = modifier
        )

        if (!sources.isNullOrEmpty()) {
            Sources(sources = sources)
        }
    }
}
