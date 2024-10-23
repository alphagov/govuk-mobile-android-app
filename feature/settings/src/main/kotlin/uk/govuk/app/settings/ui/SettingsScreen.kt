package uk.govuk.app.settings.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedCard
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import uk.govuk.app.design.ui.component.BodyRegularLabel
import uk.govuk.app.design.ui.component.CaptionRegularLabel
import uk.govuk.app.design.ui.component.LargeVerticalSpacer
import uk.govuk.app.design.ui.component.ListDivider
import uk.govuk.app.design.ui.component.ListHeadingLabel
import uk.govuk.app.design.ui.component.MediumVerticalSpacer
import uk.govuk.app.design.ui.component.SmallVerticalSpacer
import uk.govuk.app.design.ui.component.TabHeader
import uk.govuk.app.design.ui.component.ToggleListItem
import uk.govuk.app.design.ui.theme.GovUkTheme
import uk.govuk.app.settings.R
import uk.govuk.app.settings.SettingsViewModel

@Composable
internal fun SettingsRoute(
    appVersion: String,
    onHelpClick: () -> Unit,
    onPrivacyPolicyClick: () -> Unit,
    onOpenSourceLicenseClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: SettingsViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()

    uiState?.let {
        SettingsScreen(
            appVersion = appVersion,
            isAnalyticsEnabled = it.isAnalyticsEnabled,
            onPageView = { viewModel.onPageView() },
            onLicenseView = { viewModel.onLicenseView() },
            onHelpClick = onHelpClick,
            onAnalyticsConsentChange = { enabled -> viewModel.onAnalyticsConsentChanged(enabled) },
            onPrivacyPolicyClick = onPrivacyPolicyClick,
            onOpenSourceLicenseClick = onOpenSourceLicenseClick,
            modifier = modifier
        )
    }
}

@Composable
private fun SettingsScreen(
    appVersion: String,
    isAnalyticsEnabled: Boolean,
    onPageView: () -> Unit,
    onLicenseView: () -> Unit,
    onHelpClick: () -> Unit,
    onAnalyticsConsentChange: (Boolean) -> Unit,
    onPrivacyPolicyClick: () -> Unit,
    onOpenSourceLicenseClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(Unit) {
        onPageView()
    }

    Column(
        modifier = modifier
    ) {
        TabHeader(stringResource(R.string.screen_title))
        SmallVerticalSpacer()
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(horizontal = GovUkTheme.spacing.medium)
                .padding(bottom = GovUkTheme.spacing.extraLarge)
        ) {
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
            OpenSourceLicenses(onLicenseView, onOpenSourceLicenseClick)
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

        // We might want to make this a component when
        // we understand the various use cases better
        OutlinedCard(
            colors = CardDefaults.cardColors(
                containerColor = GovUkTheme.colourScheme.surfaces.card
            ),
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Row(
                Modifier
                    .clickable(onClick = onHelpClick)
                    .padding(GovUkTheme.spacing.medium),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BodyRegularLabel(
                    text = stringResource(R.string.help_setting),
                    modifier = Modifier.weight(1f),
                    color = GovUkTheme.colourScheme.textAndIcons.link,
                )

                Icon(
                    painter = painterResource(
                        uk.govuk.app.design.R.drawable.ic_external_link
                    ),
                    contentDescription = stringResource(R.string.link_opens_in),
                    tint = GovUkTheme.colourScheme.textAndIcons.link
                )
            }

            ListDivider(
                Modifier.padding(
                    top = 1.dp,
                    bottom = 1.dp,
                    start = GovUkTheme.spacing.medium,
                    end = GovUkTheme.spacing.medium
                )
            )

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

        CaptionRegularLabel(
            text = stringResource(R.string.privacy_description),
            modifier = Modifier
                .padding(top = GovUkTheme.spacing.medium)
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
private fun OpenSourceLicenses(
    onLicenseView: () -> Unit,
    onOpenSourceLicenseClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        OutlinedCard(
            colors = CardDefaults.cardColors(
                containerColor = GovUkTheme.colourScheme.surfaces.card
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                Modifier
                    .clickable(
                        onClick = {
                            onLicenseView()
                            onOpenSourceLicenseClick()
                        }
                    )
                    .padding(GovUkTheme.spacing.medium),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BodyRegularLabel(
                    text = stringResource(R.string.oss_licenses_title),
                    modifier = Modifier.weight(1f),
                    color = GovUkTheme.colourScheme.textAndIcons.link,
                )
            }
        }
    }
}
