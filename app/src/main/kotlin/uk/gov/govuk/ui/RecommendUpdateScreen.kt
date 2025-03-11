package uk.gov.govuk.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.window.core.layout.WindowHeightSizeClass
import uk.gov.govuk.BuildConfig.PLAY_STORE_URL
import uk.gov.govuk.R
import uk.gov.govuk.design.ui.component.BodyRegularLabel
import uk.gov.govuk.design.ui.component.HorizontalButtonGroup
import uk.gov.govuk.design.ui.component.LargeTitleBoldLabel
import uk.gov.govuk.design.ui.component.ListDivider
import uk.gov.govuk.design.ui.component.MediumVerticalSpacer
import uk.gov.govuk.design.ui.component.VerticalButtonGroup
import uk.gov.govuk.design.ui.theme.GovUkTheme

@Composable
internal fun RecommendUpdateScreen(
    recommendUpdateSkipped: () -> Unit,
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
            LargeTitleBoldLabel(stringResource(R.string.recommend_update_title))
            MediumVerticalSpacer()
            BodyRegularLabel(stringResource(R.string.recommend_update_description))
        }

        ListDivider()

        val updateButtonText = stringResource(R.string.recommend_update_button_title_update)
        val skipButtonText = stringResource(R.string.recommend_update_button_title_skip)

        val context = LocalContext.current
        val onUpdateClick: () -> Unit = {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(PLAY_STORE_URL)
            context.startActivity(intent)
        }

        val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
        if (windowSizeClass.windowHeightSizeClass == WindowHeightSizeClass.COMPACT) {
            HorizontalButtonGroup(
                primaryText = updateButtonText,
                onPrimary = onUpdateClick,
                secondaryText = skipButtonText,
                onSecondary = recommendUpdateSkipped
            )
        } else {
            VerticalButtonGroup(
                primaryText = updateButtonText,
                onPrimary = onUpdateClick,
                secondaryText = skipButtonText,
                onSecondary = recommendUpdateSkipped
            )
        }
    }
}

@Preview
@Composable
private fun RecommendUpdatePreview() {
    GovUkTheme {
        RecommendUpdateScreen(
            recommendUpdateSkipped = {},
            Modifier
        )
    }
}
