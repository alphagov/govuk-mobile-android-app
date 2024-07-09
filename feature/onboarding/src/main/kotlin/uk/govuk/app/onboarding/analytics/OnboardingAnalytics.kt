package uk.govuk.app.onboarding.analytics

interface OnboardingAnalytics {

    fun onboardingScreenView(screenClass: String, alias: String, title: String)
    fun onboardingButtonClick(screenName: String, cta: String, action: String)

}