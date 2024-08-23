package uk.govuk.app.launch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uk.govuk.app.release_flag.ReleaseFlagsService
import javax.inject.Inject

enum class AppLaunchState {
    ONBOARDING_REQUIRED,
    ONBOARDING_COMPLETED
}

@HiltViewModel
internal class AppLaunchViewModel @Inject constructor(
    private val appLaunchRepo: AppLaunchRepo
): ViewModel() {

    private val _appLaunchState: MutableStateFlow<AppLaunchState?> = MutableStateFlow(null)
    val appLaunchState = _appLaunchState.asStateFlow()

    private val _isSearchEnabled: MutableStateFlow<Boolean?> = MutableStateFlow(null)
    val isSearchEnabled = _isSearchEnabled.asStateFlow()

    init {
        viewModelScope.launch {
            _appLaunchState.value = if (appLaunchRepo.isOnboardingCompleted()) {
                AppLaunchState.ONBOARDING_COMPLETED
            } else {
                AppLaunchState.ONBOARDING_REQUIRED
            }
            _isSearchEnabled.value = ReleaseFlagsService().isSearchEnabled()
        }
    }

    internal fun onboardingCompleted() {
        viewModelScope.launch {
            appLaunchRepo.onboardingCompleted()
        }
    }
}
