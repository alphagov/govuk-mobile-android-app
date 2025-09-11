package uk.gov.govuk.chat.ui.chat

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import uk.gov.govuk.chat.R
import uk.gov.govuk.chat.ui.model.ChatEntry
import uk.gov.govuk.design.ui.component.MediumVerticalSpacer
import uk.gov.govuk.design.ui.theme.GovUkTheme

@Composable
internal fun DisplayChatEntry(
    isLoading: Boolean,
    chatEntry: ChatEntry,
    launchBrowser: (url: String) -> Unit,
    onMarkdownLinkClicked: (String, String) -> Unit,
    onSourcesExpanded: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        MediumVerticalSpacer()
        Question(question = chatEntry.question)

        MediumVerticalSpacer()
        if (isLoading && chatEntry.answer.isEmpty()) {
            Loading()
        } else {
            Answer(
                answer = chatEntry.answer,
                sources = chatEntry.sources,
                launchBrowser = launchBrowser,
                onMarkdownLinkClicked = onMarkdownLinkClicked,
                onSourcesExpanded = onSourcesExpanded
            )
        }
    }
}

@Composable
private fun Loading(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(R.drawable.baseline_circle_24),
            contentDescription = null,
            tint = GovUkTheme.colourScheme.textAndIcons.chatLoadingIcon,
            modifier = Modifier
                .padding(end = GovUkTheme.spacing.small),
        )

        Text(
            text = stringResource(R.string.loading_text),
            color = GovUkTheme.colourScheme.textAndIcons.chatLoadingTextDark,
            style = GovUkTheme.typography.bodyRegular
        )
    }
}