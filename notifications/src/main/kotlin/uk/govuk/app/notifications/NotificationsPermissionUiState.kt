package uk.govuk.app.notifications

internal sealed class NotificationsPermissionUiState {
    internal data object Default : NotificationsPermissionUiState()
    internal data object Finish : NotificationsPermissionUiState()
}
