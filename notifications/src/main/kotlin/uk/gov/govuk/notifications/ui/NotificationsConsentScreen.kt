package uk.gov.govuk.notifications.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import uk.gov.govuk.design.ui.component.FixedDoubleButtonGroup
import uk.gov.govuk.notifications.NotificationsViewModel
import uk.gov.govuk.notifications.R

@Composable
internal fun NotificationsConsentRoute(
    notificationsConsentCompleted: () -> Unit,
    launchBrowser: (url: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val notificationsViewModel: NotificationsViewModel = hiltViewModel()

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
                        notificationsConsentCompleted()
                    }
                },
                secondaryText = secondaryText,
                onSecondary = {
                    notificationsViewModel.onTurnOffNotificationsClick(secondaryText)
                    showNotificationsAlert(
                        context,
                        onCancelButtonClick = { notificationsViewModel.onCancelButtonClick(it) },
                        onContinueButtonClick = { notificationsViewModel.onContinueButtonClick(it) })
                }
            )
        }
    )
}
