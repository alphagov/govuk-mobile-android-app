package uk.gov.govuk.login.ui

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import uk.gov.govuk.R
import uk.gov.govuk.design.ui.component.BodyRegularLabel
import uk.gov.govuk.design.ui.component.ChildPageHeader
import uk.gov.govuk.design.ui.component.LargeTitleBoldLabel
import uk.gov.govuk.design.ui.component.LargeVerticalSpacer
import uk.gov.govuk.design.ui.component.MediumVerticalSpacer
import uk.gov.govuk.design.ui.component.ToggleListItem
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.gov.govuk.login.BiometricSettingsViewModel

@Composable
internal fun BiometricSettingsRoute(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: BiometricSettingsViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()

    val activity = LocalActivity.current as FragmentActivity

    BiometricSettingsScreen(
        onBack = onBack,
        onPageView = { viewModel.onPageView() },
        isUserSignedIn = uiState,
        onToggle = { viewModel.onToggle(activity) },
        modifier = modifier
    )
}

@Composable
private fun BiometricSettingsScreen(
    onBack: () -> Unit,
    onPageView: () -> Unit,
    isUserSignedIn: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(Unit) {
        onPageView()
    }

    Column(modifier.fillMaxWidth()) {
        ChildPageHeader(
            onBack = onBack
        )

        Column(
            Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(bottom = GovUkTheme.spacing.large)
        ) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .background(GovUkTheme.colourScheme.surfaces.homeHeader)
            ) {
                LargeTitleBoldLabel(
                    text = stringResource(R.string.biometric_settings_title),
                    modifier = Modifier
                        .padding(horizontal = GovUkTheme.spacing.medium)
                        .semantics { heading() },
                    color = GovUkTheme.colourScheme.textAndIcons.header
                )
            }

            LargeVerticalSpacer()

            Column(Modifier.padding(horizontal = GovUkTheme.spacing.medium)) {
                ToggleListItem(
                    title = stringResource(R.string.biometric_settings_toggle),
                    checked = isUserSignedIn,
                    onCheckedChange = { onToggle() }
                )

                MediumVerticalSpacer()

                BodyRegularLabel(stringResource(R.string.biometric_settings_description_1))

                MediumVerticalSpacer()

                BodyRegularLabel(stringResource(R.string.biometric_settings_description_2))

                MediumVerticalSpacer()

                BodyRegularLabel(stringResource(R.string.biometric_settings_description_3))

                MediumVerticalSpacer()

                BodyRegularLabel(stringResource(R.string.biometric_settings_description_4))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun BiometricSettingsPreview() {
    GovUkTheme {
        BiometricSettingsScreen(
            onBack = { },
            onPageView = { },
            isUserSignedIn = true,
            onToggle = { }
        )
    }
}
