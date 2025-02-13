package uk.govuk.app.notifications

import android.Manifest
import android.os.Build
import androidx.compose.runtime.Composable
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun notificationsPermissionShouldShowRationale(): Boolean =
    getNotificationsPermissionStatus().shouldShowRationale

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun getNotificationsPermissionStatus(): PermissionStatus =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        rememberPermissionState(Manifest.permission.POST_NOTIFICATIONS).status
    } else {
        PermissionStatus.Granted
    }
