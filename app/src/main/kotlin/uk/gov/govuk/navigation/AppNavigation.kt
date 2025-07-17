package uk.gov.govuk.navigation

import android.net.Uri
import androidx.navigation.NavController
import uk.gov.govuk.analytics.AnalyticsClient
import uk.gov.govuk.analytics.navigation.ANALYTICS_GRAPH_ROUTE
import uk.gov.govuk.config.data.flags.FlagRepo
import uk.gov.govuk.data.AppRepo
import uk.gov.govuk.data.auth.AuthRepo
import uk.gov.govuk.home.navigation.HOME_GRAPH_ROUTE
import uk.gov.govuk.login.navigation.LOGIN_GRAPH_ROUTE
import uk.gov.govuk.notifications.NotificationsClient
import uk.gov.govuk.notifications.navigation.NOTIFICATIONS_CONSENT_ROUTE
import uk.gov.govuk.notifications.navigation.NOTIFICATIONS_ONBOARDING_GRAPH_ROUTE
import uk.gov.govuk.topics.TopicsFeature
import uk.gov.govuk.topics.navigation.TOPIC_SELECTION_GRAPH_ROUTE
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class AppNavigation @Inject constructor(
    private val flagRepo: FlagRepo,
    private val analyticsClient: AnalyticsClient,
    private val appRepo: AppRepo,
    private val authRepo: AuthRepo,
    private val topicsFeature: TopicsFeature,
    private val deeplinkHandler: DeeplinkHandler,
    private val notificationsClient: NotificationsClient
) {
    fun setOnLaunchBrowser(onLaunchBrowser: (String) -> Unit) {
        deeplinkHandler.onLaunchBrowser = onLaunchBrowser
    }

    fun setOnDeeplinkNotFound(onDeeplinkNotFound: () -> Unit) {
        deeplinkHandler.onDeeplinkNotFound = onDeeplinkNotFound
    }

    fun setDeeplink(navController: NavController, uri: Uri?) {
        deeplinkHandler.deepLink = uri
        if (authRepo.isUserSessionActive()) {
            deeplinkHandler.handleDeeplink(navController)
        }
    }

    suspend fun onNext(navController: NavController) {
        when {
            analyticsClient.isAnalyticsConsentRequired() -> navigate(navController, ANALYTICS_GRAPH_ROUTE)
            flagRepo.isTopicsEnabled() &&
                    !appRepo.isTopicSelectionCompleted() &&
                    topicsFeature.hasTopics() -> navigate(navController, TOPIC_SELECTION_GRAPH_ROUTE)
            flagRepo.isNotificationsEnabled() &&
                    !appRepo.isNotificationsOnboardingCompleted() -> navigate(navController, NOTIFICATIONS_ONBOARDING_GRAPH_ROUTE)
            else -> {
                navigate(navController, HOME_GRAPH_ROUTE)
                deeplinkHandler.handleDeeplink(navController)
            }
        }
    }

    suspend fun onNotificationsOnboardingCompleted(navController: NavController) {
        appRepo.notificationsOnboardingCompleted()
        onNext(navController)
    }

    suspend fun navigateToNotificationsConsent(navController: NavController) {
        val isPermissionGranted = notificationsClient.permissionGranted(navController.context)
        if (flagRepo.isNotificationsEnabled() &&
            appRepo.isNotificationsOnboardingCompleted() &&
            isPermissionGranted &&
            !notificationsClient.consentGiven()
        ) {
            navigate(navController, NOTIFICATIONS_CONSENT_ROUTE)
        } else if (!isPermissionGranted) {
            notificationsClient.removeConsent()
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
