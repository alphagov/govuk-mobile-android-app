package uk.govuk.app

internal sealed class AppUiState {
    internal class Default(
        val shouldDisplayRecommendUpdate: Boolean,
        val shouldDisplayAnalyticsConsent: Boolean,
        val shouldDisplayOnboarding: Boolean,
        val shouldDisplayTopicSelection: Boolean,
        val isNotificationsEnabled: Boolean,
        val isSearchEnabled: Boolean,
        val isRecentActivityEnabled: Boolean,
        val isTopicsEnabled: Boolean
    ) : AppUiState()

    internal data object Loading : AppUiState()

    internal data object AppUnavailable : AppUiState()

    internal data object DeviceOffline : AppUiState()

    internal data object ForcedUpdate : AppUiState()
}
