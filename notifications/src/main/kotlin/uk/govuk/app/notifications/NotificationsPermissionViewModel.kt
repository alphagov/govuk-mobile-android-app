package uk.govuk.app.notifications

import androidx.lifecycle.ViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.shouldShowRationale
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import uk.govuk.app.analytics.AnalyticsClient
import javax.inject.Inject

@OptIn(ExperimentalPermissionsApi::class)
@HiltViewModel
internal class NotificationsPermissionViewModel @Inject constructor(
    private val analyticsClient: AnalyticsClient,
    private val notificationsClient: NotificationsClient
) : ViewModel() {

    companion object {
        private const val SCREEN_CLASS = "NotificationsPermissionScreen"
        private const val TITLE = "NotificationsPermissionScreen"
    }

    private val _uiState: MutableStateFlow<NotificationsPermissionUiState?> = MutableStateFlow(null)
    val uiState = _uiState.asStateFlow()

    internal fun updatePermission(status: PermissionStatus) {
        if (status.isGranted) {
            _uiState.value = NotificationsPermissionUiState.Finish
        } else if (status.shouldShowRationale) {
            _uiState.value = NotificationsPermissionUiState.Default
        } else {
            notificationsClient.requestPermission()
            _uiState.value = NotificationsPermissionUiState.Finish
        }
    }

    internal fun onPageView() {
        analyticsClient.screenView(
            screenClass = SCREEN_CLASS,
            screenName = TITLE,
            title = TITLE
        )
    }

    internal fun onContinueClick(text: String) {
        notificationsClient.requestPermission()
        analyticsClient.buttonClick(
            text = text
        )
    }

    internal fun onSkipClick(text: String) {
        analyticsClient.buttonClick(
            text = text
        )
    }
}
