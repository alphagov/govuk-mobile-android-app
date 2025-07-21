package uk.gov.govuk.navigation

import android.net.Uri
import androidx.navigation.NavController
import androidx.navigation.NavOptionsBuilder
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import uk.gov.govuk.analytics.AnalyticsClient
import uk.gov.govuk.analytics.navigation.ANALYTICS_GRAPH_ROUTE
import uk.gov.govuk.config.data.flags.FlagRepo
import uk.gov.govuk.data.AppRepo
import uk.gov.govuk.data.auth.AuthRepo
import uk.gov.govuk.home.navigation.HOME_GRAPH_ROUTE
import uk.gov.govuk.login.navigation.LOGIN_GRAPH_ROUTE
import uk.gov.govuk.notifications.NotificationsClient
import uk.gov.govuk.notifications.data.NotificationsRepo
import uk.gov.govuk.notifications.navigation.NOTIFICATIONS_CONSENT_ON_NEXT_ROUTE
import uk.gov.govuk.notifications.navigation.NOTIFICATIONS_CONSENT_ROUTE
import uk.gov.govuk.notifications.navigation.NOTIFICATIONS_ONBOARDING_GRAPH_ROUTE
import uk.gov.govuk.topics.TopicsFeature
import uk.gov.govuk.topics.navigation.TOPIC_SELECTION_GRAPH_ROUTE

class AppNavigationTest {

    private val flagRepo = mockk<FlagRepo>(relaxed = true)
    private val analyticsClient = mockk<AnalyticsClient>(relaxed = true)
    private val appRepo = mockk<AppRepo>(relaxed = true)
    private val authRepo = mockk<AuthRepo>(relaxed = true)
    private val notificationsRepo = mockk<NotificationsRepo>(relaxed = true)
    private val topicsFeature = mockk<TopicsFeature>(relaxed = true)
    private val deeplinkHandler = mockk<DeeplinkHandler>(relaxed = true)
    private val notificationsClient = mockk<NotificationsClient>(relaxed = true)
    private val navController = mockk<NavController>(relaxed = true)
    private val deeplink = mockk<Uri>(relaxed = true)

    private lateinit var appLaunchNav: AppNavigation

    @Before
    fun setup() {
        appLaunchNav = AppNavigation(
            flagRepo,
            analyticsClient,
            appRepo,
            authRepo,
            topicsFeature,
            deeplinkHandler,
            notificationsClient,
            notificationsRepo
        )
    }

    @Test
    fun `Set launch browser on deeplink handler`() {
        val onLaunchBrowser: (String) -> Unit = { }
        appLaunchNav.setOnLaunchBrowser(onLaunchBrowser)
        verify {
            deeplinkHandler.onLaunchBrowser = onLaunchBrowser
        }
    }

    @Test
    fun `Set deeplink not found on deeplink handler`() {
        val onDeeplinkNotFound: () -> Unit = { }
        appLaunchNav.setOnDeeplinkNotFound(onDeeplinkNotFound)
        verify {
            deeplinkHandler.onDeeplinkNotFound = onDeeplinkNotFound
        }
    }

    @Test
    fun `Set deeplink when user session not active`() {
        every { authRepo.isUserSessionActive() } returns false
        appLaunchNav.setDeeplink(navController, deeplink)

        verify {
            deeplinkHandler.deepLink = deeplink
        }

        verify(exactly = 0) {
            deeplinkHandler.handleDeeplink(any())
        }
    }

    @Test
    fun `Set deeplink when user session active`() {
        every { authRepo.isUserSessionActive() } returns true
        appLaunchNav.setDeeplink(navController, deeplink)

        verify {
            deeplinkHandler.deepLink = deeplink
            deeplinkHandler.handleDeeplink(navController)
        }
    }

    @Test
    fun `On next navigates to analytics consent`() {
        every { analyticsClient.isAnalyticsConsentRequired() } returns true
        runTest {
            appLaunchNav.onNext(navController)

            verify {
                navController.popBackStack()
                navController.navigate(ANALYTICS_GRAPH_ROUTE)
            }
        }
    }

    @Test
    fun `On next navigates to topic selection`() {
        every { analyticsClient.isAnalyticsConsentRequired() } returns false
        every { flagRepo.isTopicsEnabled() } returns true
        coEvery { appRepo.isTopicSelectionCompleted() } returns false
        coEvery { topicsFeature.hasTopics() } returns true

        runTest {
            appLaunchNav.onNext(navController)

            verify {
                navController.popBackStack()
                navController.navigate(TOPIC_SELECTION_GRAPH_ROUTE)
            }
        }
    }

    @Test
    fun `On next navigates to notifications onboarding - topics disabled`() {
        every { analyticsClient.isAnalyticsConsentRequired() } returns false
        every { flagRepo.isTopicsEnabled() } returns false
        coEvery { appRepo.isTopicSelectionCompleted() } returns false
        coEvery { topicsFeature.hasTopics() } returns true
        every { flagRepo.isNotificationsEnabled() } returns true

        runTest {
            appLaunchNav.onNext(navController)

            verify {
                navController.popBackStack()
                navController.navigate(NOTIFICATIONS_ONBOARDING_GRAPH_ROUTE)
            }
        }
    }

    @Test
    fun `On next navigates to notifications onboarding - topic selection completed`() {
        every { analyticsClient.isAnalyticsConsentRequired() } returns false
        every { flagRepo.isTopicsEnabled() } returns true
        coEvery { appRepo.isTopicSelectionCompleted() } returns true
        coEvery { topicsFeature.hasTopics() } returns true
        every { flagRepo.isNotificationsEnabled() } returns true

        runTest {
            appLaunchNav.onNext(navController)

            verify {
                navController.popBackStack()
                navController.navigate(NOTIFICATIONS_ONBOARDING_GRAPH_ROUTE)
            }
        }
    }

    @Test
    fun `On next navigates to notifications onboarding - no topics`() {
        every { analyticsClient.isAnalyticsConsentRequired() } returns false
        every { flagRepo.isTopicsEnabled() } returns true
        coEvery { appRepo.isTopicSelectionCompleted() } returns false
        coEvery { topicsFeature.hasTopics() } returns false
        every { flagRepo.isNotificationsEnabled() } returns true

        runTest {
            appLaunchNav.onNext(navController)

            verify {
                navController.popBackStack()
                navController.navigate(NOTIFICATIONS_ONBOARDING_GRAPH_ROUTE)
            }
        }
    }

    @Test
    fun `On next navigates to notifications consent - no topics`() {
        every { analyticsClient.isAnalyticsConsentRequired() } returns false
        every { flagRepo.isTopicsEnabled() } returns true
        coEvery { appRepo.isTopicSelectionCompleted() } returns false
        coEvery { topicsFeature.hasTopics() } returns false
        every { flagRepo.isNotificationsEnabled() } returns true
        coEvery { notificationsRepo.isNotificationsOnboardingCompleted() } returns true
        every { notificationsClient.permissionGranted(any()) } returns true
        every { notificationsClient.consentGiven() } returns false

        runTest {
            appLaunchNav.onNext(navController)

            verify {
                navController.popBackStack()
                navController.navigate(NOTIFICATIONS_CONSENT_ON_NEXT_ROUTE)
            }
        }
    }

    @Test
    fun `On next navigates to home - notifications disabled`() {
        every { analyticsClient.isAnalyticsConsentRequired() } returns false
        every { flagRepo.isTopicsEnabled() } returns true
        coEvery { appRepo.isTopicSelectionCompleted() } returns false
        coEvery { topicsFeature.hasTopics() } returns false
        every { flagRepo.isNotificationsEnabled() } returns false

        runTest {
            appLaunchNav.onNext(navController)

            verify {
                navController.popBackStack()
                navController.navigate(HOME_GRAPH_ROUTE)
                deeplinkHandler.handleDeeplink(navController)
            }
        }
    }

    @Test
    fun `On next navigates to home - notifications onboarding completed`() {
        every { analyticsClient.isAnalyticsConsentRequired() } returns false
        every { flagRepo.isTopicsEnabled() } returns true
        coEvery { appRepo.isTopicSelectionCompleted() } returns false
        coEvery { topicsFeature.hasTopics() } returns false
        every { flagRepo.isNotificationsEnabled() } returns true

        runTest {
            appLaunchNav.onNotificationsOnboardingCompleted(navController)

            clearAllMocks()

            appLaunchNav.onNext(navController)

            verify {
                navController.popBackStack()
                navController.navigate(HOME_GRAPH_ROUTE)
                deeplinkHandler.handleDeeplink(navController)
            }
        }
    }

    @Test
    fun `On next navigates to home - notifications onboarding completed, notifications permissions not granted`() {
        every { analyticsClient.isAnalyticsConsentRequired() } returns false
        every { flagRepo.isTopicsEnabled() } returns true
        coEvery { appRepo.isTopicSelectionCompleted() } returns false
        coEvery { topicsFeature.hasTopics() } returns false
        every { flagRepo.isNotificationsEnabled() } returns true
        every { notificationsClient.permissionGranted(any()) } returns false

        runTest {
            appLaunchNav.onNotificationsOnboardingCompleted(navController)

            clearAllMocks()

            appLaunchNav.onNext(navController)

            verify {
                navController.popBackStack()
                navController.navigate(HOME_GRAPH_ROUTE)
                deeplinkHandler.handleDeeplink(navController)
            }
        }
    }

    @Test
    fun `On next navigates to home - notifications onboarding completed, notifications permissions granted, consent given`() {
        every { analyticsClient.isAnalyticsConsentRequired() } returns false
        every { flagRepo.isTopicsEnabled() } returns true
        coEvery { appRepo.isTopicSelectionCompleted() } returns false
        coEvery { topicsFeature.hasTopics() } returns false
        every { flagRepo.isNotificationsEnabled() } returns true
        every { notificationsClient.permissionGranted(any()) } returns true
        every { notificationsClient.consentGiven() } returns true

        runTest {
            appLaunchNav.onNotificationsOnboardingCompleted(navController)

            clearAllMocks()

            appLaunchNav.onNext(navController)

            verify {
                navController.popBackStack()
                navController.navigate(HOME_GRAPH_ROUTE)
                deeplinkHandler.handleDeeplink(navController)
            }
        }
    }

    @Test
    fun `On notifications onboarding completed navigates to home`() {
        every { analyticsClient.isAnalyticsConsentRequired() } returns false
        every { flagRepo.isTopicsEnabled() } returns true
        coEvery { appRepo.isTopicSelectionCompleted() } returns false
        coEvery { notificationsRepo.isNotificationsOnboardingCompleted() } returns true
        coEvery { topicsFeature.hasTopics() } returns false
        every { flagRepo.isNotificationsEnabled() } returns true

        runTest {
            appLaunchNav.onNotificationsOnboardingCompleted(navController)

            coVerify(exactly = 1) {
                notificationsRepo.notificationsOnboardingCompleted()
            }

            verify(exactly = 1) {
                navController.popBackStack()
                navController.navigate(HOME_GRAPH_ROUTE)
                deeplinkHandler.handleDeeplink(navController)
            }
        }
    }

    @Test
    fun `On navigate on resume, when user session not active, navigates to login`() {
        every { authRepo.isUserSessionActive() } returns false

        runTest {
            appLaunchNav.navigateOnResume(navController)

            verify(exactly = 1) {
                navController.navigate(LOGIN_GRAPH_ROUTE, any<NavOptionsBuilder.() -> Unit>())
            }
        }
    }

    @Test
    fun `On navigate on resume, when notifications disabled, doesn't navigate to notifications consent`() {
        every { flagRepo.isNotificationsEnabled() } returns false

        runTest {
            appLaunchNav.navigateOnResume(navController)

            verify(exactly = 0) {
                navController.popBackStack()
                navController.navigate(NOTIFICATIONS_CONSENT_ROUTE)
            }
        }
    }

    @Test
    fun `On navigate on resume, when notifications enabled and permission not granted, removes consent`() {
        every { flagRepo.isNotificationsEnabled() } returns true
        every { notificationsClient.permissionGranted(any()) } returns false

        runTest {
            appLaunchNav.navigateOnResume(navController)

            verify(exactly = 1) {
                notificationsClient.removeConsent()
            }
            verify(exactly = 0) {
                navController.popBackStack()
                navController.navigate(NOTIFICATIONS_CONSENT_ROUTE)
            }
        }
    }

    @Test
    fun `On navigate on resume, when notifications enabled, permission not granted and current destination is consent, removes consent and navigates home`() {
        every { analyticsClient.isAnalyticsConsentRequired() } returns false
        every { flagRepo.isTopicsEnabled() } returns true
        coEvery { appRepo.isTopicSelectionCompleted() } returns false
        coEvery { notificationsRepo.isNotificationsOnboardingCompleted() } returns true
        coEvery { topicsFeature.hasTopics() } returns false
        every { flagRepo.isNotificationsEnabled() } returns true
        every { notificationsClient.permissionGranted(any()) } returns false
        every { navController.currentDestination?.route } returns NOTIFICATIONS_CONSENT_ON_NEXT_ROUTE

        runTest {
            appLaunchNav.navigateOnResume(navController)

            verify(exactly = 1) {
                notificationsClient.removeConsent()
            }
            verify(exactly = 1) {
                navController.popBackStack()
                navController.navigate(HOME_GRAPH_ROUTE)
                deeplinkHandler.handleDeeplink(navController)
            }
        }
    }

    @Test
    fun `On navigate on resume, when notifications enabled, permission not granted and current destination is home, removes consent and doesn't navigate`() {
        every { analyticsClient.isAnalyticsConsentRequired() } returns false
        every { flagRepo.isTopicsEnabled() } returns true
        coEvery { appRepo.isTopicSelectionCompleted() } returns false
        coEvery { notificationsRepo.isNotificationsOnboardingCompleted() } returns true
        coEvery { topicsFeature.hasTopics() } returns false
        every { flagRepo.isNotificationsEnabled() } returns true
        every { notificationsClient.permissionGranted(any()) } returns false
        every { navController.currentDestination?.route } returns HOME_GRAPH_ROUTE

        runTest {
            appLaunchNav.navigateOnResume(navController)

            verify(exactly = 1) {
                notificationsClient.removeConsent()
            }
            verify(exactly = 0) {
                navController.popBackStack()
                navController.navigate(HOME_GRAPH_ROUTE)
                deeplinkHandler.handleDeeplink(navController)
            }
        }
    }

    @Test
    fun `On navigate on resume, when notifications enabled and permission granted, user session not active, doesn't navigate`() {
        every { flagRepo.isNotificationsEnabled() } returns true
        every { notificationsClient.permissionGranted(any()) } returns true
        every { authRepo.isUserSessionActive() } returns false

        runTest {
            appLaunchNav.navigateOnResume(navController)

            verify(exactly = 0) {
                navController.navigate(NOTIFICATIONS_CONSENT_ROUTE)
            }
        }
    }

    @Test
    fun `On navigate on resume, when notifications enabled, permission granted, user session active and notifications onboarding not completed, doesn't navigate`() {
        every { flagRepo.isNotificationsEnabled() } returns true
        every { notificationsClient.permissionGranted(any()) } returns true
        every { authRepo.isUserSessionActive() } returns true
        coEvery { notificationsRepo.isNotificationsOnboardingCompleted() } returns false

        runTest {
            appLaunchNav.navigateOnResume(navController)

            verify(exactly = 0) {
                navController.navigate(NOTIFICATIONS_CONSENT_ROUTE)
            }
        }
    }

    @Test
    fun `On navigate on resume, when notifications enabled, permission granted, user session active, notifications onboarding completed and consent given, doesn't navigate`() {
        every { flagRepo.isNotificationsEnabled() } returns true
        every { notificationsClient.permissionGranted(any()) } returns true
        every { authRepo.isUserSessionActive() } returns true
        coEvery { notificationsRepo.isNotificationsOnboardingCompleted() } returns false
        coEvery { notificationsClient.consentGiven() } returns true

        runTest {
            appLaunchNav.navigateOnResume(navController)

            verify(exactly = 0) {
                navController.navigate(NOTIFICATIONS_CONSENT_ROUTE)
            }
        }
    }

    @Test
    fun `On navigate on resume, when notifications enabled, permission granted, user session active, notifications onboarding completed and consent not given, navigates to consent`() {
        every { flagRepo.isNotificationsEnabled() } returns true
        every { notificationsClient.permissionGranted(any()) } returns true
        every { authRepo.isUserSessionActive() } returns true
        coEvery { notificationsRepo.isNotificationsOnboardingCompleted() } returns true
        coEvery { notificationsClient.consentGiven() } returns false

        runTest {
            appLaunchNav.navigateOnResume(navController)

            verify(exactly = 1) {
                navController.navigate(NOTIFICATIONS_CONSENT_ROUTE)
            }
        }
    }

    @Test
    fun `On sign out navigates to login`() {
        appLaunchNav.onSignOut(navController)

        verify {
            navController.navigate(LOGIN_GRAPH_ROUTE, any<NavOptionsBuilder.() -> Unit>())
        }
    }
}
