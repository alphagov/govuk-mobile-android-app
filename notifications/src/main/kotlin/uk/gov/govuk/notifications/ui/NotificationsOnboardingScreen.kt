package uk.gov.govuk.notifications.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.window.core.layout.WindowHeightSizeClass
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import uk.gov.govuk.design.ui.component.ChildPageHeader
import uk.gov.govuk.design.ui.component.HorizontalButtonGroup
import uk.gov.govuk.design.ui.component.ListDivider
import uk.gov.govuk.design.ui.component.OnboardingSlide
import uk.gov.govuk.design.ui.component.PrimaryButton
import uk.gov.govuk.design.ui.component.VerticalButtonGroup
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.gov.govuk.notifications.NotificationsOnboardingUiState
import uk.gov.govuk.notifications.NotificationsOnboardingViewModel
import uk.gov.govuk.notifications.R

@OptIn(ExperimentalPermissionsApi::class)
@Composable
internal fun NotificationsOnboardingRoute(
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
                    onPageView = { viewModel.onPageView() },
                    modifier = modifier,
                    footer = {
                        OnboardingScreenFooter(
                            onContinue = {
                                viewModel.onContinueClick(it)
                            },
                            onSkip = {
                                viewModel.onSkipClick(it)
                                notificationsOnboardingCompleted()
                            }
                        )
                    }
                )
            }

            NotificationsOnboardingUiState.Finish -> {
                EmptyScreen()
                notificationsOnboardingCompleted()
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
internal fun NotificationsOnboardingNoSkipRoute(
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
                    onPageView = { viewModel.onPageView() },
                    modifier = modifier,
                    header = {
                        ChildPageHeader(
                            onBack = notificationsOnboardingCompleted
                        )
                    },
                    footer = {
                        OnboardingScreenFooterNoSkip(
                            onContinue = {
                                viewModel.onContinueClick(it)
                            }
                        )
                    }
                )
            }

            NotificationsOnboardingUiState.Finish -> {
                EmptyScreen()
                notificationsOnboardingCompleted()
            }
        }
    }
}

@Composable
private fun EmptyScreen() {
    Box(
        Modifier
            .fillMaxSize()
            .background(Color.Transparent)
    )
}

@Composable
private fun OnboardingScreen(
    onPageView: () -> Unit,
    modifier: Modifier = Modifier,
    header: (@Composable () -> Unit)? = null,
    footer: @Composable () -> Unit
) {
    LaunchedEffect(Unit) {
        onPageView()
    }

    Column(modifier.fillMaxWidth()) {
        header?.invoke()

        OnboardingSlide(
            title = R.string.onboarding_screen_title,
            body = R.string.onboarding_screen_body,
            animation = R.raw.bell
        )

        Spacer(modifier = Modifier.weight(1f))

        ListDivider()

        footer()
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

        val allowButtonText = stringResource(R.string.allow_button)
        val notNowButtonText = stringResource(R.string.not_now_button)

        if (windowSizeClass.windowHeightSizeClass == WindowHeightSizeClass.COMPACT) {
            HorizontalButtonGroup(
                primaryText = allowButtonText,
                onPrimary = { onContinue(allowButtonText) },
                secondaryText = notNowButtonText,
                onSecondary = { onSkip(notNowButtonText) }
            )
        } else {
            VerticalButtonGroup(
                primaryText = allowButtonText,
                onPrimary = { onContinue(allowButtonText) },
                secondaryText = notNowButtonText,
                onSecondary = { onSkip(notNowButtonText) }
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
            .padding(vertical = GovUkTheme.spacing.medium)
            .padding(horizontal = GovUkTheme.spacing.small),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        val allowButtonText = stringResource(R.string.allow_button)

        PrimaryButton(
            text = allowButtonText,
            onClick = { onContinue(allowButtonText) },
            modifier = modifier
        )
    }
}

@Preview
@Composable
private fun OnboardingScreenPreview() {
    OnboardingScreen({}, footer = { OnboardingScreenFooter({}, {}) })
}

@Preview
@Composable
private fun OnboardingScreenNoSkipPreview() {
    OnboardingScreen(
        {},
        header = { ChildPageHeader(onBack = {}) },
        footer = { OnboardingScreenFooterNoSkip({}) })
}
