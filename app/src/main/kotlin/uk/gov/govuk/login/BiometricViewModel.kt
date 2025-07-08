package uk.gov.govuk.login

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uk.gov.govuk.R
import uk.gov.govuk.data.AppRepo
import uk.gov.govuk.data.auth.AuthRepo
import javax.inject.Inject

@HiltViewModel
internal class BiometricViewModel @Inject constructor(
    private val authRepo: AuthRepo,
    private val appRepo: AppRepo
) : ViewModel() {

    private val _uiState: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val uiState = _uiState.asStateFlow()

    fun onContinue(activity: FragmentActivity) {
        viewModelScope.launch {
            _uiState.value = authRepo.persistRefreshToken(
                activity = activity,
                title = activity.getString(R.string.login_biometric_prompt_title)
            )
        }
    }

    fun onSkip() {
        viewModelScope.launch {
            appRepo.skipBiometrics()
        }
    }
}
