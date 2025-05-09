package uk.gov.govuk.notifications.ui

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import uk.gov.govuk.design.ui.component.ChildPageHeader
import uk.gov.govuk.design.ui.component.FixedDoubleButtonGroup
import uk.gov.govuk.design.ui.component.FixedPrimaryButton
import uk.gov.govuk.design.ui.component.OnboardingSlide
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
                    body = R.string.onboarding_screen_body,
                    onPrivacyPolicyClick = { text, url ->
                        viewModel.onPrivacyPolicyClick(text, url)
                    },
                    image = R.drawable.notifications_bell,
                    footer = {
                        val primaryText = stringResource(R.string.allow_notifications_button)
                        val secondaryText = stringResource(R.string.not_now_button)
                        FixedDoubleButtonGroup(
                            primaryText = primaryText,
                            onPrimary = { viewModel.onContinueClick(primaryText) },
                            secondaryText = secondaryText,
                            onSecondary = {
                                viewModel.onSkipClick(secondaryText)
                                notificationsOnboardingCompleted()
                            }
                        )
                    }
                )
            }

            NotificationsOnboardingUiState.NoConsent -> {
                OnboardingScreen(
                    onPageView = { viewModel.onPageView() },
                    body = R.string.onboarding_screen_no_consent_body,
                    onPrivacyPolicyClick = { text, url ->
                        viewModel.onPrivacyPolicyClick(text, url)
                    },
                    modifier = modifier,
                    footer = {
                        val context = LocalContext.current
                        val primaryText = stringResource(R.string.allow_notifications_button)
                        val secondaryText = stringResource(R.string.turn_off_notifications_button)
                        FixedDoubleButtonGroup(
                            primaryText = primaryText,
                            onPrimary = { viewModel.onGiveConsentClick(primaryText) },
                            secondaryText = secondaryText,
                            onSecondary = {
                                viewModel.onTurnOffNotificationsClick(secondaryText)
                                openDeviceSettings(context)
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
internal fun NotificationsOnboardingFromSettingsRoute(
    notificationsOnboardingCompleted: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: NotificationsOnboardingViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()

    val permissionStatus = getNotificationsPermissionStatus()
    LaunchedEffect(permissionStatus) {
        viewModel.updateUiState(permissionStatus, fromSettings = true)
    }

    uiState?.let { state ->
        when (state) {
            NotificationsOnboardingUiState.Default -> {
                OnboardingScreen(
                    onPageView = { viewModel.onPageView() },
                    body = R.string.onboarding_screen_body,
                    onPrivacyPolicyClick = { text, url ->
                        viewModel.onPrivacyPolicyClick(text, url)
                    },
                    modifier = modifier,
                    header = {
                        ChildPageHeader(
                            onBack = notificationsOnboardingCompleted
                        )
                    },
                    footer = {
                        val primaryText = stringResource(R.string.allow_notifications_button)
                        val secondaryText = stringResource(R.string.not_now_button)
                        FixedDoubleButtonGroup(
                            primaryText = primaryText,
                            onPrimary = { viewModel.onContinueClick(primaryText) },
                            secondaryText = secondaryText,
                            onSecondary = {
                                viewModel.onSkipClick(secondaryText)
                                notificationsOnboardingCompleted()
                            }
                        )
                    }
                )
            }

            else -> {
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
    @StringRes body: Int,
    onPrivacyPolicyClick: (text: String, url: String) -> Unit,
    modifier: Modifier = Modifier,
    @DrawableRes image: Int? = null,
    header: (@Composable () -> Unit)? = null,
    footer: @Composable () -> Unit
) {
    LaunchedEffect(Unit) {
        onPageView()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = GovUkTheme.spacing.medium),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        header?.invoke() ?: run {
            image ?: Spacer(Modifier)
        }

        OnboardingSlide(
            title = R.string.onboarding_screen_title,
            body = body,
            image = image,
            onPrivacyPolicyClick = onPrivacyPolicyClick,
            modifier = Modifier.weight(1f, fill = false)
        )
        footer()
    }
}

private fun openDeviceSettings(context: Context) {
    Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        .putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
        .also { intent ->
            context.startActivity(intent)
        }
}

@Preview(showBackground = true)
@Composable
private fun OnboardingScreenOneButtonPreview() {
    GovUkTheme {
        OnboardingScreen(
            {},
            body = R.string.onboarding_screen_no_consent_body,
            onPrivacyPolicyClick = { _, _ -> },
            header = { ChildPageHeader(onBack = {}) },
            footer = { FixedPrimaryButton("Primary button", {}) })
    }
}

@Preview(showBackground = true)
@Composable
private fun OnboardingScreenTwoButtonsPreview() {
    GovUkTheme {
        OnboardingScreen(
            {},
            body = R.string.onboarding_screen_body,
            onPrivacyPolicyClick = { _, _ -> },
            image = R.drawable.ic_bell,
            header = { ChildPageHeader(onBack = {}) },
            footer = {
                FixedDoubleButtonGroup("Primary button", {}, "Secondary button", {})
            })
    }
}
