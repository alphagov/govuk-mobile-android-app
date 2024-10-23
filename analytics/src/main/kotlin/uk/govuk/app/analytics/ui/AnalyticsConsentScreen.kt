package uk.govuk.app.analytics.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.window.core.layout.WindowHeightSizeClass
import uk.govuk.app.analytics.AnalyticsViewModel
import uk.govuk.app.analytics.R
import uk.govuk.app.design.ui.component.BodyRegularLabel
import uk.govuk.app.design.ui.component.HorizontalButtonGroup
import uk.govuk.app.design.ui.component.LargeTitleBoldLabel
import uk.govuk.app.design.ui.component.ListDivider
import uk.govuk.app.design.ui.component.MediumHorizontalSpacer
import uk.govuk.app.design.ui.component.MediumVerticalSpacer
import uk.govuk.app.design.ui.component.SmallHorizontalSpacer
import uk.govuk.app.design.ui.component.SmallVerticalSpacer
import uk.govuk.app.design.ui.component.VerticalButtonGroup
import uk.govuk.app.design.ui.theme.GovUkTheme

@Composable
internal fun AnalyticsConsentRoute(
    onPrivacyPolicyClick: () -> Unit,
    analyticsConsentCompleted: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: AnalyticsViewModel = hiltViewModel()

    AnalyticsConsentScreen(
        onPrivacyPolicyClick = onPrivacyPolicyClick,
        onConsentGranted = {
            viewModel.onConsentGranted()
            analyticsConsentCompleted()
        },
        onConsentDenied = {
            viewModel.onConsentDenied()
            analyticsConsentCompleted()
        },
        modifier = modifier
    )
}

@Composable
private fun AnalyticsConsentScreen(
    onPrivacyPolicyClick: () -> Unit,
    onConsentGranted: () -> Unit,
    onConsentDenied: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier
            .fillMaxWidth()
    ) {
        Column(
            Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = GovUkTheme.spacing.medium)
                .padding(top = GovUkTheme.spacing.medium)
        ) {
            LargeTitleBoldLabel(stringResource(R.string.analytics_consent_title))
            MediumVerticalSpacer()
            BodyRegularLabel(stringResource(R.string.analytics_consent_bullet_title))
            MediumVerticalSpacer()
            BulletList()
            MediumVerticalSpacer()
            BodyRegularLabel(stringResource(R.string.analytics_consent_anonymous))
            MediumVerticalSpacer()
            BodyRegularLabel(stringResource(R.string.analytics_consent_stop))
            MediumVerticalSpacer()
            PrivacyPolicyLink(onPrivacyPolicyClick)
            MediumVerticalSpacer()
        }

        ListDivider()

        val enableButtonText = stringResource(R.string.analytics_consent_button_enable)
        val disableButtonText = stringResource(R.string.analytics_consent_button_disable)
        val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
        if (windowSizeClass.windowHeightSizeClass == WindowHeightSizeClass.COMPACT) {
            HorizontalButtonGroup(
                primaryText = enableButtonText,
                onPrimary = onConsentGranted,
                secondaryText = disableButtonText,
                onSecondary = onConsentDenied
            )
        } else {
            VerticalButtonGroup(
                primaryText = enableButtonText,
                onPrimary = onConsentGranted,
                secondaryText = disableButtonText,
                onSecondary = onConsentDenied
            )
        }
    }
}

@Composable
private fun BulletList(
    modifier: Modifier = Modifier
) {
    Column(modifier) {
        BulletItem(stringResource(R.string.analytics_consent_bullet_1))
        SmallVerticalSpacer()
        BulletItem(stringResource(R.string.analytics_consent_bullet_2))
        SmallVerticalSpacer()
        BulletItem(stringResource(R.string.analytics_consent_bullet_3))
        SmallVerticalSpacer()
        BulletItem(stringResource(R.string.analytics_consent_bullet_4))
    }
}

@Composable
private fun BulletItem(
    text: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(GovUkTheme.colourScheme.textAndIcons.primary)
        )
        MediumHorizontalSpacer()
        BodyRegularLabel(text)
    }
}

@Composable
private fun PrivacyPolicyLink(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier
            .clickable { onClick() }
    ) {
        BodyRegularLabel(
            text = stringResource(R.string.analytics_consent_privacy_policy),
            modifier = Modifier.weight(1f),
            color = GovUkTheme.colourScheme.textAndIcons.link,
        )
        SmallHorizontalSpacer()
        Icon(
            painter = painterResource(
                uk.govuk.app.design.R.drawable.ic_external_link
            ),
            contentDescription = stringResource(R.string.analytics_consent_link_opens_in),
            tint = GovUkTheme.colourScheme.textAndIcons.link
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AnalyticsConsentPreview() {
    GovUkTheme {
        AnalyticsConsentScreen(
            onPrivacyPolicyClick = { },
            onConsentGranted = { },
            onConsentDenied = { }
        )
    }
}