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
internal class BiometricViewModel @Inject constructor(
    private val secureStore: SecureStore,
    private val loginRepo: LoginRepo,
    private val analyticsClient: AnalyticsClient
) : ViewModel() {

    companion object {
        private const val SCREEN_CLASS = "BiometricScreen"
        private const val SCREEN_NAME = "Biometrics"
        private const val TITLE = "Biometrics"

        private const val SECTION = "Biometrics"
    }

    private val _uiState: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val uiState = _uiState.asStateFlow()

    fun onPageView() {
        analyticsClient.screenView(
            screenClass = SCREEN_CLASS,
            screenName = SCREEN_NAME,
            title = TITLE
        )
    }

    fun onContinue(activity: FragmentActivity, text: String) {
        analyticsClient.buttonClick(
            text = text,
            section = SECTION
        )

        viewModelScope.launch {
            secureStore.upsert("refreshToken", loginRepo.tokens.refreshToken)
            val result = secureStore.retrieveWithAuthentication(
                key = arrayOf("refreshToken"),
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
                secureStore.delete("refreshToken")
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