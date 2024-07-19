package uk.govuk.app.analytics

import io.mockk.mockk
import io.mockk.verify
import org.junit.Test
import uk.gov.logging.api.analytics.AnalyticsEvent
import uk.gov.logging.api.analytics.logging.AnalyticsLogger
import uk.gov.logging.api.analytics.parameters.ButtonParameters
import uk.gov.logging.api.analytics.parameters.ScreenViewParameters

class AnalyticsClientTest {

    private val analyticsLogger = mockk<AnalyticsLogger>(relaxed = true)

    @Test
    fun `Given an onboarding screen view, then log event`() {
        val analyticsClient = uk.govuk.app.analytics.AnalyticsClient(analyticsLogger)
        analyticsClient.onboardingScreenView(
            screenClass = "screenClass",
            alias = "alias",
            title = "title"
        )

        verify {
            analyticsLogger.logEvent(
                true,
                AnalyticsEvent.screenView(
                    ScreenViewParameters(
                        clazz = "screenClass",
                        name = "alias",
                        title = "title"
                    )
                )
            )
        }
    }

    @Test
    fun `Given an onboarding button click, then log event`() {
        val analyticsClient = uk.govuk.app.analytics.AnalyticsClient(analyticsLogger)
        analyticsClient.onboardingButtonClick(
            screenName = "screenName",
            cta = "cta",
            action = "action"
        )

        verify {
            analyticsLogger.logEvent(
                true,
                AnalyticsEvent.trackEvent(
                    ButtonParameters(
                        callToActionText = "cta",
                        name = "screenName",
                        action = "action"
                    )
                )
            )
        }
    }
}