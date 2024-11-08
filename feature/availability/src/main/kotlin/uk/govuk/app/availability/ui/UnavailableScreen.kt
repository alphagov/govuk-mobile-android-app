package uk.govuk.app.availability.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.tooling.preview.Preview
import uk.govuk.app.availability.R
import uk.govuk.app.design.ui.component.BodyRegularLabel
import uk.govuk.app.design.ui.component.LargeTitleBoldLabel
import uk.govuk.app.design.ui.component.ListDivider
import uk.govuk.app.design.ui.component.MediumVerticalSpacer
import uk.govuk.app.design.ui.component.SecondaryButton
import uk.govuk.app.design.ui.theme.GovUkTheme

@Composable
internal fun UnavailableRoute(
    onGoToGovUkClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    UnavailableScreen(
        onGoToGovUkClick = onGoToGovUkClick,
        modifier = modifier
    )
}

@Composable
private fun UnavailableScreen(
    onGoToGovUkClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier.fillMaxWidth()
    ) {
        Column(
            Modifier
                .weight(1f)
                .padding(horizontal = GovUkTheme.spacing.medium)
                .padding(top = GovUkTheme.spacing.medium)
        ) {
            LargeTitleBoldLabel(stringResource(R.string.unavailable_title))
            MediumVerticalSpacer()
            BodyRegularLabel(stringResource(R.string.unavailable_description))
        }

        ListDivider()

        GoToGovUkButton(
            onGoToGovUkClick = { onGoToGovUkClick() }
        )
    }
}

@Composable
private fun GoToGovUkButton(
    onGoToGovUkClick: () -> Unit
) {
    val text = stringResource(R.string.unavailable_button_title)
    SecondaryButton(
        text = text,
        onClick = { onGoToGovUkClick() },
        externalLink = true
    )
}

@Preview
@Composable
private fun UnavailablePreview() {
    GovUkTheme {
        UnavailableScreen(
            onGoToGovUkClick = {},
            Modifier
        )
    }
}
