package uk.govuk.app.navigation

import androidx.navigation.NavHostController
import io.mockk.clearMocks
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Test
import uk.govuk.app.AppUiState
import uk.govuk.app.analytics.navigation.ANALYTICS_GRAPH_ROUTE
import uk.govuk.app.home.navigation.HOME_GRAPH_ROUTE
import uk.govuk.app.onboarding.navigation.ONBOARDING_GRAPH_ROUTE
import uk.govuk.app.topics.navigation.TOPICS_GRAPH_ROUTE

class AppLaunchNavigationTest {

    private val navController = mockk<NavHostController>(relaxed = true)

    @Test
    fun `Given app unavailable should be displayed, then return correct start destination`() {
        val appLaunchNavigation = AppLaunchNavigation(
            navController,
            AppUiState(
                shouldDisplayAppUnavailable = true,
                shouldDisplayAnalyticsConsent = false,
                shouldDisplayOnboarding = false,
                shouldDisplayTopicSelection = false,
                isSearchEnabled = false,
                isRecentActivityEnabled = false,
                isTopicsEnabled = false
            )
        )

        assertEquals(APP_UNAVAILABLE_GRAPH_ROUTE, appLaunchNavigation.startDestination)
    }

    @Test
    fun `Given analytics consent, onboarding and topic selection should be displayed, then return correct start destination and navigate through routes`() {
        val appLaunchNavigation = AppLaunchNavigation(
            navController,
            AppUiState(
                shouldDisplayAppUnavailable = false,
                shouldDisplayAnalyticsConsent = true,
                shouldDisplayOnboarding = true,
                shouldDisplayTopicSelection = true,
                isSearchEnabled = true,
                isRecentActivityEnabled = true,
                isTopicsEnabled = true
            )
        )

        assertEquals(ANALYTICS_GRAPH_ROUTE, appLaunchNavigation.startDestination)

        appLaunchNavigation.next()

        verify {
            navController.popBackStack()
            navController.navigate(ONBOARDING_GRAPH_ROUTE)
        }

        clearMocks(navController)

        appLaunchNavigation.next()

        verify {
            navController.popBackStack()
            navController.navigate(TOPICS_GRAPH_ROUTE)
        }

        clearMocks(navController)

        appLaunchNavigation.next()

        verify {
            navController.popBackStack()
            navController.navigate(HOME_GRAPH_ROUTE)
        }
    }

    @Test
    fun `Given analytics consent and onboarding should be displayed, then return correct start destination and navigate through routes`() {
        val appLaunchNavigation = AppLaunchNavigation(
            navController,
            AppUiState(
                shouldDisplayAppUnavailable = false,
                shouldDisplayAnalyticsConsent = true,
                shouldDisplayOnboarding = true,
                shouldDisplayTopicSelection = false,
                isSearchEnabled = true,
                isRecentActivityEnabled = true,
                isTopicsEnabled = true
            )
        )

        assertEquals(ANALYTICS_GRAPH_ROUTE, appLaunchNavigation.startDestination)

        clearMocks(navController)

        appLaunchNavigation.next()

        verify {
            navController.popBackStack()
            navController.navigate(ONBOARDING_GRAPH_ROUTE)
        }

        clearMocks(navController)

        appLaunchNavigation.next()

        verify {
            navController.popBackStack()
            navController.navigate(HOME_GRAPH_ROUTE)
        }
    }

    @Test
    fun `Given analytics consent and topic selection should be displayed, then return correct start destination and navigate through routes`() {
        val appLaunchNavigation = AppLaunchNavigation(
            navController,
            AppUiState(
                shouldDisplayAppUnavailable = false,
                shouldDisplayAnalyticsConsent = true,
                shouldDisplayOnboarding = false,
                shouldDisplayTopicSelection = true,
                isSearchEnabled = true,
                isRecentActivityEnabled = true,
                isTopicsEnabled = true
            )
        )

        assertEquals(ANALYTICS_GRAPH_ROUTE, appLaunchNavigation.startDestination)

        clearMocks(navController)

        appLaunchNavigation.next()

        verify {
            navController.popBackStack()
            navController.navigate(TOPICS_GRAPH_ROUTE)
        }

        clearMocks(navController)

        appLaunchNavigation.next()

        verify {
            navController.popBackStack()
            navController.navigate(HOME_GRAPH_ROUTE)
        }
    }

    @Test
    fun `Given analytics consent should be displayed, then return correct start destination and navigate through routes`() {
        val appLaunchNavigation = AppLaunchNavigation(
            navController,
            AppUiState(
                shouldDisplayAppUnavailable = false,
                shouldDisplayAnalyticsConsent = true,
                shouldDisplayOnboarding = false,
                shouldDisplayTopicSelection = false,
                isSearchEnabled = true,
                isRecentActivityEnabled = true,
                isTopicsEnabled = true
            )
        )

        assertEquals(ANALYTICS_GRAPH_ROUTE, appLaunchNavigation.startDestination)

        clearMocks(navController)

        appLaunchNavigation.next()

        verify {
            navController.popBackStack()
            navController.navigate(HOME_GRAPH_ROUTE)
        }
    }

    @Test
    fun `Given onboarding and topic selection should be displayed, then return correct start destination and navigate through routes`() {
        val appLaunchNavigation = AppLaunchNavigation(
            navController,
            AppUiState(
                shouldDisplayAppUnavailable = false,
                shouldDisplayAnalyticsConsent = false,
                shouldDisplayOnboarding = true,
                shouldDisplayTopicSelection = true,
                isSearchEnabled = true,
                isRecentActivityEnabled = true,
                isTopicsEnabled = true
            )
        )

        assertEquals(ONBOARDING_GRAPH_ROUTE, appLaunchNavigation.startDestination)

        clearMocks(navController)

        appLaunchNavigation.next()

        verify {
            navController.popBackStack()
            navController.navigate(TOPICS_GRAPH_ROUTE)
        }

        clearMocks(navController)

        appLaunchNavigation.next()

        verify {
            navController.popBackStack()
            navController.navigate(HOME_GRAPH_ROUTE)
        }
    }

    @Test
    fun `Given onboarding should be displayed, then return correct start destination and navigate through routes`() {
        val appLaunchNavigation = AppLaunchNavigation(
            navController,
            AppUiState(
                shouldDisplayAppUnavailable = false,
                shouldDisplayAnalyticsConsent = false,
                shouldDisplayOnboarding = true,
                shouldDisplayTopicSelection = false,
                isSearchEnabled = true,
                isRecentActivityEnabled = true,
                isTopicsEnabled = true
            )
        )

        assertEquals(ONBOARDING_GRAPH_ROUTE, appLaunchNavigation.startDestination)

        clearMocks(navController)

        appLaunchNavigation.next()

        verify {
            navController.popBackStack()
            navController.navigate(HOME_GRAPH_ROUTE)
        }
    }

    @Test
    fun `Given topic selection should be displayed, then return correct start destination and navigate through routes`() {
        val appLaunchNavigation = AppLaunchNavigation(
            navController,
            AppUiState(
                shouldDisplayAppUnavailable = false,
                shouldDisplayAnalyticsConsent = false,
                shouldDisplayOnboarding = false,
                shouldDisplayTopicSelection = true,
                isSearchEnabled = true,
                isRecentActivityEnabled = true,
                isTopicsEnabled = true
            )
        )

        assertEquals(TOPICS_GRAPH_ROUTE, appLaunchNavigation.startDestination)

        clearMocks(navController)

        appLaunchNavigation.next()

        verify {
            navController.popBackStack()
            navController.navigate(HOME_GRAPH_ROUTE)
        }
    }

    @Test
    fun `Given analytics, onboarding and topic selection should not be displayed, then return home as start destination`() {
        val appLaunchNavigation = AppLaunchNavigation(
            navController,
            AppUiState(
                shouldDisplayAppUnavailable = false,
                shouldDisplayAnalyticsConsent = false,
                shouldDisplayOnboarding = false,
                shouldDisplayTopicSelection = false,
                isSearchEnabled = true,
                isRecentActivityEnabled = true,
                isTopicsEnabled = true
            )
        )

        assertEquals(HOME_GRAPH_ROUTE, appLaunchNavigation.startDestination)
    }
}