package uk.govuk.app.analytics

import com.google.firebase.analytics.FirebaseAnalytics
import uk.gov.logging.api.analytics.AnalyticsEvent
import uk.gov.logging.api.analytics.logging.AnalyticsLogger
import uk.govuk.app.analytics.extension.redactPii
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AnalyticsClient @Inject constructor(
    private val analyticsLogger: AnalyticsLogger,
    private val analyticsRepo: AnalyticsRepo
): Analytics {

    override suspend fun isAnalyticsConsentRequired(): Boolean {
        return analyticsRepo.getAnalyticsEnabledState() == AnalyticsEnabledState.NOT_SET
    }

    override suspend fun isAnalyticsEnabled(): Boolean {
        return analyticsRepo.getAnalyticsEnabledState() == AnalyticsEnabledState.ENABLED
    }

    override suspend fun enable() {
        analyticsRepo.analyticsEnabled()
        analyticsLogger.setEnabled(true)
    }

    override suspend fun disable() {
        analyticsRepo.analyticsDisabled()
        analyticsLogger.setEnabled(false)
    }

    override fun screenView(screenClass: String, screenName: String, title: String) {
        log(
            AnalyticsEvent(
                eventType = FirebaseAnalytics.Event.SCREEN_VIEW,
                parameters = parametersWithLanguage(
                    mapOf(
                        FirebaseAnalytics.Param.SCREEN_CLASS to screenClass,
                        FirebaseAnalytics.Param.SCREEN_NAME to screenName,
                        "screen_title" to title,
                    )
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

    override fun widgetClick(text: String) {
        navigation(text = text, type = "Widget")
    }

    override fun search(searchTerm: String) {
        log(
            AnalyticsEvent(
                eventType = "Search",
                parameters = mapOf(
                    "text" to searchTerm.redactPii()
                )
            )
        )
    }

    private fun navigation(text: String? = null, type: String) {
        val parameters = mutableMapOf(
            "type" to type,
            "external" to false, // Todo - in the future will need to pass this in if navigate outside of the app
        )

        text?.let {
            parameters["text"] = it
        }

        log(
            AnalyticsEvent(
                eventType = "Navigation",
                parameters = parametersWithLanguage(parameters)
            )
        )
    }

    private fun parametersWithLanguage(parameters: Map<String, Any>): Map<String, Any> {
        return parameters + Pair("language", Locale.getDefault().language)
    }

    private fun log(event: AnalyticsEvent) {
        analyticsLogger.logEvent(true, event)
    }
}