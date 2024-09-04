package uk.govuk.app.settings.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import uk.govuk.app.design.ui.component.BaseHeader
import uk.govuk.app.design.ui.component.BodyRegularLabel
import uk.govuk.app.design.ui.component.ListDivider
import uk.govuk.app.design.ui.component.SettingsHeader
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

    Column(modifier) {
        BaseHeader("Settings")
        AboutTheApp(onButtonClick)
        PrivacyAndLegal()
    }
}

@Composable
private fun AboutTheApp(
    onButtonClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    SettingsHeader("About the app")

    OutlinedCard(
        colors = CardDefaults.cardColors(
            containerColor = GovUkTheme.colourScheme.surfaces.card
        ),
        modifier = Modifier.fillMaxWidth()
            .padding(GovUkTheme.spacing.medium)
    ) {
        Row(
            Modifier.padding(GovUkTheme.spacing.medium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BodyRegularLabel(
                text = "Help and feedback",
                modifier = Modifier.weight(1f),
                color = GovUkTheme.colourScheme.textAndIcons.link,
            )

            Icon(
                painter = painterResource(R.drawable.baseline_open_in_new_24),
                contentDescription = "",
                tint = GovUkTheme.colourScheme.textAndIcons.link,
                modifier = Modifier.clickable(onClick = onButtonClick)
            )
        }

        ListDivider(Modifier.padding(
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
                text = "App version number",
                modifier = Modifier.weight(1f)
            )

            BodyRegularLabel(text = "1.0")
        }
    }
}

@Composable
private fun PrivacyAndLegal(
    modifier: Modifier = Modifier
) {
    SettingsHeader("Privacy and legal")

    OutlinedCard(
        colors = CardDefaults.cardColors(
            containerColor = GovUkTheme.colourScheme.surfaces.card
        ),
        modifier = Modifier.fillMaxWidth()
            .padding(GovUkTheme.spacing.medium)
    ) {
        Row(
            Modifier.padding(GovUkTheme.spacing.medium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BodyRegularLabel(
                text = "Share app usage statistics",
                modifier = Modifier.weight(1f),
            )

            Switch(
                checked = true,
                onCheckedChange = {},

                colors = SwitchDefaults.colors(
                    checkedThumbColor = GovUkTheme.colourScheme.textAndIcons.buttonPrimaryHighlight,
                    checkedTrackColor = GovUkTheme.colourScheme.surfaces.primary,
                    uncheckedThumbColor = GovUkTheme.colourScheme.textAndIcons.buttonPrimaryDisabled,
                    uncheckedTrackColor = GovUkTheme.colourScheme.surfaces.buttonPrimaryDisabled,
                )
            )
        }
    }
}
