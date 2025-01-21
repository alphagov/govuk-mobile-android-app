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

    fun screenView(screenClass: String, screenName: String, title: String) {
        firebaseAnalyticsClient.logEvent(
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

    fun pageIndicatorClick() {
        navigation(type = "Dot")
    }

    fun buttonClick(
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

    fun tabClick(text: String) {
        navigation(text = text, type = "Tab")
    }

    fun widgetClick(
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

    fun search(searchTerm: String) {
        redactedEvent(name = "Search", inputString = searchTerm)
    }

    fun autocomplete(searchTerm: String) {
        redactedEvent(name = "Autocomplete", inputString = searchTerm)
    }

    fun searchResultClick(text: String, url: String) {
        // external as these links will be opened in the device browser
        navigation(text = text, type = "SearchResult", url = url, external = true)
    }

    fun visitedItemClick(text: String, url: String) {
        // external as these links will be opened in the device browser
        navigation(text = text, type = "VisitedItem", url = url, external = true)
    }

    fun settingsItemClick(text: String, url: String) {
        // external as these links will be opened in the device browser
        navigation(text = text, type = "SettingsItem", url = url, external = true)
    }

    fun toggleFunction(text: String, section: String, action: String) {
        function(
            text = text,
            type = "Toggle",
            section = section,
            action = action
        )
    }

    fun buttonFunction(
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

    private fun redactedEvent(name: String, inputString: String) {
        firebaseAnalyticsClient.logEvent(
            name,
            mapOf(
                "text" to inputString.redactPii()
            )
        )
    }

    private fun navigation(
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

        firebaseAnalyticsClient.logEvent("Navigation", parametersWithLanguage(parameters))
    }

    private fun function(text: String, type: String, section: String, action: String) {
        val parameters = mapOf(
            "text" to text,
            "type" to type,
            "section" to section,
            "action" to action
        )

        firebaseAnalyticsClient.logEvent("Function", parametersWithLanguage(parameters))
    }

    private fun parametersWithLanguage(parameters: Map<String, Any>): Map<String, Any> {
        return parameters + Pair("language", Locale.getDefault().language)
    }
}
