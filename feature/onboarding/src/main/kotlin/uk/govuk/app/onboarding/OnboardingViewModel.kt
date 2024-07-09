package uk.govuk.app.onboarding

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import uk.govuk.app.onboarding.analytics.OnboardingAnalytics
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val onboardingAnalytics: OnboardingAnalytics
): ViewModel() {

    fun onPageView(index: Int) {
        onboardingAnalytics.onboardingScreenView(
            screenClass = "OnboardingScreen",
            alias = "",
            title = ""
        )
    }

    fun onContinue() {
        onboardingAnalytics.onboardingButtonClick(
            screenName = "",
            cta = "",
            action = ""
        )
    }

    fun onSkip() {
        onboardingAnalytics.onboardingButtonClick(
            screenName = "",
            cta = "",
            action = ""
        )
    }

    fun onDone() {
        onboardingAnalytics.onboardingButtonClick(
            screenName = "",
            cta = "",
            action = ""
        )
    }

}