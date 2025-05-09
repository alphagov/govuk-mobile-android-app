package uk.gov.govuk.settings.ui

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.core.app.NotificationManagerCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleResumeEffect
import uk.gov.govuk.design.ui.component.BodyRegularLabel
import uk.gov.govuk.design.ui.component.CaptionRegularLabel
import uk.gov.govuk.design.ui.component.CardListItem
import uk.gov.govuk.design.ui.component.ExternalLinkListItem
import uk.gov.govuk.design.ui.component.InternalLinkListItem
import uk.gov.govuk.design.ui.component.LargeVerticalSpacer
import uk.gov.govuk.design.ui.component.MediumVerticalSpacer
import uk.gov.govuk.design.ui.component.SmallHorizontalSpacer
import uk.gov.govuk.design.ui.component.SmallVerticalSpacer
import uk.gov.govuk.design.ui.component.SubheadlineRegularLabel
import uk.gov.govuk.design.ui.component.TabHeader
import uk.gov.govuk.design.ui.component.ToggleListItem
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.gov.govuk.notifications.notificationsPermissionShouldShowRationale
import uk.gov.govuk.settings.R
import uk.gov.govuk.settings.SettingsUiState
import uk.gov.govuk.settings.SettingsViewModel

internal class SettingsRouteActions(
    val onAccountClick: () -> Unit,
    val onSignOutClick: () -> Unit,
    val onNotificationsClick: () -> Unit,
    val onPrivacyPolicyClick: () -> Unit,
    val onHelpClick: () -> Unit,
    val onAccessibilityStatementClick: () -> Unit,
    val onOpenSourceLicenseClick: () -> Unit,
    val onTermsAndConditionsClick: () -> Unit
)

@Composable
internal fun SettingsRoute(
    appVersion: String,
    actions: SettingsRouteActions,
    modifier: Modifier = Modifier
) {
    val viewModel: SettingsViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    uiState?.let {
        SettingsScreen(
            uiState = it,
            appVersion = appVersion,
            actions = SettingsActions(
                onPageView = { viewModel.onPageView() },
                onAccountClick = {
                    viewModel.onAccount()
                    actions.onAccountClick()
                },
                onSignOutClick = {
                    viewModel.onSignOut()
                    actions.onSignOutClick()
                },
                onNotificationsClick = {
                    viewModel.onNotificationsClick()
                    if (notificationsPermissionShouldShowRationale(context as Activity)) {
                        actions.onNotificationsClick()
                    } else {
                        showNotificationsAlert(context, viewModel)
                    }
                },
                onAnalyticsConsentChange = { enabled -> viewModel.onAnalyticsConsentChanged(enabled) },
                onPrivacyPolicyClick = {
                    viewModel.onPrivacyPolicyView()
                    actions.onPrivacyPolicyClick()
                },
                onHelpClick = {
                    viewModel.onHelpAndFeedbackView()
                    actions.onHelpClick()
                },
                onAccessibilityStatementClick = {
                    viewModel.onAccessibilityStatementView()
                    actions.onAccessibilityStatementClick()
                },
                onLicenseClick = {
                    viewModel.onLicenseView()
                    actions.onOpenSourceLicenseClick()
                },
                onTermsAndConditionsClick = {
                    viewModel.onTermsAndConditionsView()
                    actions.onTermsAndConditionsClick()
                }
            ),
            modifier = modifier
        )
    }
}

private class SettingsActions(
    val onPageView: () -> Unit,
    val onAccountClick: () -> Unit,
    val onSignOutClick: () -> Unit,
    val onNotificationsClick: () -> Unit,
    val onAnalyticsConsentChange: (Boolean) -> Unit,
    val onPrivacyPolicyClick: () -> Unit,
    val onHelpClick: () -> Unit,
    val onAccessibilityStatementClick: () -> Unit,
    val onLicenseClick: () -> Unit,
    val onTermsAndConditionsClick: () -> Unit,
)

@Composable
private fun SettingsScreen(
    uiState: SettingsUiState,
    appVersion: String,
    actions: SettingsActions,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(Unit) {
        actions.onPageView()
    }

    Column(
        modifier = modifier
    ) {
        TabHeader(stringResource(R.string.screen_title))
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(horizontal = GovUkTheme.spacing.medium)
                .padding(bottom = GovUkTheme.spacing.large)
        ) {
            MediumVerticalSpacer()

            if (uiState.isLoginEnabled) {
                ManageLogin(
                    userEmail = uiState.userEmail,
                    onAccountClick = actions.onAccountClick,
                    onSignOutClick = actions.onSignOutClick
                )

                LargeVerticalSpacer()
            }

            NotificationsAndPrivacy(
                isNotificationsEnabled = uiState.isNotificationsEnabled,
                isAnalyticsEnabled = uiState.isAnalyticsEnabled,
                onNotificationsClick = actions.onNotificationsClick,
                onAnalyticsConsentChange = actions.onAnalyticsConsentChange,
                onPrivacyPolicyClick = actions.onPrivacyPolicyClick
            )

            MediumVerticalSpacer()

            AboutTheApp(
                appVersion = appVersion,
                onHelpClick = actions.onHelpClick
            )

            MediumVerticalSpacer()

            PrivacyPolicy(actions.onPrivacyPolicyClick)
            AccessibilityStatement(actions.onAccessibilityStatementClick)
            OpenSourceLicenses(actions.onLicenseClick)
            TermsAndConditions(actions.onTermsAndConditionsClick)
        }
    }
}

@Composable
private fun ManageLogin(
    userEmail: String,
    onAccountClick: () -> Unit,
    onSignOutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        CardListItem(
            isFirst = true,
            isLast = false
        ) {
            Row(
                modifier = Modifier.padding(GovUkTheme.spacing.medium),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_login),
                    contentDescription = null
                )
                SmallHorizontalSpacer()
                Column {
                    BodyRegularLabel(stringResource(R.string.manage_login_header_title))
                    SubheadlineRegularLabel(
                        text = userEmail,
                        color = GovUkTheme.colourScheme.textAndIcons.secondary
                    )
                }
            }
        }

        ExternalLinkListItem(
            title = stringResource(R.string.manage_login_link),
            onClick = onAccountClick,
            isFirst = false
        )

        SmallVerticalSpacer()

        CaptionRegularLabel(
            text = stringResource(R.string.manage_login_description),
            modifier = Modifier.padding(horizontal = GovUkTheme.spacing.medium)
        )

        MediumVerticalSpacer()

        CardListItem(
            modifier = Modifier.fillMaxWidth(),
            onClick = onSignOutClick
        ) {
            BodyRegularLabel(
                text = stringResource(R.string.manage_login_sign_out),
                modifier = Modifier
                    .padding(GovUkTheme.spacing.medium),
                color = GovUkTheme.colourScheme.textAndIcons.buttonDestructive
            )
        }
    }
}

@Composable
private fun NotificationsAndPrivacy(
    isNotificationsEnabled: Boolean,
    isAnalyticsEnabled: Boolean,
    onNotificationsClick: () -> Unit,
    onAnalyticsConsentChange: (Boolean) -> Unit,
    onPrivacyPolicyClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        if (isNotificationsEnabled) {
            Notifications(
                onNotificationsClick = onNotificationsClick
            )
        }

        ToggleListItem(
            title = stringResource(R.string.share_setting),
            checked = isAnalyticsEnabled,
            onCheckedChange = onAnalyticsConsentChange,
            isFirst = !isNotificationsEnabled
        )

        SmallVerticalSpacer()

        CaptionRegularLabel(
            text = stringResource(R.string.privacy_description),
            modifier = Modifier
                .padding(horizontal = GovUkTheme.spacing.medium)
        )

        val altText = "${stringResource(R.string.privacy_read_more)} " +
                stringResource(id = R.string.link_opens_in)

        CaptionRegularLabel(
            text = stringResource(R.string.privacy_read_more),
            modifier = Modifier
                .semantics {
                    contentDescription = altText
                }
                .padding(horizontal = GovUkTheme.spacing.medium)
                .clickable(onClick = onPrivacyPolicyClick),
            color = GovUkTheme.colourScheme.textAndIcons.link,
        )
    }
}

@Composable
private fun Notifications(
    onNotificationsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    fun getStatus() = if (NotificationManagerCompat.from(context)
            .areNotificationsEnabled()
    ) R.string.on_button else R.string.off_button

    var status by remember { mutableIntStateOf(getStatus()) }

    LifecycleResumeEffect(Unit) {
        status = getStatus()
        onPauseOrDispose {
            // Do nothing
        }
    }

    InternalLinkListItem(
        title = stringResource(R.string.notifications_title),
        status = stringResource(status),
        onClick = onNotificationsClick,
        modifier = modifier,
        isFirst = true,
        isLast = false,
    )
}

@Composable
private fun AboutTheApp(
    appVersion: String,
    onHelpClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        CardListItem(
            isFirst = true,
            isLast = false
        ) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(GovUkTheme.spacing.medium),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BodyRegularLabel(
                    text = stringResource(R.string.version_setting),
                    modifier = Modifier.weight(1f)
                )

                BodyRegularLabel(text = appVersion)
            }
        }

        ExternalLinkListItem(
            title = stringResource(R.string.help_and_feedback_title),
            onClick = onHelpClick,
            isFirst = false,
            isLast = true
        )
    }
}

@Composable
private fun PrivacyPolicy(
    onPrivacyPolicyClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ExternalLinkListItem(
        title = stringResource(R.string.privacy_policy_title),
        onClick = onPrivacyPolicyClick,
        modifier = modifier,
        isFirst = true,
        isLast = false,
    )
}

@Composable
private fun AccessibilityStatement(
    onAccessibilityStatementClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ExternalLinkListItem(
        title = stringResource(R.string.accessibility_title),
        onClick = onAccessibilityStatementClick,
        modifier = modifier,
        isFirst = false,
        isLast = false,
    )
}

@Composable
private fun OpenSourceLicenses(
    onLicenseClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    InternalLinkListItem(
        title = stringResource(R.string.oss_licenses_title),
        onClick = onLicenseClick,
        modifier = modifier,
        isFirst = false,
        isLast = false,
    )
}

@Composable
private fun TermsAndConditions(
    onLicenseClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ExternalLinkListItem(
        title = stringResource(R.string.terms_and_conditions_title),
        onClick = onLicenseClick,
        modifier = modifier,
        isFirst = false,
        isLast = true,
    )
}

private fun showNotificationsAlert(context: Context, viewModel: SettingsViewModel) {
    val isNotificationsOn = NotificationManagerCompat.from(context).areNotificationsEnabled()
    val alertTitle =
        if (isNotificationsOn) R.string.notifications_alert_title_off else R.string.notifications_alert_title_on
    val alertMessage =
        if (isNotificationsOn) R.string.notifications_alert_message_off else R.string.notifications_alert_message_on
    val neutralButton = context.getString(R.string.cancel_button)
    val positiveButton = context.getString(R.string.continue_button)

    AlertDialog.Builder(context).apply {
        setTitle(context.getString(alertTitle))
        setMessage(context.getString(alertMessage))
        setNeutralButton(neutralButton) { dialog, _ ->
            viewModel.onButtonClick(neutralButton)
            dialog.dismiss()
        }
        setPositiveButton(positiveButton) { dialog, _ ->
            viewModel.onButtonClick(positiveButton)
            openDeviceSettings(context)
            dialog.dismiss()
        }
    }.also { notificationsAlert ->
        notificationsAlert.show()
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
