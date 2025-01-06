package uk.govuk.app.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import uk.govuk.app.R
import uk.govuk.app.design.ui.component.BodyRegularLabel
import uk.govuk.app.design.ui.component.LargeTitleBoldLabel
import uk.govuk.app.design.ui.component.LargeVerticalSpacer
import uk.govuk.app.design.ui.component.ListDivider
import uk.govuk.app.design.ui.component.MediumVerticalSpacer
import uk.govuk.app.design.ui.component.PrimaryButton
import uk.govuk.app.design.ui.component.SmallVerticalSpacer
import uk.govuk.app.design.ui.theme.GovUkTheme

@Composable
internal fun DeviceOfflineScreen(
    onTryAgain: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier.fillMaxWidth()) {
        Column(
            Modifier
                .weight(1f)
                .padding(horizontal = GovUkTheme.spacing.medium)
                .padding(top = GovUkTheme.spacing.medium)
        ) {
            LargeTitleBoldLabel(stringResource(R.string.device_offline_title))

            MediumVerticalSpacer()

            BodyRegularLabel(stringResource(R.string.device_offline_description_1))

            SmallVerticalSpacer()

            BodyRegularLabel(stringResource(R.string.device_offline_description_2))

            Spacer(Modifier.weight(1f))
        }

        ListDivider()

        PrimaryButton(
            text = stringResource(R.string.device_offline_button_title),
            onClick = onTryAgain,
            modifier = Modifier.padding(horizontal = GovUkTheme.spacing.small)
        )

        LargeVerticalSpacer()
    }
}

@Preview
@Composable
private fun DeviceOfflinePreview() {
    GovUkTheme {
        DeviceOfflineScreen(
            onTryAgain = { }
        )
    }
}
