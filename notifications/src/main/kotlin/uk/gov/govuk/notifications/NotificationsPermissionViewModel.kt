package uk.gov.govuk.notifications

import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.shouldShowRationale
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uk.gov.govuk.analytics.AnalyticsClient
import uk.gov.govuk.notifications.data.local.NotificationsDataStore
import javax.inject.Inject

@HiltViewModel
internal class NotificationsPermissionViewModel @Inject constructor(
    private val analyticsClient: AnalyticsClient,
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
                if (!status.isGranted &&
                    (!notificationsDataStore.isFirstPermissionRequestCompleted() ||
                            status.shouldShowRationale)
                ) {
                    NotificationsUiState.Default
                } else {
                    NotificationsUiState.Alert
                }
            } else {
                NotificationsUiState.Alert
            }
        }
    }

    internal fun onAlertButtonClick(text: String) {
        analyticsClient.buttonClick(text)
    }
}
