package uk.govuk.app.notifications

internal sealed class NotificationsOnboardingUiState {
    internal data object Default : NotificationsOnboardingUiState()
    internal data object Finish : NotificationsOnboardingUiState()
}
