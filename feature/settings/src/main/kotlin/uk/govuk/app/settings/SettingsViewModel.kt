package uk.govuk.app.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uk.govuk.app.analytics.Analytics
import javax.inject.Inject

internal data class SettingsUiState(
    val isAnalyticsEnabled: Boolean,
)

@HiltViewModel
internal class SettingsViewModel @Inject constructor(
    private val analytics: Analytics
): ViewModel() {

    companion object {
        private const val SCREEN_CLASS = "SettingsScreen"
        private const val SCREEN_NAME = "Settings"
        private const val TITLE = "Settings"
    }

    private val _uiState: MutableStateFlow<SettingsUiState?> = MutableStateFlow(null)
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            // Todo - fetch from data store
            _uiState.value = SettingsUiState(
                isAnalyticsEnabled = true,
            )
        }
    }

    fun onPageView() {
        analytics.screenView(
            screenClass = SCREEN_CLASS,
            screenName = SCREEN_NAME,
            title = TITLE
        )
    }

    fun onAnalyticsConsentChanged(enabled: Boolean) {
        // Todo - persist
        if (enabled) {
            analytics.enable()
        } else {
            analytics.disable()
        }
        _uiState.value = SettingsUiState(
            isAnalyticsEnabled = enabled
        )
    }
}