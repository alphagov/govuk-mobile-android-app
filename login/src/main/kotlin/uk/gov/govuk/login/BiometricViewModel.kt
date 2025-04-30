package uk.gov.govuk.login

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uk.gov.govuk.analytics.AnalyticsClient
import uk.gov.govuk.data.auth.AuthRepo
import javax.inject.Inject

@HiltViewModel
internal class BiometricViewModel @Inject constructor(
    private val authRepo: AuthRepo,
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
            _uiState.value = authRepo.persistRefreshToken(
                // Todo - actual copy!!!
                activity = activity,
                title = "Title",
                subtitle = "Subtitle",
                description = "Description"
            )
        }
    }

    fun onSkip(text: String) {
        analyticsClient.buttonClick(
            text = text,
            section = SECTION
        )
    }
}