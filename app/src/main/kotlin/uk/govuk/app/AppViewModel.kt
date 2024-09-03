package uk.govuk.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uk.govuk.app.analytics.Analytics
import uk.govuk.app.config.flags.ReleaseFlagsService
import javax.inject.Inject

data class AppUiState(
    val isOnboardingRequired: Boolean,
    val isSearchEnabled: Boolean
)

@HiltViewModel
internal class AppViewModel @Inject constructor(
    private val appRepo: AppRepo,
    private val releaseFlagsService: ReleaseFlagsService,
    private val analytics: Analytics
): ViewModel() {

    private val _uiState: MutableStateFlow<AppUiState?> = MutableStateFlow(null)
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _uiState.value = AppUiState(
                isOnboardingRequired = !appRepo.isOnboardingCompleted(),
                isSearchEnabled = releaseFlagsService.isSearchEnabled()
            )
        }
    }

    internal fun onboardingCompleted() {
        viewModelScope.launch {
            appRepo.onboardingCompleted()
        }
    }

    internal fun onWidgetClick(
        screenName: String,
        cta: String
    ) {
        analytics.widgetClick(
            screenName = screenName,
            cta = cta
        )
    }

    internal fun onTabClick(text: String) {
        analytics.tabClick(text)
    }
}
