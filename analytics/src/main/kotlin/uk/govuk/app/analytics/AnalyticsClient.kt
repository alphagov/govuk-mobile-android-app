package uk.govuk.app.analytics

import com.google.firebase.analytics.FirebaseAnalytics
import uk.gov.logging.api.analytics.AnalyticsEvent
import uk.gov.logging.api.analytics.logging.AnalyticsLogger
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

    override fun pageIndicatorClick() {
        navigation(type = "Dot")
    }

    override fun buttonClick(text: String) {
        navigation(text = text, type = "Button")
    }

    override fun tabClick(text: String) {
        navigation(text = text, type = "Tab")
    }

    private fun navigation(text: String? = null, type: String) {
        val parameters = mutableMapOf(
            "type" to type,
            "external" to false, // Todo - in the future will need to pass this in if navigate outside of the app
            "language" to Locale.getDefault().language
        )

        text?.let {
            parameters["text"] = it
        }

        analyticsLogger.logEvent(
            true,
            AnalyticsEvent(
                eventType = "Navigation",
                parameters = parameters
            )
        )
    }

    override fun widgetClick(text: String) {
        navigation(text = text, type = "Widget")
    }

    override fun search(searchTerm: String) {
        analyticsLogger.logEvent(
            true,
            AnalyticsEvent(
                eventType = "Search",
                parameters = mapOf(
                    "text" to searchTerm
                )
            )
        )
    }
}