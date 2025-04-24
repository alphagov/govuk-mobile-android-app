package uk.gov.govuk.login

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uk.gov.android.securestore.RetrievalEvent
import uk.gov.android.securestore.SecureStore
import uk.gov.android.securestore.authentication.AuthenticatorPromptConfiguration
import uk.gov.govuk.analytics.AnalyticsClient
import uk.gov.govuk.login.data.LoginRepo
import javax.inject.Inject

@HiltViewModel
internal class LoginViewModel @Inject constructor(
    private val secureStore: SecureStore,
    private val loginRepo: LoginRepo,
    private val analyticsClient: AnalyticsClient
) : ViewModel() {

    companion object {
        private const val BIOMETRICS_SCREEN_CLASS = "BiometricScreen"
        private const val BIOMETRICS_SCREEN_NAME = "Biometrics"
        private const val BIOMETRICS_TITLE = "Biometrics"

        private const val SECTION = "Login"
    }

    private val _uiState: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val uiState = _uiState.asStateFlow()

    fun onBiometricPageView() {
        analyticsClient.screenView(
            screenClass = BIOMETRICS_SCREEN_CLASS,
            screenName = BIOMETRICS_SCREEN_NAME,
            title = BIOMETRICS_TITLE
        )
    }

    fun onSetupBiometrics(activity: FragmentActivity, text: String) {
        analyticsClient.buttonClick(
            text = text,
            section = SECTION
        )

        viewModelScope.launch {
            secureStore.upsert("token", loginRepo.token)
            val result = secureStore.retrieveWithAuthentication(
                key = arrayOf("token"),
                authPromptConfig = AuthenticatorPromptConfiguration(
                    title = "Title",
                    subTitle = "Subtitle",
                    description = "Description"
                ),
                context = activity
            )
            if (result is RetrievalEvent.Success) {
                _uiState.value = true
            } else {
                secureStore.delete("token")
            }
        }
    }

    fun onSkip(text: String) {
        analyticsClient.buttonClick(
            text = text,
            section = SECTION
        )
    }
}