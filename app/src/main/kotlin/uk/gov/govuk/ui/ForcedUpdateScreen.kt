package uk.gov.govuk.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import uk.gov.govuk.BuildConfig.PLAY_STORE_URL
import uk.gov.govuk.R
import uk.gov.govuk.design.ui.component.BodyRegularLabel
import uk.gov.govuk.design.ui.component.LargeTitleBoldLabel
import uk.gov.govuk.design.ui.component.ListDivider
import uk.gov.govuk.design.ui.component.MediumVerticalSpacer
import uk.gov.govuk.design.ui.component.PrimaryButton
import uk.gov.govuk.design.ui.theme.GovUkTheme

@Composable
internal fun ForcedUpdateScreen(
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
            LargeTitleBoldLabel(stringResource(R.string.forced_update_title))
            MediumVerticalSpacer()
            BodyRegularLabel(stringResource(R.string.forced_update_description))
        }

        ListDivider()

        val context = LocalContext.current
        UpdateButton(
            onUpdateClick = {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(PLAY_STORE_URL)
                context.startActivity(intent)
            }
        )
    }
}

@Composable
private fun UpdateButton(
    onUpdateClick: () -> Unit
) {
    val text = stringResource(R.string.forced_update_button_title_update)
    PrimaryButton(
        text = text,
        onClick = { onUpdateClick() }
    )
}

@Preview
@Composable
private fun ForcedUpdatePreview() {
    GovUkTheme {
        ForcedUpdateScreen(
            Modifier
        )
    }
}
