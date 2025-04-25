package uk.gov.govuk.login

import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uk.gov.govuk.analytics.AnalyticsClient
import uk.gov.govuk.login.data.LoginRepo
import javax.inject.Inject

@HiltViewModel
internal class LoginViewModel @Inject constructor(
    private val loginRepo: LoginRepo,
    private val analyticsClient: AnalyticsClient
) : ViewModel() {

    data class LoginUiState(val isAuthenticationEnabled: Boolean)

    companion object {
        private const val SCREEN_CLASS = "LoginScreen"
        private const val SCREEN_NAME = "Login"
        private const val TITLE = "Login"

        private const val SECTION = "Login"
    }

    private val _uiState: MutableStateFlow<LoginUiState?> = MutableStateFlow(null)
    val uiState = _uiState.asStateFlow()

    val authIntent = loginRepo.authIntent

    fun onPageView() {
        analyticsClient.screenView(
            screenClass = SCREEN_CLASS,
            screenName = SCREEN_NAME,
            title = TITLE
        )
    }

    fun onContinue(text: String) {
        analyticsClient.buttonClick(
            text = text,
            section = SECTION
        )
    }

    fun onAuthResponse(data: Intent?) {
        viewModelScope.launch {
            val result = loginRepo.handleAuthResponse(data)
            if (result) {
                _uiState.value = LoginUiState(loginRepo.isAuthenticationEnabled())
            } else {
                // Todo - handle failure!!!
            }
        }
    }
}