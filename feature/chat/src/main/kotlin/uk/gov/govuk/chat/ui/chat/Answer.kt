package uk.gov.govuk.chat.ui.chat

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import uk.gov.govuk.chat.R
import uk.gov.govuk.chat.domain.Analytics
import uk.gov.govuk.design.ui.component.BodyBoldLabel
import uk.gov.govuk.design.ui.component.MediumVerticalSpacer
import uk.gov.govuk.design.ui.theme.GovUkTheme

@Composable
internal fun Answer(
    answer: String,
    launchBrowser: (url: String) -> Unit,
    onMarkdownLinkClicked: (String, String) -> Unit,
    onSourcesExpanded: () -> Unit,
    modifier: Modifier = Modifier,
    showHeader: Boolean = true,
    sources: List<String>? = null
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = GovUkTheme.colourScheme.surfaces.chatBotMessageBackground,
            contentColor = GovUkTheme.colourScheme.textAndIcons.chatBotMessageText
        ),
        modifier = modifier.fillMaxWidth()
    ) {
        MediumVerticalSpacer()

        if (showHeader) {
            BodyBoldLabel(
                text = stringResource(id = R.string.bot_header_text),
                modifier = Modifier.padding(
                    start = GovUkTheme.spacing.medium,
                    end = GovUkTheme.spacing.medium
                )
            )
        }

        Markdown(
            text = answer,
            talkbackText = answer,
            launchBrowser = launchBrowser,
            onMarkdownLinkClicked = onMarkdownLinkClicked,
            markdownLinkType = Analytics.RESPONSE_LINK_CLICKED
        )

        if (!sources.isNullOrEmpty()) {
            Sources(
                sources = sources,
                launchBrowser = launchBrowser,
                onMarkdownLinkClicked = onMarkdownLinkClicked,
                onSourcesExpanded = onSourcesExpanded
            )
        }

        MediumVerticalSpacer()
    }
}
