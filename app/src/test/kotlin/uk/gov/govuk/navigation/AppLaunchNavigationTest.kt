package uk.gov.govuk.navigation

import androidx.navigation.NavController
import io.mockk.clearAllMocks
import io.mockk.coEvery
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
import uk.gov.govuk.login.navigation.BIOMETRIC_ROUTE
import uk.gov.govuk.login.navigation.LOGIN_GRAPH_ROUTE
import uk.gov.govuk.notifications.navigation.NOTIFICATIONS_ONBOARDING_GRAPH_ROUTE
import uk.gov.govuk.onboarding.navigation.ONBOARDING_GRAPH_ROUTE
import uk.gov.govuk.topics.TopicsFeature
import uk.gov.govuk.topics.navigation.TOPIC_SELECTION_GRAPH_ROUTE
import java.util.Stack
import kotlin.test.assertEquals

class AppLaunchNavigationTest {

    private val flagRepo = mockk<FlagRepo>(relaxed = true)
    private val analyticsClient = mockk<AnalyticsClient>(relaxed = true)
    private val appRepo = mockk<AppRepo>(relaxed = true)
    private val topicsFeature = mockk<TopicsFeature>(relaxed = true)
    private val authRepo = mockk<AuthRepo>(relaxed = true)
    private val navController = mockk<NavController>(relaxed = true)

    private lateinit var appLaunchNav: AppLaunchNavigation

    @Before
    fun setup() {
        appLaunchNav = AppLaunchNavigation(flagRepo, analyticsClient, appRepo, topicsFeature, authRepo)

        // Default config (simulates first time app launch)
        every { flagRepo.isNotificationsEnabled() } returns true
        every { flagRepo.isTopicsEnabled() } returns true
        coEvery { appRepo.isTopicSelectionCompleted() } returns false
        coEvery { authRepo.isAuthenticationEnabled() } returns true
        coEvery { authRepo.isUserSignedIn() } returns false
        coEvery { flagRepo.isLoginEnabled() } returns true
        coEvery { flagRepo.isOnboardingEnabled() } returns true
        coEvery { appRepo.isOnboardingCompleted() } returns false
        coEvery { analyticsClient.isAnalyticsConsentRequired() } returns true
        coEvery { topicsFeature.hasTopics() } returns true

        runTest {
            appLaunchNav.buildLaunchFlow()
        }
    }

    @Test
    fun `When build launch flow, builds all launch routes`() {
        every { flagRepo.isNotificationsEnabled() } returns true
        every { flagRepo.isTopicsEnabled() } returns true
        coEvery { appRepo.isTopicSelectionCompleted() } returns false
        coEvery { authRepo.isAuthenticationEnabled() } returns true
        coEvery { authRepo.isUserSignedIn() } returns false
        coEvery { flagRepo.isLoginEnabled() } returns true
        coEvery { flagRepo.isOnboardingEnabled() } returns true
        coEvery { appRepo.isOnboardingCompleted() } returns false
        coEvery { analyticsClient.isAnalyticsConsentRequired() } returns true

        runTest {
            appLaunchNav.buildLaunchFlow()

            assertEquals(LOGIN_GRAPH_ROUTE, appLaunchNav.startDestination)

            val expected = Stack<String>()
            expected.push(HOME_GRAPH_ROUTE)
            expected.push(NOTIFICATIONS_ONBOARDING_GRAPH_ROUTE)
            expected.push(TOPIC_SELECTION_GRAPH_ROUTE)
            expected.push(BIOMETRIC_ROUTE)
            expected.push(ONBOARDING_GRAPH_ROUTE)
            expected.push(ANALYTICS_GRAPH_ROUTE)

            assertEquals(expected, appLaunchNav.launchRoutes)
        }
    }

    @Test
    fun `Given analytics consent is not required, When build launch flow, builds launch routes`() {
        coEvery { analyticsClient.isAnalyticsConsentRequired() } returns false

        runTest {
            appLaunchNav.buildLaunchFlow()

            assertEquals(LOGIN_GRAPH_ROUTE, appLaunchNav.startDestination)

            val expected = Stack<String>()
            expected.push(HOME_GRAPH_ROUTE)
            expected.push(NOTIFICATIONS_ONBOARDING_GRAPH_ROUTE)
            expected.push(TOPIC_SELECTION_GRAPH_ROUTE)
            expected.push(BIOMETRIC_ROUTE)
            expected.push(ONBOARDING_GRAPH_ROUTE)

            assertEquals(expected, appLaunchNav.launchRoutes)
        }
    }

    @Test
    fun `Given onboarding is complete, When build launch flow, builds launch routes`() {
        coEvery { appRepo.isOnboardingCompleted() } returns true

        runTest {
            appLaunchNav.buildLaunchFlow()

            assertEquals(LOGIN_GRAPH_ROUTE, appLaunchNav.startDestination)

            val expected = Stack<String>()
            expected.push(HOME_GRAPH_ROUTE)
            expected.push(NOTIFICATIONS_ONBOARDING_GRAPH_ROUTE)
            expected.push(TOPIC_SELECTION_GRAPH_ROUTE)
            expected.push(BIOMETRIC_ROUTE)
            expected.push(ANALYTICS_GRAPH_ROUTE)

            assertEquals(expected, appLaunchNav.launchRoutes)
        }
    }

    @Test
    fun `Given onboarding is disabled, When build launch flow, builds launch routes`() {
        coEvery { flagRepo.isOnboardingEnabled() } returns false

        runTest {
            appLaunchNav.buildLaunchFlow()

            assertEquals(LOGIN_GRAPH_ROUTE, appLaunchNav.startDestination)

            val expected = Stack<String>()
            expected.push(HOME_GRAPH_ROUTE)
            expected.push(NOTIFICATIONS_ONBOARDING_GRAPH_ROUTE)
            expected.push(TOPIC_SELECTION_GRAPH_ROUTE)
            expected.push(BIOMETRIC_ROUTE)
            expected.push(ANALYTICS_GRAPH_ROUTE)

            assertEquals(expected, appLaunchNav.launchRoutes)
        }
    }

    @Test
    fun `Given login is disabled, When build launch flow, builds launch routes`() {
        coEvery { flagRepo.isLoginEnabled() } returns false

        runTest {
            appLaunchNav.buildLaunchFlow()

            assertEquals(ANALYTICS_GRAPH_ROUTE, appLaunchNav.startDestination)

            val expected = Stack<String>()
            expected.push(HOME_GRAPH_ROUTE)
            expected.push(NOTIFICATIONS_ONBOARDING_GRAPH_ROUTE)
            expected.push(TOPIC_SELECTION_GRAPH_ROUTE)
            expected.push(ONBOARDING_GRAPH_ROUTE)

            assertEquals(expected, appLaunchNav.launchRoutes)
        }
    }

    @Test
    fun `Given authentication is not enabled, When build launch flow, builds launch routes`() {
        coEvery { authRepo.isAuthenticationEnabled() } returns false

        runTest {
            appLaunchNav.buildLaunchFlow()

            assertEquals(LOGIN_GRAPH_ROUTE, appLaunchNav.startDestination)

            val expected = Stack<String>()
            expected.push(HOME_GRAPH_ROUTE)
            expected.push(NOTIFICATIONS_ONBOARDING_GRAPH_ROUTE)
            expected.push(TOPIC_SELECTION_GRAPH_ROUTE)
            expected.push(ONBOARDING_GRAPH_ROUTE)
            expected.push(ANALYTICS_GRAPH_ROUTE)

            assertEquals(expected, appLaunchNav.launchRoutes)
        }
    }

    @Test
    fun `Given user is signed in, When build launch flow, builds launch routes`() {
        coEvery { authRepo.isUserSignedIn() } returns true

        runTest {
            appLaunchNav.buildLaunchFlow()

            assertEquals(LOGIN_GRAPH_ROUTE, appLaunchNav.startDestination)

            val expected = Stack<String>()
            expected.push(HOME_GRAPH_ROUTE)
            expected.push(NOTIFICATIONS_ONBOARDING_GRAPH_ROUTE)
            expected.push(TOPIC_SELECTION_GRAPH_ROUTE)
            expected.push(ONBOARDING_GRAPH_ROUTE)
            expected.push(ANALYTICS_GRAPH_ROUTE)

            assertEquals(expected, appLaunchNav.launchRoutes)
        }
    }

    @Test
    fun `Given topics are disabled, When build launch flow, builds launch routes`() {
        every { flagRepo.isTopicsEnabled() } returns false

        runTest {
            appLaunchNav.buildLaunchFlow()

            assertEquals(LOGIN_GRAPH_ROUTE, appLaunchNav.startDestination)

            val expected = Stack<String>()
            expected.push(HOME_GRAPH_ROUTE)
            expected.push(NOTIFICATIONS_ONBOARDING_GRAPH_ROUTE)
            expected.push(BIOMETRIC_ROUTE)
            expected.push(ONBOARDING_GRAPH_ROUTE)
            expected.push(ANALYTICS_GRAPH_ROUTE)

            assertEquals(expected, appLaunchNav.launchRoutes)
        }
    }

    @Test
    fun `Given topic selection is complete, When build launch flow, builds launch routes`() {
        coEvery { appRepo.isTopicSelectionCompleted() } returns true

        runTest {
            appLaunchNav.buildLaunchFlow()

            assertEquals(LOGIN_GRAPH_ROUTE, appLaunchNav.startDestination)

            val expected = Stack<String>()
            expected.push(HOME_GRAPH_ROUTE)
            expected.push(NOTIFICATIONS_ONBOARDING_GRAPH_ROUTE)
            expected.push(BIOMETRIC_ROUTE)
            expected.push(ONBOARDING_GRAPH_ROUTE)
            expected.push(ANALYTICS_GRAPH_ROUTE)

            assertEquals(expected, appLaunchNav.launchRoutes)
        }
    }

    @Test
    fun `Given no topics, When build launch flow, builds launch routes`() {
        coEvery { topicsFeature.hasTopics() } returns false

        runTest {
            appLaunchNav.buildLaunchFlow()

            assertEquals(LOGIN_GRAPH_ROUTE, appLaunchNav.startDestination)

            val expected = Stack<String>()
            expected.push(HOME_GRAPH_ROUTE)
            expected.push(NOTIFICATIONS_ONBOARDING_GRAPH_ROUTE)
            expected.push(BIOMETRIC_ROUTE)
            expected.push(ONBOARDING_GRAPH_ROUTE)
            expected.push(ANALYTICS_GRAPH_ROUTE)

            assertEquals(expected, appLaunchNav.launchRoutes)
        }
    }

    @Test
    fun `Given notifications are disabled, When build launch flow, builds launch routes`() {
        every { flagRepo.isNotificationsEnabled() } returns false

        runTest {
            appLaunchNav.buildLaunchFlow()

            assertEquals(LOGIN_GRAPH_ROUTE, appLaunchNav.startDestination)

            val expected = Stack<String>()
            expected.push(HOME_GRAPH_ROUTE)
            expected.push(TOPIC_SELECTION_GRAPH_ROUTE)
            expected.push(BIOMETRIC_ROUTE)
            expected.push(ONBOARDING_GRAPH_ROUTE)
            expected.push(ANALYTICS_GRAPH_ROUTE)

            assertEquals(expected, appLaunchNav.launchRoutes)
        }
    }

    @Test
    fun `When different user login, builds all launch routes`() {
        runTest {
            appLaunchNav.onDifferentUserLogin(true)

            assertEquals(LOGIN_GRAPH_ROUTE, appLaunchNav.startDestination)

            val expected = Stack<String>()
            expected.push(HOME_GRAPH_ROUTE)
            expected.push(NOTIFICATIONS_ONBOARDING_GRAPH_ROUTE)
            expected.push(TOPIC_SELECTION_GRAPH_ROUTE)
            expected.push(BIOMETRIC_ROUTE)
            expected.push(ANALYTICS_GRAPH_ROUTE)

            assertEquals(expected, appLaunchNav.launchRoutes)
        }
    }

    @Test
    fun `Given authentication is not enabled, When different user login, build launch routes`() {
        every { authRepo.isAuthenticationEnabled() } returns false

        runTest {
            appLaunchNav.onDifferentUserLogin(true)

            assertEquals(LOGIN_GRAPH_ROUTE, appLaunchNav.startDestination)

            val expected = Stack<String>()
            expected.push(HOME_GRAPH_ROUTE)
            expected.push(NOTIFICATIONS_ONBOARDING_GRAPH_ROUTE)
            expected.push(TOPIC_SELECTION_GRAPH_ROUTE)
            expected.push(ANALYTICS_GRAPH_ROUTE)

            assertEquals(expected, appLaunchNav.launchRoutes)
        }
    }

    @Test
    fun `Given topics are disabled, When different user login, build launch routes`() {
        every { flagRepo.isTopicsEnabled() } returns false

        runTest {
            appLaunchNav.onDifferentUserLogin(true)

            assertEquals(LOGIN_GRAPH_ROUTE, appLaunchNav.startDestination)

            val expected = Stack<String>()
            expected.push(HOME_GRAPH_ROUTE)
            expected.push(NOTIFICATIONS_ONBOARDING_GRAPH_ROUTE)
            expected.push(BIOMETRIC_ROUTE)
            expected.push(ANALYTICS_GRAPH_ROUTE)

            assertEquals(expected, appLaunchNav.launchRoutes)
        }
    }

    @Test
    fun `Given no topics, When different user login, build launch routes`() {
        runTest {
            appLaunchNav.onDifferentUserLogin(false)

            assertEquals(LOGIN_GRAPH_ROUTE, appLaunchNav.startDestination)

            val expected = Stack<String>()
            expected.push(HOME_GRAPH_ROUTE)
            expected.push(NOTIFICATIONS_ONBOARDING_GRAPH_ROUTE)
            expected.push(BIOMETRIC_ROUTE)
            expected.push(ANALYTICS_GRAPH_ROUTE)

            assertEquals(expected, appLaunchNav.launchRoutes)
        }
    }

    @Test
    fun `Given notifications are disabled, When different user login, build launch routes`() {
        every { flagRepo.isNotificationsEnabled() } returns false

        runTest {
            appLaunchNav.onDifferentUserLogin(true)

            assertEquals(LOGIN_GRAPH_ROUTE, appLaunchNav.startDestination)

            val expected = Stack<String>()
            expected.push(HOME_GRAPH_ROUTE)
            expected.push(TOPIC_SELECTION_GRAPH_ROUTE)
            expected.push(BIOMETRIC_ROUTE)
            expected.push(ANALYTICS_GRAPH_ROUTE)

            assertEquals(expected, appLaunchNav.launchRoutes)
        }
    }

    @Test
    fun `When sign out, builds all launch routes`() {
        runTest {
            appLaunchNav.onSignOut()

            assertEquals(LOGIN_GRAPH_ROUTE, appLaunchNav.startDestination)

            val expected = Stack<String>()
            expected.push(HOME_GRAPH_ROUTE)
            expected.push(NOTIFICATIONS_ONBOARDING_GRAPH_ROUTE)
            expected.push(BIOMETRIC_ROUTE)

            assertEquals(expected, appLaunchNav.launchRoutes)
        }
    }

    @Test
    fun `Given authentication is not enabled, When sign out, build launch routes`() {
        every { authRepo.isAuthenticationEnabled() } returns false

        runTest {
            appLaunchNav.onSignOut()

            assertEquals(LOGIN_GRAPH_ROUTE, appLaunchNav.startDestination)

            val expected = Stack<String>()
            expected.push(HOME_GRAPH_ROUTE)
            expected.push(NOTIFICATIONS_ONBOARDING_GRAPH_ROUTE)

            assertEquals(expected, appLaunchNav.launchRoutes)
        }
    }

    @Test
    fun `Given notifications are disabled, When sign out, build launch routes`() {
        every { flagRepo.isNotificationsEnabled() } returns false

        runTest {
            appLaunchNav.onSignOut()

            assertEquals(LOGIN_GRAPH_ROUTE, appLaunchNav.startDestination)

            val expected = Stack<String>()
            expected.push(HOME_GRAPH_ROUTE)
            expected.push(BIOMETRIC_ROUTE)

            assertEquals(expected, appLaunchNav.launchRoutes)
        }
    }

    @Test
    fun `Given launch routes are empty, When on Next, pop back stack but don't navigate`() {
        every { flagRepo.isNotificationsEnabled() } returns false
        every { authRepo.isAuthenticationEnabled() } returns false

        appLaunchNav.onSignOut()
        appLaunchNav.onNext(navController)

        clearAllMocks()

        appLaunchNav.onNext(navController)
        verify {
            navController.popBackStack()
        }
        verify(exactly = 0) {
            navController.navigate(any<String>())
        }
    }

    @Test
    fun `Given launch routes are not empty, When on Next, pop back stack and navigate`() {
        appLaunchNav.onNext(navController)

        verify {
            navController.popBackStack()
            navController.navigate(ANALYTICS_GRAPH_ROUTE)
            assertEquals(ANALYTICS_GRAPH_ROUTE, appLaunchNav.startDestination)
        }
    }
}
