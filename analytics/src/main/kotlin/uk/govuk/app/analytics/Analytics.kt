package uk.govuk.app.analytics

interface Analytics {

    fun onboardingScreenView(screenClass: String, alias: String, title: String)
    fun onboardingButtonClick(screenName: String, cta: String, action: String)

}