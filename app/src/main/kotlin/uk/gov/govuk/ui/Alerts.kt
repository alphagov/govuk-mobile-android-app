package uk.gov.govuk.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import uk.gov.govuk.R
import uk.gov.govuk.design.ui.component.BodyBoldLabel
import uk.gov.govuk.design.ui.component.BodyRegularLabel
import uk.gov.govuk.design.ui.component.MediumVerticalSpacer
import uk.gov.govuk.design.ui.theme.GovUkTheme

@Composable
internal fun InfoAlert(
    @StringRes title: Int,
    @StringRes message: Int,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(GovUkTheme.numbers.cornerAndroidList),
        text = {
            Column {
                BodyBoldLabel(
                    text = stringResource(id = title),
                    color = GovUkTheme.colourScheme.textAndIcons.primary
                )
                MediumVerticalSpacer()
                BodyRegularLabel(
                    text = stringResource(id = message),
                    color = GovUkTheme.colourScheme.textAndIcons.secondary
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = onDismiss
            ) {
                BodyBoldLabel(
                    text = stringResource(id = R.string.close_alert_button),
                    color = GovUkTheme.colourScheme.textAndIcons.linkSecondary
                )
            }
        },
        containerColor = GovUkTheme.colourScheme.surfaces.alert
    )
}
