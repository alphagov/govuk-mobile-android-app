package uk.govuk.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class AppLaunchState {
    LOADING,
    ONBOARDING_REQUIRED,
    ONBOARDING_COMPLETED
}

@HiltViewModel
class AppLaunchViewModel @Inject constructor(): ViewModel() {

    private val _appLaunchState: MutableStateFlow<AppLaunchState> = MutableStateFlow(AppLaunchState.LOADING)
    val appLaunchState = _appLaunchState.asStateFlow()

    init {
        viewModelScope.launch {
            delay(3000L)
            _appLaunchState.value = AppLaunchState.ONBOARDING_COMPLETED
        }
    }

}