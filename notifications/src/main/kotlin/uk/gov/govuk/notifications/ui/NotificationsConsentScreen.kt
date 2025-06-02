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
import uk.gov.govuk.design.ui.component.FixedDoubleButtonGroup
import uk.gov.govuk.notifications.NotificationsConsentViewModel
import uk.gov.govuk.notifications.NotificationsUiState
import uk.gov.govuk.notifications.NotificationsViewModel
import uk.gov.govuk.notifications.R
import uk.gov.govuk.notifications.openDeviceSettings

@OptIn(ExperimentalPermissionsApi::class)
@Composable
internal fun NotificationsConsentRoute(
    notificationsConsentCompleted: () -> Unit,
    launchBrowser: (url: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val notificationsViewModel: NotificationsViewModel = hiltViewModel()
    val viewModel: NotificationsConsentViewModel = hiltViewModel()
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
                    body = R.string.onboarding_screen_no_consent_body,
                    onPrivacyPolicyClick = { text, url ->
                        notificationsViewModel.onPrivacyPolicyClick(text, url)
                        launchBrowser(url)
                    },
                    modifier = modifier,
                    footer = {
                        val context = LocalContext.current
                        val primaryText = stringResource(R.string.allow_notifications_button)
                        val secondaryText = stringResource(R.string.turn_off_notifications_button)
                        FixedDoubleButtonGroup(
                            primaryText = primaryText,
                            onPrimary = {
                                notificationsViewModel.onGiveConsentClick(primaryText) {
                                    viewModel.finish()
                                }
                            },
                            secondaryText = secondaryText,
                            onSecondary = {
                                notificationsViewModel.onTurnOffNotificationsClick(secondaryText)
                                openDeviceSettings(context)
                            }
                        )
                    }
                )
            }

            else -> {
                EmptyScreen()
                notificationsConsentCompleted()
            }
        }
    }
}
