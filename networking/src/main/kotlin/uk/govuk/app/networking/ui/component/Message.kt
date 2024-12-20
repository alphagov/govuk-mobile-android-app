package uk.govuk.app.networking.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import uk.govuk.app.design.ui.component.BodyBoldLabel
import uk.govuk.app.design.ui.component.BodyRegularLabel
import uk.govuk.app.design.ui.component.SecondaryButton
import uk.govuk.app.design.ui.theme.GovUkTheme

@Composable
internal fun Message(
    title: String,
    description: String,
    buttonTitle: String,
    modifier: Modifier = Modifier,
    externalLink: Boolean = false,
    onButtonClick: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
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
                .padding(
                    horizontal = GovUkTheme.spacing.large,
                    vertical = GovUkTheme.spacing.small
                ),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.Center
        ) {
            SecondaryButton(
                text = buttonTitle,
                onClick = { onButtonClick() },
                externalLink = externalLink
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
            buttonTitle = "Button Title",
            onButtonClick = {}
        )
    }
}
