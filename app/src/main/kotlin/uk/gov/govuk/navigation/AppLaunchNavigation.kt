package uk.gov.govuk.navigation

import androidx.navigation.NavController
import uk.gov.govuk.analytics.AnalyticsClient
import uk.gov.govuk.analytics.navigation.ANALYTICS_GRAPH_ROUTE
import uk.gov.govuk.config.data.flags.FlagRepo
import uk.gov.govuk.data.AppRepo
import uk.gov.govuk.data.auth.AuthRepo
import uk.gov.govuk.home.navigation.HOME_GRAPH_ROUTE
import uk.gov.govuk.login.navigation.BIOMETRIC_GRAPH_ROUTE
import uk.gov.govuk.notifications.navigation.NOTIFICATIONS_ONBOARDING_GRAPH_ROUTE
import uk.gov.govuk.topics.TopicsFeature
import uk.gov.govuk.topics.navigation.TOPIC_SELECTION_GRAPH_ROUTE
import java.util.Stack
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class AppLaunchNavigation @Inject constructor(
    private val flagRepo: FlagRepo,
    private val analyticsClient: AnalyticsClient,
    private val appRepo: AppRepo,
    private val topicsFeature: TopicsFeature,
    private val authRepo: AuthRepo
) {
    private var _launchRoutes = Stack<String>()
    val launchRoutes
        get() = _launchRoutes

    private var _startDestination = ""
    val startDestination
        get() = _startDestination

    suspend fun buildLaunchFlow() {
        _launchRoutes.clear()

        _launchRoutes.push(HOME_GRAPH_ROUTE)

        if (flagRepo.isNotificationsEnabled()) {
            _launchRoutes.push(NOTIFICATIONS_ONBOARDING_GRAPH_ROUTE)
        }

        if (flagRepo.isTopicsEnabled()
            && !appRepo.isTopicSelectionCompleted()
            && topicsFeature.hasTopics()) {
            _launchRoutes.push(TOPIC_SELECTION_GRAPH_ROUTE)
        }

        if (flagRepo.isLoginEnabled()) {
            if (authRepo.isAuthenticationEnabled()
                && !appRepo.hasSkippedBiometrics()) {
                _launchRoutes.push(BIOMETRIC_GRAPH_ROUTE)
            }
        }

        if (analyticsClient.isAnalyticsConsentRequired()) {
            _launchRoutes.push(ANALYTICS_GRAPH_ROUTE)
        }

        _startDestination = _launchRoutes.pop()
    }

    fun onDifferentUserLogin(hasTopics: Boolean) {
        _launchRoutes.clear()

        _launchRoutes.push(HOME_GRAPH_ROUTE)

        if (flagRepo.isNotificationsEnabled()) {
            _launchRoutes.push(NOTIFICATIONS_ONBOARDING_GRAPH_ROUTE)
        }

        if (flagRepo.isTopicsEnabled()
            && hasTopics) {
            _launchRoutes.push(TOPIC_SELECTION_GRAPH_ROUTE)
        }

        if (authRepo.isAuthenticationEnabled()) {
            _launchRoutes.push(BIOMETRIC_GRAPH_ROUTE)
        }

        _launchRoutes.push(ANALYTICS_GRAPH_ROUTE)
    }

    suspend fun onSignOut() {
        _launchRoutes.clear()

        _launchRoutes.push(HOME_GRAPH_ROUTE)

        if (flagRepo.isNotificationsEnabled()) {
            _launchRoutes.push(NOTIFICATIONS_ONBOARDING_GRAPH_ROUTE)
        }

        if (authRepo.isAuthenticationEnabled()
            && !appRepo.hasSkippedBiometrics()) {
            _launchRoutes.push(BIOMETRIC_GRAPH_ROUTE)
        }
    }

    fun onNext(navController: NavController) {
        if (_launchRoutes.isNotEmpty()) {
            val route = _launchRoutes.pop()
            // Todo - temp fix for refresh token expiry
            if (route == BIOMETRIC_GRAPH_ROUTE && authRepo.isUserSignedIn()) {
                onNext(navController)
            } else {
                navController.popBackStack()
                _startDestination = route
                navController.navigate(route)
            }
        }
    }
}
