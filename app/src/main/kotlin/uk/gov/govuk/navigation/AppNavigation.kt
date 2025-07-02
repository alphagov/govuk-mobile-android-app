package uk.gov.govuk.navigation

import android.net.Uri
import androidx.navigation.NavController
import uk.gov.govuk.analytics.AnalyticsClient
import uk.gov.govuk.analytics.navigation.ANALYTICS_GRAPH_ROUTE
import uk.gov.govuk.config.data.flags.FlagRepo
import uk.gov.govuk.data.AppRepo
import uk.gov.govuk.home.navigation.HOME_GRAPH_ROUTE
import uk.gov.govuk.login.navigation.LOGIN_GRAPH_ROUTE
import uk.gov.govuk.notifications.navigation.NOTIFICATIONS_ONBOARDING_GRAPH_ROUTE
import uk.gov.govuk.search.navigation.searchDeepLinks
import uk.gov.govuk.settings.navigation.settingsDeepLinks
import uk.gov.govuk.topics.TopicsFeature
import uk.gov.govuk.topics.navigation.TOPIC_SELECTION_GRAPH_ROUTE
import uk.gov.govuk.topics.navigation.topicsDeepLinks
import uk.gov.govuk.visited.navigation.visitedDeepLinks
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class AppNavigation @Inject constructor(
    private val flagRepo: FlagRepo,
    private val analyticsClient: AnalyticsClient,
    private val appRepo: AppRepo,
    private val topicsFeature: TopicsFeature
) {
    private val deepLinks =
        settingsDeepLinks + searchDeepLinks + topicsDeepLinks + visitedDeepLinks

    private var deepLink: String? = null

    fun setDeeplink(uri: Uri?) {
        uri?.path?.let { path ->
            deepLink = path
        }
    }

    suspend fun onLoginCompleted(navController: NavController) {
        navigateToAnalyticsConsent(navController)
    }

    private suspend fun navigateToAnalyticsConsent(navController: NavController) {
        if (analyticsClient.isAnalyticsConsentRequired()) {
            navigate(navController, ANALYTICS_GRAPH_ROUTE)
        } else {
            onAnalyticsConsentCompleted(navController)
        }
    }

    suspend fun onAnalyticsConsentCompleted(navController: NavController) {
        navigateToTopicSelection(navController)
    }

    private suspend fun navigateToTopicSelection(navController: NavController) {
        if (flagRepo.isTopicsEnabled()
            && !appRepo.isTopicSelectionCompleted()
            && topicsFeature.hasTopics()) {
            navigate(navController, TOPIC_SELECTION_GRAPH_ROUTE)
        } else {
            onTopicSelectionCompleted(navController)
        }
    }

    fun onTopicSelectionCompleted(navController: NavController) {
        navigateToNotificationsOnboarding(navController)
    }

    private fun navigateToNotificationsOnboarding(navController: NavController) {
        if (flagRepo.isNotificationsEnabled()) {
            navigate(navController, NOTIFICATIONS_ONBOARDING_GRAPH_ROUTE)
        } else {
            onNotificationsOnboardingCompleted(navController)
        }
    }

    fun onNotificationsOnboardingCompleted(navController: NavController) {
        navigate(navController, HOME_GRAPH_ROUTE)
        handleDeeplink(navController)
    }

    private fun handleDeeplink(navController: NavController) {
        // Todo - deeplinks with args are going to be tricky e.g. topics!!!
        deepLinks[deepLink]?.let {
            navController.navigate(it)
            deepLink = null
        }
    }

    private fun navigate(navController: NavController, route: String) {
        navController.popBackStack()
        navController.navigate(route)
    }

    fun onSignOut(navController: NavController) {
        navController.navigate(LOGIN_GRAPH_ROUTE) {
            popUpTo(0) { inclusive = true }
        }
    }
}
