package uk.govuk.app.analytics

import android.util.Log
import uk.govuk.app.onboarding.analytics.OnboardingAnalytics

class AnalyticsClient: OnboardingAnalytics {

    override fun onboardingScreenView(screenClass: String, alias: String, title: String) {
        Log.d("Blah", "Onboarding screen view")
    }

    override fun onboardingButtonClick(screenName: String, cta: String, action: String) {
        Log.d("Blah", "Onboarding button click")
    }
}