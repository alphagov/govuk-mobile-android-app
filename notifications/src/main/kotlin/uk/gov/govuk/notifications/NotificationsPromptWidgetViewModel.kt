package uk.gov.govuk.notifications

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class NotificationsPromptWidgetViewModel @Inject constructor(
    private val notificationsClient: NotificationsClient
) : ViewModel() {

    internal fun onClick() {
        notificationsClient.requestPermission()
    }
}
