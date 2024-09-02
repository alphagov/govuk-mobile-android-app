package uk.govuk.app.analytics

import uk.gov.logging.api.analytics.AnalyticsEvent
import uk.gov.logging.api.analytics.logging.AnalyticsLogger
import uk.gov.logging.api.analytics.parameters.ButtonParameters
import uk.gov.logging.api.analytics.parameters.ScreenViewParameters
import uk.govuk.app.analytics.search.SearchParameters
import javax.inject.Inject

class AnalyticsClient @Inject constructor(
    private val analyticsLogger: AnalyticsLogger
): Analytics {

    override fun screenView(screenClass: String, alias: String, title: String) {
        analyticsLogger.logEvent(
            true,
            AnalyticsEvent.screenView(
                ScreenViewParameters(
                    clazz = screenClass,
                    name = alias,
                    title = title
                )
            )
        )
    }

    override fun buttonClick(screenName: String, cta: String, action: String) {
        analyticsLogger.logEvent(
            true,
            AnalyticsEvent.trackEvent(
                ButtonParameters(
                    callToActionText = cta,
                    name = screenName,
                    action = action
                )
            )
        )
    }

    override fun search(searchTerm: String) {
        analyticsLogger.logEvent(
            true,
            AnalyticsEvent.trackEvent(
                SearchParameters(searchTerm)
            )
        )
    }
}