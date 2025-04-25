package uk.gov.govuk.login.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import uk.gov.govuk.design.ui.component.BodyRegularLabel
import uk.gov.govuk.design.ui.component.FixedDoubleButtonGroup
import uk.gov.govuk.design.ui.component.LargeHorizontalSpacer
import uk.gov.govuk.design.ui.component.LargeTitleBoldLabel
import uk.gov.govuk.design.ui.component.MediumVerticalSpacer
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.gov.govuk.login.BiometricViewModel
import uk.gov.govuk.login.R

@Composable
internal fun BiometricRoute(
    onCompleted: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: BiometricViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()

    val activity = LocalContext.current as FragmentActivity

    BiometricScreen(
        onPageView = { viewModel.onPageView() },
        onSetupBiometrics = { text ->
            viewModel.onContinue(activity, text)
        },
        onSkip = { text ->
            viewModel.onSkip(text)
            onCompleted()
        },
        modifier = modifier
    )

    LaunchedEffect(uiState) {
        if (uiState) {
            onCompleted()
        }
    }
}

@Composable
private fun BiometricScreen(
    onPageView: () -> Unit,
    onSetupBiometrics: (String) -> Unit,
    onSkip: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(Unit) {
        onPageView()
    }

    Column(modifier.fillMaxSize()) {
        Column(Modifier.weight(1f)) {
            Spacer(Modifier.weight(1F))

            Column(
                modifier = modifier
                    .verticalScroll(rememberScrollState())
                    .fillMaxWidth()
                    .padding(horizontal = GovUkTheme.spacing.medium)
                    .padding(vertical = GovUkTheme.spacing.large),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                IconRow()

                MediumVerticalSpacer()

                LargeTitleBoldLabel(
                    text = stringResource(R.string.login_biometrics_title),
                    textAlign = TextAlign.Center
                )

                MediumVerticalSpacer()

                BodyRegularLabel(
                    text = stringResource(R.string.login_biometrics_description_1),
                    textAlign = TextAlign.Center
                )

                MediumVerticalSpacer()

                BodyRegularLabel(
                    text = stringResource(R.string.login_biometrics_description_2),
                    textAlign = TextAlign.Center
                )

                MediumVerticalSpacer()

                BodyRegularLabel(
                    text = stringResource(R.string.login_biometrics_description_3),
                    textAlign = TextAlign.Center
                )
            }

            Spacer(Modifier.weight(1F))
        }

        val primaryButtonText = stringResource(R.string.login_biometrics_button)
        val secondaryButtonText = stringResource(R.string.login_biometrics_skip_button)

        FixedDoubleButtonGroup(
            primaryText = primaryButtonText,
            onPrimary = { onSetupBiometrics(primaryButtonText) },
            secondaryText = secondaryButtonText,
            onSecondary = { onSkip(secondaryButtonText) }
        )
    }
}

@Composable
private fun IconRow(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.height(IntrinsicSize.Max),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_face),
            contentDescription = null,
            tint = GovUkTheme.colourScheme.textAndIcons.primary
        )

        LargeHorizontalSpacer()

        VerticalDivider(
            thickness = 2.dp,
            color = GovUkTheme.colourScheme.textAndIcons.secondary // Todo - should probably be a stroke colour
        )

        LargeHorizontalSpacer()

        Icon(
            painter = painterResource(id = R.drawable.ic_fingerprint),
            contentDescription = null,
            tint = GovUkTheme.colourScheme.textAndIcons.primary
        )

        LargeHorizontalSpacer()

        VerticalDivider(
            thickness = 2.dp,
            color = GovUkTheme.colourScheme.textAndIcons.secondary // Todo - should probably be a stroke colour
        )

        LargeHorizontalSpacer()

        Icon(
            painter = painterResource(id = R.drawable.ic_iris),
            contentDescription = null,
            tint = GovUkTheme.colourScheme.textAndIcons.primary
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun BiometricPreview() {
    GovUkTheme {
        BiometricScreen(
            onPageView = { },
            onSetupBiometrics = { },
            onSkip = { }
        )
    }
}