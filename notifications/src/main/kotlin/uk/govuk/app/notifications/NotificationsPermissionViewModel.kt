package uk.govuk.app.notifications

import android.os.Build
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
    internal val uiState = _uiState.asStateFlow()

    internal fun updateUiState(status: PermissionStatus) {
        _uiState.value = if (status.isGranted) {
            notificationsClient.giveConsent()
            NotificationsPermissionUiState.Finish
        } else if (status.shouldShowRationale) {
            NotificationsPermissionUiState.OptIn
        } else {
            notificationsClient.requestPermission()
            NotificationsPermissionUiState.Finish
        }
    }

    internal fun permissionRequired(androidVersion: Int = Build.VERSION.SDK_INT) =
        androidVersion >= Build.VERSION_CODES.TIRAMISU

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
