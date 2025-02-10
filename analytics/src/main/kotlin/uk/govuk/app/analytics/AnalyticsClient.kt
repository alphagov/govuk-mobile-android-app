package uk.govuk.app.analytics

import com.google.firebase.analytics.FirebaseAnalytics
import uk.govuk.app.analytics.data.AnalyticsRepo
import uk.govuk.app.analytics.data.local.AnalyticsEnabledState
import uk.govuk.app.analytics.extension.redactPii
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AnalyticsClient @Inject constructor(
    private val analyticsRepo: AnalyticsRepo,
    private val firebaseAnalyticsClient: FirebaseAnalyticsClient
) {

    suspend fun isAnalyticsConsentRequired(): Boolean {
        return analyticsRepo.getAnalyticsEnabledState() == AnalyticsEnabledState.NOT_SET
    }

    suspend fun isAnalyticsEnabled(): Boolean {
        return analyticsRepo.getAnalyticsEnabledState() == AnalyticsEnabledState.ENABLED
    }

    suspend fun enable() {
        analyticsRepo.analyticsEnabled()
        firebaseAnalyticsClient.enable()
    }

    suspend fun disable() {
        analyticsRepo.analyticsDisabled()
        firebaseAnalyticsClient.disable()
    }

    suspend fun screenView(screenClass: String, screenName: String, title: String) {
        logEvent(
            FirebaseAnalytics.Event.SCREEN_VIEW,
            parametersWithLanguage(
                mapOf(
                    FirebaseAnalytics.Param.SCREEN_CLASS to screenClass,
                    FirebaseAnalytics.Param.SCREEN_NAME to screenName,
                    "screen_title" to title,
                )
            )
        )
    }

    suspend fun pageIndicatorClick() {
        navigation(type = "Dot")
    }

    suspend fun buttonClick(
        text: String,
        url: String? = null,
        external: Boolean = false,
        section: String? = null
    ) {
        navigation(
            text = text,
            type = "Button",
            url = url,
            external = external,
            section = section
        )
    }

    suspend fun tabClick(text: String) {
        navigation(text = text, type = "Tab")
    }

    suspend fun widgetClick(
        text: String,
        external: Boolean,
        section: String
    ) {
        navigation(
            text = text,
            type = "Widget",
            external = external,
            section = section
        )
    }

    suspend fun search(searchTerm: String) {
        redactedEvent(name = "Search", type = "typed", inputString = searchTerm)
    }

    suspend fun autocomplete(searchTerm: String) {
        redactedEvent(name = "Search", type = "autocomplete", inputString = searchTerm)
    }

    suspend fun history(searchTerm: String) {
        redactedEvent(name = "Search", type = "history", inputString = searchTerm)
    }

    suspend fun searchResultClick(text: String, url: String) {
        // external as these links will be opened in the device browser
        navigation(text = text, type = "SearchResult", url = url, external = true)
    }

    suspend fun visitedItemClick(text: String, url: String) {
        // external as these links will be opened in the device browser
        navigation(text = text, type = "VisitedItem", url = url, external = true)
    }

    suspend fun settingsItemClick(text: String, url: String) {
        // external as these links will be opened in the device browser
        navigation(text = text, type = "SettingsItem", url = url, external = true)
    }

    suspend fun toggleFunction(text: String, section: String, action: String) {
        function(
            text = text,
            type = "Toggle",
            section = section,
            action = action
        )
    }

    suspend fun buttonFunction(
        text: String,
        section: String,
        action: String
    ) {
        function(
            text = text,
            type = "Button",
            section = section,
            action = action
        )
    }

    fun topicsCustomised() {
        firebaseAnalyticsClient.setUserProperty("topics_customised", "true")
    }

    private suspend fun redactedEvent(name: String, type: String, inputString: String) {
        logEvent(
            name,
            mapOf(
                "type" to type,
                "text" to inputString.redactPii()
            )
        )
    }

    private suspend fun navigation(
        text: String? = null,
        type: String,
        url: String? = null,
        external: Boolean = false,
        section: String? = null
    ) {
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

        section?.let {
            parameters["section"] = it
        }

        logEvent("Navigation", parametersWithLanguage(parameters))
    }

    private suspend fun function(text: String, type: String, section: String, action: String) {
        val parameters = mapOf(
            "text" to text,
            "type" to type,
            "section" to section,
            "action" to action
        )

        logEvent("Function", parametersWithLanguage(parameters))
    }

    private fun parametersWithLanguage(parameters: Map<String, Any>): Map<String, Any> {
        return parameters + Pair("language", Locale.getDefault().language)
    }

    private suspend fun logEvent(name: String, parameters: Map<String, Any>) {
        if (analyticsRepo.getAnalyticsEnabledState() == AnalyticsEnabledState.ENABLED) {
            firebaseAnalyticsClient.logEvent(name, parameters)
        }
    }
}
