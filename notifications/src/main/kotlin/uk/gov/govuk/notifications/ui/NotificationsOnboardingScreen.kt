package uk.gov.govuk.notifications.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import uk.gov.govuk.design.ui.component.FixedDoubleButtonGroup
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

    val permissionStatus = getNotificationsPermissionStatus()

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
                    onPrimary = { notificationsViewModel.onGiveConsentClick(primaryText) { notificationsOnboardingCompleted() } },
                    secondaryText = secondaryText,
                    onSecondary = {
                        notificationsViewModel.onTurnOffNotificationsClick(secondaryText)
                        showNotificationsAlert(
                            context,
                            onCancelButtonClick = { notificationsViewModel.onCancelButtonClick(it) },
                            onContinueButtonClick = {
                                notificationsViewModel.onContinueButtonClick(it)
                                notificationsOnboardingCompleted()
                            })
                    }
                )
            } else {
                val secondaryText = stringResource(R.string.not_now_button)
                FixedDoubleButtonGroup(
                    primaryText = primaryText,
                    onPrimary = {
                        notificationsViewModel.onAllowNotificationsClick(primaryText)
                        { notificationsOnboardingCompleted() }
                    },
                    secondaryText = secondaryText,
                    onSecondary = {
                        notificationsViewModel.onNotNowClick(secondaryText)
                        notificationsOnboardingCompleted()
                    }
                )
            }
        }
    )
}
