package uk.govuk.app.settings.ui

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import uk.govuk.app.design.ui.component.BodyRegularLabel
import uk.govuk.app.design.ui.component.CaptionRegularLabel
import uk.govuk.app.design.ui.component.Header
import uk.govuk.app.design.ui.component.ListDivider
import uk.govuk.app.design.ui.component.ListHeadingLabel
import uk.govuk.app.design.ui.component.ToggleSwitch
import uk.govuk.app.design.ui.theme.GovUkTheme
import uk.govuk.app.settings.R
import uk.govuk.app.settings.SettingsViewModel

@Composable
internal fun SettingsRoute(
    onButtonClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: SettingsViewModel = hiltViewModel()

    SettingsScreen(
        onPageView = { viewModel.onPageView() },
        onButtonClick = onButtonClick,
        modifier = modifier
    )
}

@Composable
private fun SettingsScreen(
    onPageView: () -> Unit,
    onButtonClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(Unit) {
        onPageView()
    }

    Column(
        modifier = modifier
    ) {
        Header(stringResource(R.string.screen_title))
        Column(
            modifier = Modifier.verticalScroll(rememberScrollState())
        ) {
            AboutTheApp(onButtonClick)
            PrivacyAndLegal()
            Spacer(Modifier.height(100.dp))
        }
    }
}

@Composable
private fun AboutTheApp(
    onButtonClick: () -> Unit,
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
            modifier = Modifier.fillMaxWidth()
                .padding(GovUkTheme.spacing.medium)
        ) {
            Row(
                Modifier.padding(GovUkTheme.spacing.medium)
                    .clickable(onClick = onButtonClick),
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
                    contentDescription = "",
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
                Modifier.fillMaxWidth()
                    .padding(GovUkTheme.spacing.medium),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BodyRegularLabel(
                    text = stringResource(R.string.version_setting),
                    modifier = Modifier.weight(1f)
                )

                BodyRegularLabel(text = "1.0")
            }
        }
    }
}

@Composable
private fun PrivacyAndLegal(
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
            modifier = Modifier.fillMaxWidth()
                .padding(GovUkTheme.spacing.medium)
        ) {
            Row(
                modifier.padding(
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
                    onCheckedChange = {},
                    modifier = modifier
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
            Modifier.padding(
                top = 1.dp,
                start = GovUkTheme.spacing.extraLarge,
                end = GovUkTheme.spacing.extraLarge,
                bottom = GovUkTheme.spacing.medium
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CaptionRegularLabel(
                text = stringResource(R.string.privacy_read_more),
                color = GovUkTheme.colourScheme.textAndIcons.link,
            )
        }
    }
}
