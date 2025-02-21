package uk.govuk.app.settings.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.hilt.navigation.compose.hiltViewModel
import uk.govuk.app.design.ui.component.BodyRegularLabel
import uk.govuk.app.design.ui.component.CaptionRegularLabel
import uk.govuk.app.design.ui.component.CardListItem
import uk.govuk.app.design.ui.component.ExternalLinkListItem
import uk.govuk.app.design.ui.component.InternalLinkListItem
import uk.govuk.app.design.ui.component.LargeVerticalSpacer
import uk.govuk.app.design.ui.component.ListHeadingLabel
import uk.govuk.app.design.ui.component.MediumVerticalSpacer
import uk.govuk.app.design.ui.component.SmallVerticalSpacer
import uk.govuk.app.design.ui.component.TabHeader
import uk.govuk.app.design.ui.component.ToggleListItem
import uk.govuk.app.design.ui.theme.GovUkTheme
import uk.govuk.app.notifications.ui.notificationsPermissionShouldShowRationale
import uk.govuk.app.settings.R
import uk.govuk.app.settings.SettingsViewModel

@Composable
internal fun SettingsRoute(
    appVersion: String,
    onHelpClick: () -> Unit,
    onPrivacyPolicyClick: () -> Unit,
    onAccessibilityStatementClick: () -> Unit,
    onTermsAndConditionsClick: () -> Unit,
    onOpenSourceLicenseClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: SettingsViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()

    uiState?.let {
        SettingsScreen(
            appVersion = appVersion,
            isAnalyticsEnabled = it.isAnalyticsEnabled,
            onPageView = { viewModel.onPageView() },
            onLicenseClick = {
                viewModel.onLicenseView()
                onOpenSourceLicenseClick()
            },
            onHelpClick = {
                viewModel.onHelpAndFeedbackView()
                onHelpClick()
            },
            onAnalyticsConsentChange = { enabled -> viewModel.onAnalyticsConsentChanged(enabled) },
            onPrivacyPolicyClick = {
                viewModel.onPrivacyPolicyView()
                onPrivacyPolicyClick()
            },
            onAccessibilityStatementClick = {
                viewModel.onAccessibilityStatementView()
                onAccessibilityStatementClick()
            },
            onTermsAndConditionsClick = {
                viewModel.onTermsAndConditionsView()
                onTermsAndConditionsClick()
            },
            onNotificationsClick = {
                viewModel.onNotificationsClick()
                onNotificationsClick()
            },
            modifier = modifier
        )
    }
}

@Composable
private fun SettingsScreen(
    appVersion: String,
    isAnalyticsEnabled: Boolean,
    onPageView: () -> Unit,
    onLicenseClick: () -> Unit,
    onHelpClick: () -> Unit,
    onAnalyticsConsentChange: (Boolean) -> Unit,
    onPrivacyPolicyClick: () -> Unit,
    onAccessibilityStatementClick: () -> Unit,
    onTermsAndConditionsClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(Unit) {
        onPageView()
    }

    Column(
        modifier = modifier
    ) {
        TabHeader(stringResource(R.string.screen_title))
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(horizontal = GovUkTheme.spacing.medium)
                .padding(bottom = GovUkTheme.spacing.extraLarge)
        ) {
            SmallVerticalSpacer()
            AboutTheApp(
                appVersion = appVersion,
                onHelpClick = onHelpClick
            )
            LargeVerticalSpacer()
            PrivacyAndLegal(
                isAnalyticsEnabled = isAnalyticsEnabled,
                onAnalyticsConsentChange = onAnalyticsConsentChange,
                onPrivacyPolicyClick = onPrivacyPolicyClick
            )

            MediumVerticalSpacer()

            PrivacyPolicy(onPrivacyPolicyClick)
            AccessibilityStatement(onAccessibilityStatementClick)
            OpenSourceLicenses(onLicenseClick)
            TermsAndConditions(onTermsAndConditionsClick)
            Notifications(onNotificationsClick)
        }
    }
}

@Composable
private fun AboutTheApp(
    appVersion: String,
    onHelpClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        ListHeadingLabel(stringResource(R.string.about_title))

        SmallVerticalSpacer()

        ExternalLinkListItem(
            title = stringResource(R.string.help_and_feedback_title),
            onClick = onHelpClick,
            isFirst = true,
            isLast = false
        )

        CardListItem(
            isFirst = false,
            isLast = true
        ) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(GovUkTheme.spacing.medium),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BodyRegularLabel(
                    text = stringResource(R.string.version_setting),
                    modifier = Modifier.weight(1f)
                )

                BodyRegularLabel(text = appVersion)
            }
        }
    }
}

@Composable
private fun PrivacyAndLegal(
    isAnalyticsEnabled: Boolean,
    onAnalyticsConsentChange: (Boolean) -> Unit,
    onPrivacyPolicyClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        ListHeadingLabel(stringResource(R.string.privacy_title))

        SmallVerticalSpacer()

        ToggleListItem(
            title = stringResource(R.string.share_setting),
            checked = isAnalyticsEnabled,
            onCheckedChange = onAnalyticsConsentChange
        )

        SmallVerticalSpacer()

        CaptionRegularLabel(
            text = stringResource(R.string.privacy_description),
            modifier = Modifier
                .padding(horizontal = GovUkTheme.spacing.medium)
        )

        val altText = "${stringResource(R.string.privacy_read_more)} " +
                stringResource(id = R.string.link_opens_in)

        CaptionRegularLabel(
            text = stringResource(R.string.privacy_read_more),
            modifier = Modifier
                .semantics {
                    contentDescription = altText
                }
                .padding(horizontal = GovUkTheme.spacing.medium)
                .clickable(onClick = onPrivacyPolicyClick),
            color = GovUkTheme.colourScheme.textAndIcons.link,
        )
    }
}

@Composable
private fun PrivacyPolicy(
    onPrivacyPolicyClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ExternalLinkListItem(
        title = stringResource(R.string.privacy_policy_title),
        onClick = onPrivacyPolicyClick,
        modifier = modifier,
        isFirst = true,
        isLast = false,
    )
}

@Composable
private fun AccessibilityStatement(
    onAccessibilityStatementClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ExternalLinkListItem(
        title = stringResource(R.string.accessibility_title),
        onClick = onAccessibilityStatementClick,
        modifier = modifier,
        isFirst = false,
        isLast = false,
    )
}

@Composable
private fun OpenSourceLicenses(
    onLicenseClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    InternalLinkListItem(
        title = stringResource(R.string.oss_licenses_title),
        onClick = onLicenseClick,
        modifier = modifier,
        isFirst = false,
        isLast = false,
    )
}

@Composable
private fun TermsAndConditions(
    onLicenseClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ExternalLinkListItem(
        title = stringResource(R.string.terms_and_conditions_title),
        onClick = onLicenseClick,
        modifier = modifier,
        isFirst = false,
        isLast = false,
    )
}

@Composable
private fun Notifications(
    onNotificationsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ExternalLinkListItem(
        title = stringResource(R.string.notifications_title),
        onClick = onNotificationsClick,
        modifier = modifier,
        isFirst = false,
        isLast = true,
    )
}

