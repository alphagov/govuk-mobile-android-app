package uk.govuk.app.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.window.core.layout.WindowHeightSizeClass
import uk.govuk.app.R
import uk.govuk.app.design.ui.component.BodyRegularLabel
import uk.govuk.app.design.ui.component.HorizontalButtonGroup
import uk.govuk.app.design.ui.component.LargeTitleBoldLabel
import uk.govuk.app.design.ui.component.ListDivider
import uk.govuk.app.design.ui.component.MediumVerticalSpacer
import uk.govuk.app.design.ui.component.VerticalButtonGroup
import uk.govuk.app.design.ui.theme.GovUkTheme

@Composable
internal fun RecommendUpdateRoute(
    onUpdateClick: () -> Unit,
    recommendUpdateSkipped: () -> Unit,
    modifier: Modifier = Modifier
) {
    RecommendUpdateScreen(
        onUpdateClick = onUpdateClick,
        recommendUpdateSkipped = recommendUpdateSkipped,
        modifier = modifier
    )
}

@Composable
private fun RecommendUpdateScreen(
    onUpdateClick: () -> Unit,
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
            onUpdateClick = {},
            recommendUpdateSkipped = {},
            Modifier
        )
    }
}
