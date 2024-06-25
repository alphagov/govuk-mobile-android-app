package uk.govuk.app.onboarding

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class OnboardingViewModel @Inject constructor(
    private val onboardingRepo: OnboardingRepo
) : ViewModel() {

    init {
        viewModelScope.launch {
            val isOnboardingCompleted = onboardingRepo.isOnboardingCompleted()
            Log.d("Blah", "$isOnboardingCompleted")
        }
    }

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