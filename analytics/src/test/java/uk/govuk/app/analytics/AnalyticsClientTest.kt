package uk.govuk.app.analytics

import io.mockk.mockk
import io.mockk.verify
import org.junit.Test
import uk.gov.logging.api.analytics.AnalyticsEvent
import uk.gov.logging.api.analytics.logging.AnalyticsLogger
import uk.gov.logging.api.analytics.parameters.ButtonParameters
import uk.gov.logging.api.analytics.parameters.ScreenViewParameters
import uk.govuk.app.analytics.search.SearchParameters

class AnalyticsClientTest {

    private val analyticsLogger = mockk<AnalyticsLogger>(relaxed = true)

    @Test
    fun `Given a screen view, then log event`() {
        val analyticsClient = AnalyticsClient(analyticsLogger)
        analyticsClient.screenView(
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
    fun `Given a button click, then log event`() {
        val analyticsClient = AnalyticsClient(analyticsLogger)
        analyticsClient.buttonClick(
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

    @Test
    fun `Given a search, then log event`() {
        val analyticsClient = AnalyticsClient(analyticsLogger)
        analyticsClient.search("search term")

        verify {
            analyticsLogger.logEvent(
                true,
                AnalyticsEvent.trackEvent(
                    SearchParameters("search term")
                )
            )
        }
    }
}