package uk.govuk.app.settings.ui

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import uk.govuk.app.design.ui.component.BodyRegularLabel
import uk.govuk.app.design.ui.component.CaptionRegularLabel
import uk.govuk.app.design.ui.component.ListDivider
import uk.govuk.app.design.ui.component.ListHeadingLabel
import uk.govuk.app.design.ui.component.TabHeader
import uk.govuk.app.design.ui.component.ToggleSwitch
import uk.govuk.app.design.ui.theme.GovUkTheme
import uk.govuk.app.settings.R
import uk.govuk.app.settings.SettingsViewModel

@Composable
internal fun SettingsRoute(
    appVersion: String,
    onHelpClick: () -> Unit,
    onPrivacyPolicyClick: () -> Unit,
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
            modifier = Modifier.verticalScroll(rememberScrollState())
        ) {
            AboutTheApp(
                appVersion = appVersion,
                onHelpClick = onHelpClick
            )
            PrivacyAndLegal(
                isAnalyticsEnabled = isAnalyticsEnabled,
                onAnalyticsConsentChange = onAnalyticsConsentChange,
                onPrivacyPolicyClick = onPrivacyPolicyClick
            )
            OpenSourceLicenses(onLicenseView)
            Spacer(Modifier.height(100.dp))
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

        // We might want to make this a component when
        // we understand the various use cases better
        OutlinedCard(
            colors = CardDefaults.cardColors(
                containerColor = GovUkTheme.colourScheme.surfaces.card
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(GovUkTheme.spacing.medium)
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
                        uk.govuk.app.design.R.drawable.baseline_open_in_new_24
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

        OutlinedCard(
            colors = CardDefaults.cardColors(
                containerColor = GovUkTheme.colourScheme.surfaces.card
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(GovUkTheme.spacing.medium)
        ) {
            Row(
                Modifier.padding(
                    top = GovUkTheme.spacing.small,
                    bottom = GovUkTheme.spacing.small,
                    start = GovUkTheme.spacing.medium,
                    end = GovUkTheme.spacing.medium
                ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BodyRegularLabel(
                    text = stringResource(R.string.share_setting),
                    modifier = Modifier.weight(1f),
                )

                ToggleSwitch(
                    checked = isAnalyticsEnabled,
                    onCheckedChange = onAnalyticsConsentChange,
                )
            }
        }

        Row(
            Modifier.padding(
                top = 1.dp,
                start = GovUkTheme.spacing.extraLarge,
                end = GovUkTheme.spacing.extraLarge,
                bottom = 1.dp
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CaptionRegularLabel(
                text = stringResource(R.string.privacy_description)
            )
        }

        Row(
            Modifier
                .padding(
                    top = 1.dp,
                    start = GovUkTheme.spacing.extraLarge,
                    end = GovUkTheme.spacing.extraLarge,
                    bottom = GovUkTheme.spacing.medium
                )
                .clickable(onClick = onPrivacyPolicyClick),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val altText = "${stringResource(R.string.privacy_read_more)} " +
                stringResource(id = R.string.link_opens_in)

            CaptionRegularLabel(
                text = stringResource(R.string.privacy_read_more),
                modifier = Modifier.semantics {
                    contentDescription = altText
                },
                color = GovUkTheme.colourScheme.textAndIcons.link,
            )
        }
    }
}

@Composable
private fun OpenSourceLicenses(
    onLicenseView: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Column(
        modifier = modifier
    ) {
        OutlinedCard(
            colors = CardDefaults.cardColors(
                containerColor = GovUkTheme.colourScheme.surfaces.card
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(GovUkTheme.spacing.medium)
        ) {
            Row(
                Modifier
                    .clickable(onClick = {
                        onLicenseView()
                        context.startActivity(
                            Intent(
                                context,
                                OssLicensesMenuActivity::class.java
                            )
                        )
                    })
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
