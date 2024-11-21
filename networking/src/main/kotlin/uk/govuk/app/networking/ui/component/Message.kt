package uk.govuk.app.networking.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import uk.govuk.app.design.ui.component.BodyBoldLabel
import uk.govuk.app.design.ui.component.BodyRegularLabel
import uk.govuk.app.design.ui.theme.GovUkTheme

@Composable
internal fun Message(
    title: String,
    description: String,
    linkTitle: String,
    modifier: Modifier = Modifier,
    hasExternalLink: Boolean = false,
    onLinkClick: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            Modifier.padding(
                start = GovUkTheme.spacing.large,
                top = GovUkTheme.spacing.extraLarge,
                end = GovUkTheme.spacing.large
            ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            BodyBoldLabel(
                text = title,
                modifier = Modifier.align(Alignment.CenterVertically),
                textAlign = TextAlign.Center
            )
        }

        Row(
            Modifier.padding(
                start = GovUkTheme.spacing.large,
                top = GovUkTheme.spacing.small,
                end = GovUkTheme.spacing.large
            ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            BodyRegularLabel(
                text = description,
                modifier = Modifier.align(Alignment.CenterVertically),
                textAlign = TextAlign.Center
            )
        }

        Row(
            Modifier
                .padding(all = GovUkTheme.spacing.large)
                .clickable(onClick = { onLinkClick() }),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.Center
        ) {
            BodyRegularLabel(
                text = linkTitle,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .weight(1f, fill = false),
                color = GovUkTheme.colourScheme.textAndIcons.link,
                textAlign = TextAlign.Center
            )

            if (!hasExternalLink) return

            Icon(
                painter = painterResource(
                    uk.govuk.app.design.R.drawable.ic_external_link
                ),
                contentDescription = stringResource(
                    uk.govuk.app.design.R.string.opens_in_web_browser
                ),
                tint = GovUkTheme.colourScheme.textAndIcons.link,
                modifier = Modifier
                    .padding(start = GovUkTheme.spacing.small)
                    .align(Alignment.CenterVertically)
            )
        }
    }
}

@Preview
@Composable
private fun MessagePreview() {
    GovUkTheme {
        Message(
            title = "Title",
            description = "Description",
            linkTitle = "Link Title",
            onLinkClick = {}
        )
    }
}
