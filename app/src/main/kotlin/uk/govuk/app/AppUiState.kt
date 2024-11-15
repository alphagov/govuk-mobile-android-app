package uk.govuk.app

internal sealed class AppUiState(
    val shouldDisplayAppUnavailable: Boolean = false,
    val shouldDisplayForcedUpdate: Boolean = false,
    val shouldDisplayRecommendUpdate: Boolean = false,
    val shouldDisplayAnalyticsConsent: Boolean = false,
    val shouldDisplayOnboarding: Boolean = false,
    val shouldDisplayTopicSelection: Boolean = false,
    val isSearchEnabled: Boolean = false,
    val isRecentActivityEnabled: Boolean = false,
    val isTopicsEnabled: Boolean = false
) {
    internal class Default(
        shouldDisplayRecommendUpdate: Boolean,
        shouldDisplayAnalyticsConsent: Boolean,
        shouldDisplayOnboarding: Boolean,
        shouldDisplayTopicSelection: Boolean,
        isSearchEnabled: Boolean,
        isRecentActivityEnabled: Boolean,
        isTopicsEnabled: Boolean
    ) : AppUiState(
        shouldDisplayRecommendUpdate = shouldDisplayRecommendUpdate,
        shouldDisplayAnalyticsConsent = shouldDisplayAnalyticsConsent,
        shouldDisplayOnboarding = shouldDisplayOnboarding,
        shouldDisplayTopicSelection = shouldDisplayTopicSelection,
        isSearchEnabled = isSearchEnabled,
        isRecentActivityEnabled = isRecentActivityEnabled,
        isTopicsEnabled = isTopicsEnabled
    )

    internal data object AppUnavailable : AppUiState(
        shouldDisplayAppUnavailable = true
    )

    internal data object ForcedUpdate : AppUiState(
        shouldDisplayForcedUpdate = true
    )
}
