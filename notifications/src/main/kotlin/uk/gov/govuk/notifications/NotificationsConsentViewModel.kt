package uk.gov.govuk.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.isGranted
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uk.gov.govuk.notifications.data.local.NotificationsDataStore
import javax.inject.Inject

@HiltViewModel
internal class NotificationsConsentViewModel @Inject constructor(
    private val notificationsClient: NotificationsClient,
    private val notificationsDataStore: NotificationsDataStore
) : ViewModel() {

    private val _uiState: MutableStateFlow<NotificationsUiState?> = MutableStateFlow(null)
    internal val uiState = _uiState.asStateFlow()

    @OptIn(ExperimentalPermissionsApi::class)
    internal fun updateUiState(
        status: PermissionStatus
    ) {
        viewModelScope.launch {
            _uiState.value = if (!status.isGranted) {
                notificationsClient.removeConsent()
                NotificationsUiState.Finish
            } else if (!notificationsDataStore.isOnboardingCompleted()) {
                NotificationsUiState.Finish
            } else if (notificationsClient.consentGiven()) {
                NotificationsUiState.Finish
            } else {
                NotificationsUiState.Default
            }
        }
    }

    internal fun finish() {
        _uiState.value = NotificationsUiState.Finish
    }
}
