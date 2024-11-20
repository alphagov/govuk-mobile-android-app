package uk.govuk.app.analytics

import android.os.Bundle
import androidx.core.os.bundleOf
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import uk.govuk.app.analytics.data.AnalyticsRepo
import uk.govuk.app.analytics.data.local.AnalyticsEnabledState
import uk.govuk.app.analytics.extension.redactPii
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AnalyticsClient @Inject constructor(
    private val analyticsRepo: AnalyticsRepo,
    private val firebaseAnalytics: FirebaseAnalytics,
    private val firebaseCrashlytics: FirebaseCrashlytics
): Analytics {

    override suspend fun isAnalyticsConsentRequired(): Boolean {
        return analyticsRepo.getAnalyticsEnabledState() == AnalyticsEnabledState.NOT_SET
    }

    override suspend fun isAnalyticsEnabled(): Boolean {
        return analyticsRepo.getAnalyticsEnabledState() == AnalyticsEnabledState.ENABLED
    }

    override suspend fun enable() {
        analyticsRepo.analyticsEnabled()
        firebaseAnalytics.setAnalyticsCollectionEnabled(true)
        firebaseCrashlytics.setCrashlyticsCollectionEnabled(true)
    }

    override suspend fun disable() {
        analyticsRepo.analyticsDisabled()
        firebaseAnalytics.setAnalyticsCollectionEnabled(false)
        firebaseCrashlytics.setCrashlyticsCollectionEnabled(false)
    }

    override fun screenView(screenClass: String, screenName: String, title: String) {
        firebaseAnalytics.logEvent(
            FirebaseAnalytics.Event.SCREEN_VIEW,
            parametersWithLanguage(
                bundleOf(
                    FirebaseAnalytics.Param.SCREEN_CLASS to screenClass,
                    FirebaseAnalytics.Param.SCREEN_NAME to screenName,
                    "screen_title" to title,
                )
            )
        )
    }

    override fun pageIndicatorClick() {
        navigation(type = "Dot")
    }

    override fun buttonClick(
        text: String,
        url: String?,
        external: Boolean,
        section: String?
    ) {
        navigation(
            text = text,
            type = "Button",
            url = url,
            external = external,
            section = section
        )
    }

    override fun tabClick(text: String) {
        navigation(text = text, type = "Tab")
    }

    override fun widgetClick(text: String) {
        navigation(text = text, type = "Widget")
    }

    override fun search(searchTerm: String) {
        firebaseAnalytics.logEvent(
            "Search",
            parametersWithLanguage(
                bundleOf(
                    "text" to searchTerm.redactPii()
                )
            )
        )
    }

    override fun searchResultClick(text: String, url: String) {
        // external as these links will be opened in the device browser
        navigation(text = text, type = "SearchResult", url = url, external = true)
    }

    override fun visitedItemClick(text: String, url: String) {
        // external as these links will be opened in the device browser
        navigation(text = text, type = "VisitedItem", url = url, external = true)
    }

    override fun settingsItemClick(text: String, url: String) {
        // external as these links will be opened in the device browser
        navigation(text = text, type = "SettingsItem", url = url, external = true)
    }

    override fun toggleFunction(text: String, section: String, action: String) {
        function(
            text = text,
            type = "Toggle",
            section = section,
            action = action
        )
    }

    override fun buttonFunction(
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

    override fun topicsCustomised() {
        firebaseAnalytics.setUserProperty("topics_customised", "true")
    }

    private fun navigation(
        text: String? = null,
        type: String,
        url: String? = null,
        external: Boolean = false,
        section: String? = null
    ) {
        val parameters = bundleOf(
            "type" to type,
            "external" to external,
        )

        text?.let {
            parameters.putString("text", it)
        }

        url?.let {
            parameters.putString("url", it)
        }

        section?.let {
            parameters.putString("section", it)
        }

        firebaseAnalytics.logEvent(
            "Navigation",
            parametersWithLanguage(parameters)
        )
    }

    private fun function(text: String, type: String, section: String, action: String) {
        val parameters = bundleOf(
            "text" to text,
            "type" to type,
            "section" to section,
            "action" to action
        )

        firebaseAnalytics.logEvent(
            "Function",
            parametersWithLanguage(parameters)
        )
    }

    private fun parametersWithLanguage(parameters: Bundle): Bundle {
        val bundle = Bundle()
        bundle.putString("language", Locale.getDefault().language)
        bundle.putAll(parameters)
        return bundle
    }
}
