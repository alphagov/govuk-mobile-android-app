package uk.gov.govuk.onboarding

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import uk.gov.govuk.analytics.AnalyticsClient
import javax.inject.Inject

@HiltViewModel
internal class OnboardingViewModel @Inject constructor(
    private val analyticsClient: AnalyticsClient
): ViewModel() {

    companion object {
        private const val ONBOARDING_SCREEN_CLASS = "OnboardingScreen"
        private const val ONBOARDING_SCREEN_NAME = "Onboarding Page"
        private const val ONBOARDING_TITLE = "Onboarding Page"
    }

    fun onPageView() {
        analyticsClient.screenView(
            screenClass = ONBOARDING_SCREEN_CLASS,
            screenName = ONBOARDING_SCREEN_NAME,
            title = ONBOARDING_TITLE
        )
    }

    fun onButtonClick(text: String) {
        analyticsClient.buttonClick(text)
    }
}
