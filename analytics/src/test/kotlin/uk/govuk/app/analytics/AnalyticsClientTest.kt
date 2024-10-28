package uk.govuk.app.analytics

import com.google.firebase.analytics.FirebaseAnalytics
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import uk.gov.logging.api.analytics.AnalyticsEvent
import uk.gov.logging.api.analytics.logging.AnalyticsLogger
import uk.govuk.app.analytics.data.AnalyticsRepo
import uk.govuk.app.analytics.data.local.AnalyticsEnabledState.DISABLED
import uk.govuk.app.analytics.data.local.AnalyticsEnabledState.ENABLED
import uk.govuk.app.analytics.data.local.AnalyticsEnabledState.NOT_SET
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
    fun `Given a button click with optional parameters, then log event`() {
        val analyticsClient = AnalyticsClient(analyticsLogger, analyticsRepo)
        analyticsClient.buttonClick(
            text = "text",
            url = "url",
            external = true,
            section = "section"
        )

        verify {
            analyticsLogger.logEvent(
                true,
                AnalyticsEvent(
                    eventType = "Navigation",
                    parameters = mapOf(
                        "type" to "Button",
                        "external" to true,
                        "url" to "url",
                        "section" to "section",
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
    fun `Given a search with postcode, then redact and log event`() {
        val analyticsClient = AnalyticsClient(analyticsLogger, analyticsRepo)
        analyticsClient.search("search term A1 1AA")

        verify {
            analyticsLogger.logEvent(
                true,
                AnalyticsEvent(
                    eventType = "Search",
                    parameters = mapOf(
                        "text" to "search term [postcode]"
                    )
                )
            )
        }
    }

    @Test
    fun `Given a search with email address, then redact and log event`() {
        val analyticsClient = AnalyticsClient(analyticsLogger, analyticsRepo)
        analyticsClient.search("search term test@email.com")

        verify {
            analyticsLogger.logEvent(
                true,
                AnalyticsEvent(
                    eventType = "Search",
                    parameters = mapOf(
                        "text" to "search term [email]"
                    )
                )
            )
        }
    }

    @Test
    fun `Given a search with NI number, then redact and log event`() {
        val analyticsClient = AnalyticsClient(analyticsLogger, analyticsRepo)
        analyticsClient.search("search term AA 00 00 00 A")

        verify {
            analyticsLogger.logEvent(
                true,
                AnalyticsEvent(
                    eventType = "Search",
                    parameters = mapOf(
                        "text" to "search term [NI number]"
                    )
                )
            )
        }
    }

    @Test
    fun `Given a search result click, then log event`() {
        val analyticsClient = AnalyticsClient(analyticsLogger, analyticsRepo)
        analyticsClient.searchResultClick("search result title", "search result link")

        verify {
            analyticsLogger.logEvent(
                true,
                AnalyticsEvent(
                    eventType = "Navigation",
                    parameters = mapOf(
                        "type" to "SearchResult",
                        "external" to true,
                        "language" to Locale.getDefault().language,
                        "text" to "search result title",
                        "url" to "search result link"
                    )
                )
            )
        }
    }

    @Test
    fun `Given a visited item click, then log event`() {
        val analyticsClient = AnalyticsClient(analyticsLogger, analyticsRepo)
        analyticsClient.visitedItemClick("visited item title", "visited item link")

        verify {
            analyticsLogger.logEvent(
                true,
                AnalyticsEvent(
                    eventType = "Navigation",
                    parameters = mapOf(
                        "type" to "VisitedItem",
                        "external" to true,
                        "language" to Locale.getDefault().language,
                        "text" to "visited item title",
                        "url" to "visited item link"
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
    fun `Given a toggle function, then log event`() {
        val analyticsClient = AnalyticsClient(analyticsLogger, analyticsRepo)
        analyticsClient.toggleFunction(
            text = "text",
            section = "section",
            action = "action"
        )

        verify {
            analyticsLogger.logEvent(
                true,
                AnalyticsEvent(
                    eventType = "Function",
                    parameters = mapOf(
                        "type" to "Toggle",
                        "language" to Locale.getDefault().language,
                        "text" to "text",
                        "section" to "section",
                        "action" to "action"
                    )
                )
            )
        }
    }

    @Test
    fun `Given analytics are not set, when is analytics consent required, then return true`() {
        val analyticsClient = AnalyticsClient(analyticsLogger, analyticsRepo)

        coEvery { analyticsRepo.getAnalyticsEnabledState() } returns NOT_SET

        runTest {
            assertTrue(analyticsClient.isAnalyticsConsentRequired())
        }
    }

    @Test
    fun `Given analytics are enabled, when is analytics consent required, then return false`() {
        val analyticsClient = AnalyticsClient(analyticsLogger, analyticsRepo)

        coEvery { analyticsRepo.getAnalyticsEnabledState() } returns ENABLED

        runTest {
            assertFalse(analyticsClient.isAnalyticsConsentRequired())
        }
    }

    @Test
    fun `Given analytics are disabled, when is analytics consent required, then return false`() {
        val analyticsClient = AnalyticsClient(analyticsLogger, analyticsRepo)

        coEvery { analyticsRepo.getAnalyticsEnabledState() } returns DISABLED

        runTest {
            assertFalse(analyticsClient.isAnalyticsConsentRequired())
        }
    }

    @Test
    fun `Given analytics are not set, when is analytics enabled, then return false`() {
        val analyticsClient = AnalyticsClient(analyticsLogger, analyticsRepo)

        coEvery { analyticsRepo.getAnalyticsEnabledState() } returns NOT_SET

        runTest {
            assertFalse(analyticsClient.isAnalyticsEnabled())
        }
    }

    @Test
    fun `Given analytics are enabled, when is analytics enabled, then return true`() {
        val analyticsClient = AnalyticsClient(analyticsLogger, analyticsRepo)

        coEvery { analyticsRepo.getAnalyticsEnabledState() } returns ENABLED

        runTest {
            assertTrue(analyticsClient.isAnalyticsEnabled())
        }
    }

    @Test
    fun `Given analytics are disabled, when is analytics enabled, then return false`() {
        val analyticsClient = AnalyticsClient(analyticsLogger, analyticsRepo)

        coEvery { analyticsRepo.getAnalyticsEnabledState() } returns DISABLED

        runTest {
            assertFalse(analyticsClient.isAnalyticsEnabled())
        }
    }

    @Test
    fun `Given analytics have been enabled, then enable`() {
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
    fun `Given analytics have been disabled, then disable`() {
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
