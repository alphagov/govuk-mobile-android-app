package uk.govuk.app.analytics

import com.google.firebase.analytics.FirebaseAnalytics
import uk.gov.logging.api.analytics.AnalyticsEvent
import uk.gov.logging.api.analytics.logging.AnalyticsLogger
import uk.govuk.app.analytics.data.AnalyticsRepo
import uk.govuk.app.analytics.data.local.AnalyticsEnabledState
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

    override fun searchResultClick(text: String, url: String) {
        // external as these links will be opened in the device browser
        navigation(text = text, type = "SearchResult", url = url, external = true)
    }

    private fun navigation(text: String? = null, type: String, url: String? = null, external: Boolean = false) {
        val parameters = mutableMapOf(
            "type" to type,
            "external" to external,
        )

        text?.let {
            parameters["text"] = it
        }

        url?.let {
            parameters["url"] = it
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
