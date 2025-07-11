package uk.gov.govuk.notifications.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import uk.gov.govuk.design.ui.component.FixedDoubleButtonGroup
import uk.gov.govuk.notifications.NotificationsOnboardingViewModel
import uk.gov.govuk.notifications.NotificationsUiState
import uk.gov.govuk.notifications.NotificationsViewModel
import uk.gov.govuk.notifications.R

@OptIn(ExperimentalPermissionsApi::class)
@Composable
internal fun NotificationsOnboardingRoute(
    notificationsOnboardingCompleted: () -> Unit,
    launchBrowser: (url: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val notificationsViewModel: NotificationsViewModel = hiltViewModel()
    val viewModel: NotificationsOnboardingViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()

    val permissionStatus = getNotificationsPermissionStatus()
    LaunchedEffect(Unit) {
        viewModel.updateUiState(permissionStatus)
    }

    uiState?.let { state ->
        when (state) {
            NotificationsUiState.Default -> {
                NotificationsScreen(
                    onPageView = { notificationsViewModel.onPageView() },
                    body = R.string.onboarding_screen_body,
                    onPrivacyPolicyClick = { text, url ->
                        notificationsViewModel.onPrivacyPolicyClick(text, url)
                        launchBrowser(url)
                    },
                    modifier = modifier,
                    image = R.drawable.notifications_bell,
                    footer = {
                        val primaryText = stringResource(R.string.allow_notifications_button)
                        if (permissionStatus.isGranted) {
                            val context = LocalContext.current
                            val secondaryText = stringResource(R.string.turn_off_notifications_button)
                            FixedDoubleButtonGroup(
                                primaryText = primaryText,
                                onPrimary = { notificationsViewModel.onGiveConsentClick(primaryText) { viewModel.finish() } },
                                secondaryText = secondaryText,
                                onSecondary = {
                                    notificationsViewModel.onTurnOffNotificationsClick(secondaryText)
                                    showNotificationsAlert(
                                        context,
                                        onCancelButtonClick = { notificationsViewModel.onCancelButtonClick(it) },
                                        onContinueButtonClick = { notificationsViewModel.onContinueButtonClick(it) })
                                }
                            )
                        } else {
                            val secondaryText = stringResource(R.string.not_now_button)
                            FixedDoubleButtonGroup(
                                primaryText = primaryText,
                                onPrimary = {
                                    notificationsViewModel.onAllowNotificationsClick(primaryText)
                                    { viewModel.finish() }
                                },
                                secondaryText = secondaryText,
                                onSecondary = {
                                    notificationsViewModel.onNotNowClick(secondaryText)
                                    viewModel.finish()
                                }
                            )
                        }
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
