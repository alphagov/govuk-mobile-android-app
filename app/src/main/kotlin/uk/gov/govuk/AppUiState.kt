package uk.gov.govuk

internal sealed class AppUiState {
    internal data class Default(
        val shouldDisplayRecommendUpdate: Boolean = false,
        val shouldDisplayAnalyticsConsent: Boolean = false,
        val shouldDisplayOnboarding: Boolean = false,
        val shouldDisplayTopicSelection: Boolean = false,
        val shouldDisplayNotificationsOnboarding: Boolean = false,
        val storedValue: String = ""
    ) : AppUiState()

    internal data object Loading : AppUiState()

    internal data object AppUnavailable : AppUiState()

    internal data object DeviceOffline : AppUiState()

    internal data object ForcedUpdate : AppUiState()
}
