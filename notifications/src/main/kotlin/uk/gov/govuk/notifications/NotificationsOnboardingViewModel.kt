package uk.gov.govuk.notifications

import android.os.Build
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
internal class NotificationsOnboardingViewModel @Inject constructor(
    private val notificationsDataStore: NotificationsDataStore
) : ViewModel() {

    private val _uiState: MutableStateFlow<NotificationsUiState?> = MutableStateFlow(null)
    internal val uiState = _uiState.asStateFlow()

    @OptIn(ExperimentalPermissionsApi::class)
    internal fun updateUiState(
        status: PermissionStatus,
        androidVersion: Int = Build.VERSION.SDK_INT
    ) {
        viewModelScope.launch {
            val androidVersionIsGreaterThanTwelve = androidVersion >= Build.VERSION_CODES.TIRAMISU
            _uiState.value = if (androidVersionIsGreaterThanTwelve) {
                if (notificationsDataStore.isOnboardingCompleted()) {
                    NotificationsUiState.Finish
                } else {
                    NotificationsUiState.Default
                }
            } else {
                if (status.isGranted && !notificationsDataStore.isOnboardingCompleted()) {
                    NotificationsUiState.Default
                } else {
                    NotificationsUiState.Finish
                }
            }
        }
    }

    internal fun finish() {
        _uiState.value = NotificationsUiState.Finish
    }
}
