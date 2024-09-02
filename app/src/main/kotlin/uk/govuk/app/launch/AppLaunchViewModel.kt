package uk.govuk.app.launch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uk.govuk.app.analytics.Analytics
import uk.govuk.app.config.flags.ReleaseFlagsService
import javax.inject.Inject

data class AppLaunchUiState(
    val isOnboardingRequired: Boolean,
    val isSearchEnabled: Boolean
)

@HiltViewModel
internal class AppLaunchViewModel @Inject constructor(
    private val appLaunchRepo: AppLaunchRepo,
    private val releaseFlagsService: ReleaseFlagsService,
    private val analytics: Analytics
): ViewModel() {

    private val _uiState: MutableStateFlow<AppLaunchUiState?> = MutableStateFlow(null)
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _uiState.value = AppLaunchUiState(
                isOnboardingRequired = !appLaunchRepo.isOnboardingCompleted(),
                isSearchEnabled = releaseFlagsService.isSearchEnabled()
            )
        }
    }

    internal fun onboardingCompleted() {
        viewModelScope.launch {
            appLaunchRepo.onboardingCompleted()
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
}
