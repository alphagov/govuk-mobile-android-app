package uk.gov.govuk.login

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uk.gov.govuk.R
import uk.gov.govuk.analytics.AnalyticsClient
import uk.gov.govuk.data.auth.AuthRepo
import javax.inject.Inject

@HiltViewModel
internal class BiometricSettingsViewModel @Inject constructor(
    private val authRepo: AuthRepo,
    private val analyticsClient: AnalyticsClient
) : ViewModel() {

    companion object {
        private const val SCREEN_CLASS = "BiometricSettingsScreen"
        private const val SCREEN_NAME = "Biometric Settings"
        private const val TITLE = "Biometric Settings"
    }

    private val _uiState: MutableStateFlow<Boolean> = MutableStateFlow(authRepo.isUserSignedIn())
    val uiState = _uiState.asStateFlow()

    fun onPageView() {
        analyticsClient.screenView(
            screenClass = SCREEN_CLASS,
            screenName = SCREEN_NAME,
            title = TITLE
        )
    }

    fun onToggle(activity: FragmentActivity) {
        viewModelScope.launch {
            if (authRepo.isUserSignedIn()) {
                authRepo.signOut()
            } else {
                authRepo.persistRefreshToken(
                    activity = activity,
                    title = activity.getString(R.string.login_biometric_prompt_title)
                )
            }

            _uiState.value = authRepo.isUserSignedIn()
        }
    }
}