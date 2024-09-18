package uk.govuk.app.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class AnalyticsViewModel @Inject constructor(
    private val analytics: Analytics
): ViewModel() {

    fun onConsentGranted() {
        viewModelScope.launch {
            analytics.enable()
        }
    }

    fun onConsentDenied() {
        viewModelScope.launch {
            analytics.disable()
        }
    }
}