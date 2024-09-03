package uk.govuk.app.analytics

import com.google.firebase.analytics.FirebaseAnalytics
import uk.gov.logging.api.analytics.AnalyticsEvent
import uk.gov.logging.api.analytics.logging.AnalyticsLogger
import uk.gov.logging.api.analytics.parameters.ButtonParameters
import uk.govuk.app.analytics.search.SearchParameters
import java.util.Locale
import javax.inject.Inject

class AnalyticsClient @Inject constructor(
    private val analyticsLogger: AnalyticsLogger
): Analytics {

    companion object {
        private const val WIDGET_ACTION = "Widget"
    }

    override fun screenView(screenClass: String, screenName: String, title: String) {
        analyticsLogger.logEvent(
            true,
            AnalyticsEvent(
                eventType = FirebaseAnalytics.Event.SCREEN_VIEW,
                parameters = mapOf(
                    FirebaseAnalytics.Param.SCREEN_CLASS to screenClass,
                    FirebaseAnalytics.Param.SCREEN_NAME to screenName,
                    "screen_title" to title,
                    "language" to Locale.getDefault().language
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

    override fun widgetClick(screenName: String, cta: String) {
        buttonClick(
            screenName = screenName,
            cta = cta,
            action = WIDGET_ACTION
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