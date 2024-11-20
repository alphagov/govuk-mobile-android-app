package uk.govuk.app.networking.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import uk.govuk.app.design.ui.component.BodyBoldLabel
import uk.govuk.app.design.ui.component.BodyRegularLabel
import uk.govuk.app.design.ui.theme.GovUkTheme
import uk.govuk.app.networking.R

@Composable
fun OfflineMessage(
    title: String? = null,
    description: String? = null,
    linkTitle: String? = null,
    onTryAgainClick: () -> Unit
) {
    val messageTitle = title ?: run { stringResource(R.string.no_internet_title) }
    val messageDescription = description ?: run { stringResource(R.string.no_internet_description) }
    val messageLinkTitle = linkTitle ?: run { stringResource(R.string.try_again) }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            Modifier.padding(
                GovUkTheme.spacing.medium,
                GovUkTheme.spacing.large,
                GovUkTheme.spacing.medium,
                0.dp
            ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            BodyBoldLabel(
                text = messageTitle,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
        }

        Row(
            Modifier.padding(
                GovUkTheme.spacing.medium,
                GovUkTheme.spacing.small,
                GovUkTheme.spacing.medium,
                GovUkTheme.spacing.medium
            ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            BodyRegularLabel(
                text = messageDescription,
                modifier = Modifier.align(Alignment.CenterVertically),
                textAlign = TextAlign.Center
            )
        }

        Row(
            Modifier.padding(
                GovUkTheme.spacing.medium,
                GovUkTheme.spacing.small,
                GovUkTheme.spacing.medium,
                GovUkTheme.spacing.medium
            ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            TextButton(
                onClick = { onTryAgainClick() }
            ) {
                BodyRegularLabel(
                    text = messageLinkTitle,
                    modifier = Modifier.align(Alignment.CenterVertically),
                    textAlign = TextAlign.Center,
                    color = GovUkTheme.colourScheme.textAndIcons.link
                )
            }
        }
    }
}

@Preview
@Composable
private fun OfflineMessagePreview() {
    GovUkTheme {
        OfflineMessage(onTryAgainClick = {})
    }
}
