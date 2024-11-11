package uk.govuk.app.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import uk.govuk.app.R
import uk.govuk.app.design.ui.component.BodyRegularLabel
import uk.govuk.app.design.ui.component.LargeTitleBoldLabel
import uk.govuk.app.design.ui.component.ListDivider
import uk.govuk.app.design.ui.component.MediumVerticalSpacer
import uk.govuk.app.design.ui.component.SecondaryButton
import uk.govuk.app.design.ui.theme.GovUkTheme

@Composable
internal fun AppUnavailableRoute(
    onGoToGovUkClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    AppUnavailableScreen(
        onGoToGovUkClick = onGoToGovUkClick,
        modifier = modifier
    )
}

@Composable
private fun AppUnavailableScreen(
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
            LargeTitleBoldLabel(stringResource(R.string.app_unavailable_title))
            MediumVerticalSpacer()
            BodyRegularLabel(stringResource(R.string.app_unavailable_description))
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
    val text = stringResource(R.string.app_unavailable_button_title)
    SecondaryButton(
        text = text,
        onClick = { onGoToGovUkClick() },
        externalLink = true
    )
}

@Preview
@Composable
private fun AppUnavailablePreview() {
    GovUkTheme {
        AppUnavailableScreen(
            onGoToGovUkClick = {},
            Modifier
        )
    }
}
