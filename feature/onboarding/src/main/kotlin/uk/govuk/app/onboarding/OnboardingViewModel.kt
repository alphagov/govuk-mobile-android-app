package uk.govuk.app.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class OnboardingViewModel @Inject constructor(
    private val onboardingRepo: OnboardingRepo
) : ViewModel() {

    internal fun onDone() {
        onboardingCompleted()
    }

    internal fun onSkip() {
        onboardingCompleted()
    }

    private fun onboardingCompleted() {
        viewModelScope.launch {
            onboardingRepo.onboardingCompleted()
        }
    }
}