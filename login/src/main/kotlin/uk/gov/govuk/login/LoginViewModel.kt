package uk.gov.govuk.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uk.gov.govuk.analytics.AnalyticsClient
import javax.inject.Inject

internal data class LoginUiState(
    var loginResponse: String = "",
    var accessToken: String = "",
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val analyticsClient: AnalyticsClient,
): ViewModel() {

    companion object {
        private const val LOGIN_SCREEN_CLASS = "LoginScreen"
        private const val LOGIN_SCREEN_NAME = "Login"
        private const val LOGIN_TITLE = "Login"
    }

    private val _uiState: MutableStateFlow<LoginUiState?> = MutableStateFlow(
        LoginUiState()
    )
    internal val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _uiState.value = LoginUiState()
        }
    }

    fun onPageView() {
        analyticsClient.screenView(
            screenClass = LOGIN_SCREEN_CLASS,
            screenName = LOGIN_SCREEN_NAME,
            title = LOGIN_TITLE
        )
    }

    fun onContinueClick(title: String) {
        analyticsClient.screenView(
            screenClass = LOGIN_SCREEN_CLASS,
            screenName = LOGIN_SCREEN_NAME,
            title = title
        )
    }
}
