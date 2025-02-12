package uk.govuk.app.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uk.govuk.app.analytics.AnalyticsClient
import uk.govuk.app.settings.BuildConfig.ACCESSIBILITY_STATEMENT_EVENT
import uk.govuk.app.settings.BuildConfig.ACCESSIBILITY_STATEMENT_URL
import uk.govuk.app.settings.BuildConfig.HELP_AND_FEEDBACK_EVENT
import uk.govuk.app.settings.BuildConfig.HELP_AND_FEEDBACK_URL
import uk.govuk.app.settings.BuildConfig.OPEN_SOURCE_LICENCE_EVENT
import uk.govuk.app.settings.BuildConfig.PRIVACY_POLICY_EVENT
import uk.govuk.app.settings.BuildConfig.PRIVACY_POLICY_URL
import uk.govuk.app.settings.BuildConfig.TERMS_AND_CONDITIONS_EVENT
import uk.govuk.app.settings.BuildConfig.TERMS_AND_CONDITIONS_URL
import javax.inject.Inject

internal data class SettingsUiState(
    val isAnalyticsEnabled: Boolean,
)

@HiltViewModel
internal class SettingsViewModel @Inject constructor(
    private val analyticsClient: AnalyticsClient
): ViewModel() {

    companion object {
        private const val SCREEN_CLASS = "SettingsScreen"
        private const val SCREEN_NAME = "Settings"
        private const val TITLE = "Settings"
    }

    private val _uiState: MutableStateFlow<SettingsUiState?> = MutableStateFlow(null)
    val uiState = _uiState.asStateFlow()

    init {
        _uiState.value = SettingsUiState(
            isAnalyticsEnabled = analyticsClient.isAnalyticsEnabled(),
        )
    }

    fun onPageView() {
        analyticsClient.screenView(
            screenClass = SCREEN_CLASS,
            screenName = SCREEN_NAME,
            title = TITLE
        )
    }

    fun onLicenseView() {
        analyticsClient.screenView(
            screenClass = OPEN_SOURCE_LICENCE_EVENT,
            screenName = OPEN_SOURCE_LICENCE_EVENT,
            title = OPEN_SOURCE_LICENCE_EVENT
        )
    }

    fun onHelpAndFeedbackView() {
        analyticsClient.settingsItemClick(
            text = HELP_AND_FEEDBACK_EVENT,
            url = HELP_AND_FEEDBACK_URL
        )
    }

    fun onPrivacyPolicyView() {
        analyticsClient.settingsItemClick(
            text = PRIVACY_POLICY_EVENT,
            url = PRIVACY_POLICY_URL
        )
    }

    fun onAccessibilityStatementView() {
        analyticsClient.settingsItemClick(
            text = ACCESSIBILITY_STATEMENT_EVENT,
            url = ACCESSIBILITY_STATEMENT_URL
        )
    }

    fun onTermsAndConditionsView() {
        analyticsClient.settingsItemClick(
            text = TERMS_AND_CONDITIONS_EVENT,
            url = TERMS_AND_CONDITIONS_URL
        )
    }

    fun onAnalyticsConsentChanged(enabled: Boolean) {
        viewModelScope.launch {
            if (enabled) {
                analyticsClient.enable()
            } else {
                analyticsClient.disable()
            }
            _uiState.value = SettingsUiState(
                isAnalyticsEnabled = enabled
            )
        }
    }
}
