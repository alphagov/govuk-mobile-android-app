package uk.govuk.app.notifications.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.window.core.layout.WindowHeightSizeClass
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import uk.govuk.app.design.ui.component.HorizontalButtonGroup
import uk.govuk.app.design.ui.component.ListDivider
import uk.govuk.app.design.ui.component.OnboardingPage
import uk.govuk.app.design.ui.component.PrimaryButton
import uk.govuk.app.design.ui.component.VerticalButtonGroup
import uk.govuk.app.design.ui.theme.GovUkTheme
import uk.govuk.app.notifications.NotificationsOnboardingUiState
import uk.govuk.app.notifications.NotificationsOnboardingViewModel
import uk.govuk.app.notifications.R

@OptIn(ExperimentalPermissionsApi::class)
@Composable
internal fun NotificationsOnboardingRoute(
    canSkip: Boolean,
    notificationsOnboardingCompleted: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: NotificationsOnboardingViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()

    val permissionStatus = getNotificationsPermissionStatus()
    LaunchedEffect(permissionStatus) {
        viewModel.updateUiState(permissionStatus)
    }

    uiState?.let { state ->
        when (state) {
            NotificationsOnboardingUiState.Default -> {
                OnboardingScreen(
                    canSkip = canSkip,
                    onContinue = {
                        viewModel.onContinueClick(it)
                        notificationsOnboardingCompleted()
                    },
                    onSkip = {
                        viewModel.onSkipClick(it)
                        notificationsOnboardingCompleted()
                    },
                    onPageView = { viewModel.onPageView() },
                    modifier = modifier
                )
            }

            NotificationsOnboardingUiState.Finish -> {
                notificationsOnboardingCompleted()
            }
        }
    }
}

@Composable
private fun OnboardingScreen(
    canSkip: Boolean,
    onContinue: (String) -> Unit,
    onSkip: ((String) -> Unit),
    onPageView: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LaunchedEffect(Unit) {
        onPageView()
    }

    Column(modifier.fillMaxWidth()) {
        OnboardingPage(
            title = R.string.onboarding_screen_title,
            body = R.string.onboarding_screen_body,
            animation = R.raw.bell
        )

        Spacer(modifier = Modifier.weight(1f))

        ListDivider()

        if (canSkip) {
            OnboardingScreenFooter(
                onContinue = onContinue,
                onSkip = onSkip
            )
        } else {
            OnboardingScreenFooterNoSkip(
                onContinue = onContinue
            )
        }
    }
}

@Composable
private fun OnboardingScreenFooter(
    onContinue: (String) -> Unit,
    onSkip: ((String) -> Unit),
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .padding(top = GovUkTheme.spacing.medium, bottom = GovUkTheme.spacing.small)
            .padding(horizontal = GovUkTheme.spacing.small),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass

        val continueButtonText = stringResource(R.string.continue_button)
        val skipButtonText = stringResource(R.string.skip_button)

            if (windowSizeClass.windowHeightSizeClass == WindowHeightSizeClass.COMPACT) {
                HorizontalButtonGroup(
                    primaryText = continueButtonText,
                    onPrimary = { onContinue(continueButtonText) },
                    secondaryText = skipButtonText,
                    onSecondary = { onSkip(skipButtonText) }
                )
            } else {
                VerticalButtonGroup(
                    primaryText = continueButtonText,
                    onPrimary = { onContinue(continueButtonText) },
                    secondaryText = skipButtonText,
                    onSecondary = { onSkip(skipButtonText) }
                )
            }
    }
}

@Composable
private fun OnboardingScreenFooterNoSkip(
    onContinue: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(top = GovUkTheme.spacing.medium, bottom = GovUkTheme.spacing.small)
            .padding(horizontal = GovUkTheme.spacing.small),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        val continueButtonText = stringResource(R.string.continue_button)

        PrimaryButton(
            text = continueButtonText,
            onClick = { onContinue(continueButtonText) },
            modifier = modifier
        )
    }
}

@Preview
@Composable
private fun OnboardingScreenPreview() {
    OnboardingScreen(true, {}, {}, {})
}

@Preview
@Composable
private fun OnboardingScreenNoSkipPreview() {
    OnboardingScreen(false, {}, {}, {})
}
