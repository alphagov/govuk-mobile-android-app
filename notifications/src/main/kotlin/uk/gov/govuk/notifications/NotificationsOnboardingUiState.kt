package uk.gov.govuk.notifications

internal sealed class NotificationsOnboardingUiState {
    internal data object Default : NotificationsOnboardingUiState()
    internal data object Finish : NotificationsOnboardingUiState()
}
