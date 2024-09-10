package uk.govuk.app.analytics

import com.google.firebase.analytics.FirebaseAnalytics
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Test
import uk.gov.logging.api.analytics.AnalyticsEvent
import uk.gov.logging.api.analytics.logging.AnalyticsLogger
import java.util.Locale

class AnalyticsClientTest {

    private val analyticsLogger = mockk<AnalyticsLogger>(relaxed = true)
    private val analyticsRepo = mockk<AnalyticsRepo>(relaxed = true)

    @Test
    fun `Given a screen view, then log event`() {
        val analyticsClient = AnalyticsClient(analyticsLogger, analyticsRepo)
        analyticsClient.screenView(
            screenClass = "screenClass",
            screenName = "screenName",
            title = "title"
        )

        verify {
            analyticsLogger.logEvent(
                true,
                AnalyticsEvent(
                    eventType = FirebaseAnalytics.Event.SCREEN_VIEW,
                    parameters = mapOf(
                        FirebaseAnalytics.Param.SCREEN_CLASS to "screenClass",
                        FirebaseAnalytics.Param.SCREEN_NAME to "screenName",
                        "screen_title" to "title",
                        "language" to Locale.getDefault().language
                    )
                )
            )
        }
    }

    @Test
    fun `Given a page indicator click, then log event`() {
        val analyticsClient = AnalyticsClient(analyticsLogger, analyticsRepo)
        analyticsClient.pageIndicatorClick()

        verify {
            analyticsLogger.logEvent(
                true,
                AnalyticsEvent(
                    eventType = "Navigation",
                    parameters = mapOf(
                        "type" to "Dot",
                        "external" to false,
                        "language" to Locale.getDefault().language,
                    )
                )
            )
        }
    }

    @Test
    fun `Given a button click, then log event`() {
        val analyticsClient = AnalyticsClient(analyticsLogger, analyticsRepo)
        analyticsClient.buttonClick("text")

        verify {
            analyticsLogger.logEvent(
                true,
                AnalyticsEvent(
                    eventType = "Navigation",
                    parameters = mapOf(
                        "type" to "Button",
                        "external" to false,
                        "language" to Locale.getDefault().language,
                        "text" to "text"
                    )
                )
            )
        }
    }

    @Test
    fun `Given a search, then log event`() {
        val analyticsClient = AnalyticsClient(analyticsLogger, analyticsRepo)
        analyticsClient.search("search term")

        verify {
            analyticsLogger.logEvent(
                true,
                AnalyticsEvent(
                    eventType = "Search",
                    parameters = mapOf(
                        "text" to "search term"
                    )
                )
            )
        }
    }

    @Test
    fun `Given a tab click, then log event`() {
        val analyticsClient = AnalyticsClient(analyticsLogger, analyticsRepo)
        analyticsClient.tabClick("text")

        verify {
            analyticsLogger.logEvent(
                true,
                AnalyticsEvent(
                    eventType = "Navigation",
                    parameters = mapOf(
                        "type" to "Tab",
                        "external" to false,
                        "language" to Locale.getDefault().language,
                        "text" to "text"
                    )
                )
            )
        }
    }

    @Test
    fun `Given a widget click, then log event`() {
        val analyticsClient = AnalyticsClient(analyticsLogger, analyticsRepo)
        analyticsClient.widgetClick("text")

        verify {
            analyticsLogger.logEvent(
                true,
                AnalyticsEvent(
                    eventType = "Navigation",
                    parameters = mapOf(
                        "type" to "Widget",
                        "external" to false,
                        "language" to Locale.getDefault().language,
                        "text" to "text"
                    )
                )
            )
        }
    }

    @Test
    fun `Given analytics are enabled, then enable`() {
        val analyticsClient = AnalyticsClient(analyticsLogger, analyticsRepo)

        runTest {
            analyticsClient.enable()

            coVerify {
                analyticsRepo.analyticsEnabled()
                analyticsLogger.setEnabled(true)
            }
        }
    }

    @Test
    fun `Given analytics are disabled, then disable`() {
        val analyticsClient = AnalyticsClient(analyticsLogger, analyticsRepo)

        runTest {
            analyticsClient.disable()

            coVerify {
                analyticsRepo.analyticsDisabled()
                analyticsLogger.setEnabled(false)
            }
        }
    }
    
}