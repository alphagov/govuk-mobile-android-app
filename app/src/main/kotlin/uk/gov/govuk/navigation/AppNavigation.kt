package uk.gov.govuk.navigation

import android.net.Uri
import android.util.Log
import androidx.navigation.NavController
import uk.gov.govuk.analytics.AnalyticsClient
import uk.gov.govuk.analytics.navigation.ANALYTICS_GRAPH_ROUTE
import uk.gov.govuk.config.data.flags.FlagRepo
import uk.gov.govuk.data.AppRepo
import uk.gov.govuk.data.auth.AuthRepo
import uk.gov.govuk.extension.getUrlParam
import uk.gov.govuk.home.navigation.HOME_GRAPH_ROUTE
import uk.gov.govuk.home.navigation.homeDeepLinks
import uk.gov.govuk.login.navigation.LOGIN_GRAPH_ROUTE
import uk.gov.govuk.notifications.navigation.NOTIFICATIONS_ONBOARDING_GRAPH_ROUTE
import uk.gov.govuk.search.navigation.searchDeepLinks
import uk.gov.govuk.settings.navigation.settingsDeepLinks
import uk.gov.govuk.topics.TopicsFeature
import uk.gov.govuk.topics.navigation.TOPIC_SELECTION_GRAPH_ROUTE
import uk.gov.govuk.topics.navigation.topicsDeepLinks
import uk.gov.govuk.ui.BrowserActivityLauncher
import uk.gov.govuk.visited.navigation.visitedDeepLinks
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class AppNavigation @Inject constructor(
    private val flagRepo: FlagRepo,
    private val analyticsClient: AnalyticsClient,
    private val appRepo: AppRepo,
    private val authRepo: AuthRepo,
    private val topicsFeature: TopicsFeature
) {
    private val deepLinks: Map<String, List<String>> by lazy {
        buildMap {
            putAll(homeDeepLinks)
            putAll(settingsDeepLinks)

            if (flagRepo.isSearchEnabled()) {
                putAll(searchDeepLinks)
            }

            if (flagRepo.isTopicsEnabled()) {
                putAll(topicsDeepLinks)
            }

            if (flagRepo.isRecentActivityEnabled()) {
                putAll(visitedDeepLinks)
            }
        }
    }

    private var deepLink: Uri? = null

    fun setDeeplink(
        navController: NavController,
        browserLauncher: BrowserActivityLauncher,
        uri: Uri?
    ) {
        deepLink = uri
        if (authRepo.isUserSessionActive()) {
            handleDeeplink(navController, browserLauncher)
        }
    }

    suspend fun onLoginCompleted(
        navController: NavController,
        browserLauncher: BrowserActivityLauncher
    ) {
        navigateToAnalyticsConsent(navController, browserLauncher)
    }

    private suspend fun navigateToAnalyticsConsent(
        navController: NavController,
        browserLauncher: BrowserActivityLauncher
    ) {
        if (analyticsClient.isAnalyticsConsentRequired()) {
            navigate(navController, ANALYTICS_GRAPH_ROUTE)
        } else {
            onAnalyticsConsentCompleted(navController, browserLauncher)
        }
    }

    suspend fun onAnalyticsConsentCompleted(
        navController: NavController,
        browserLauncher: BrowserActivityLauncher
    ) {
        navigateToTopicSelection(navController, browserLauncher)
    }

    private suspend fun navigateToTopicSelection(
        navController: NavController,
        browserLauncher: BrowserActivityLauncher,
    ) {
        if (flagRepo.isTopicsEnabled()
            && !appRepo.isTopicSelectionCompleted()
            && topicsFeature.hasTopics()) {
            navigate(navController, TOPIC_SELECTION_GRAPH_ROUTE)
        } else {
            onTopicSelectionCompleted(navController, browserLauncher)
        }
    }

    fun onTopicSelectionCompleted(
        navController: NavController,
        browserLauncher: BrowserActivityLauncher,
    ) {
        navigateToNotificationsOnboarding(navController, browserLauncher)
    }

    private fun navigateToNotificationsOnboarding(
        navController: NavController,
        browserLauncher: BrowserActivityLauncher,
    ) {
        if (flagRepo.isNotificationsEnabled()) {
            navigate(navController, NOTIFICATIONS_ONBOARDING_GRAPH_ROUTE)
        } else {
            onNotificationsOnboardingCompleted(navController, browserLauncher)
        }
    }

    fun onNotificationsOnboardingCompleted(
        navController: NavController,
        browserLauncher: BrowserActivityLauncher,
    ) {
        navigate(navController, HOME_GRAPH_ROUTE)
        handleDeeplink(navController, browserLauncher)
    }

    private fun handleDeeplink(
        navController: NavController,
        browserLauncher: BrowserActivityLauncher
    ) {
        deepLink?.let {
            deepLinks[it.path]?.let { routes ->
                navController.navigate(HOME_GRAPH_ROUTE) {
                    popUpTo(0) { inclusive = true }
                }

                // Construct backstack and navigate to deeplink route
                for (route in routes) {
                    navController.navigate(route) {
                        launchSingleTop = true
                    }
                }
            } ?: run {
                it.getUrlParam(DeepLink.allowedGovUkUrls)?.let { uri ->
                    browserLauncher.launch(uri.toString())
                } ?: run {
                    Log.d("Blah", "Broken deeplink: $it")
                }
            }

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
