package uk.govuk.app.notifications

internal sealed class NotificationsPermissionUiState {
    internal data object OptIn : NotificationsPermissionUiState()
    internal data object Finish : NotificationsPermissionUiState()
}
