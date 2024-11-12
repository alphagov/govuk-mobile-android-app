package uk.govuk.app.navigation

import org.junit.Assert.assertEquals
import org.junit.Test
import uk.govuk.app.AppUiState
import uk.govuk.app.analytics.navigation.ANALYTICS_GRAPH_ROUTE
import uk.govuk.app.home.navigation.HOME_GRAPH_ROUTE
import uk.govuk.app.onboarding.navigation.ONBOARDING_GRAPH_ROUTE
import uk.govuk.app.topics.navigation.TOPICS_GRAPH_ROUTE
import java.util.Stack

class AppLaunchNavigationTest {

    @Test
    fun `Given app unavailable should be displayed, then return correct launch routes`() {
        val appLaunchNavigation = AppLaunchNavigation(
            AppUiState(
                shouldDisplayAppUnavailable = true,
                shouldDisplayRecommendUpdate = false,
                shouldDisplayAnalyticsConsent = true,
                shouldDisplayOnboarding = true,
                shouldDisplayTopicSelection = true,
                isSearchEnabled = true,
                isRecentActivityEnabled = true,
                isTopicsEnabled = true
            )
        )

        val expected = Stack<String>()
        expected.push(APP_UNAVAILABLE_GRAPH_ROUTE)

        assertEquals(expected, appLaunchNavigation.launchRoutes)
    }

    @Test
    fun `Given recommend update, analytics consent, onboarding and topic selection should be displayed, then return correct start destination and navigate through routes`() {
        val appLaunchNavigation = AppLaunchNavigation(
            navController,
            AppUiState(
                shouldDisplayAppUnavailable = false,
                shouldDisplayRecommendUpdate = true,
                shouldDisplayAnalyticsConsent = true,
                shouldDisplayOnboarding = true,
                shouldDisplayTopicSelection = true,
                isSearchEnabled = true,
                isRecentActivityEnabled = true,
                isTopicsEnabled = true
            )
        )

        assertEquals(RECOMMEND_UPDATE_GRAPH_ROUTE, appLaunchNavigation.startDestination)

        appLaunchNavigation.next()

        verify {
            navController.popBackStack()
            navController.navigate(ANALYTICS_GRAPH_ROUTE)
        }

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
    fun `Given analytics consent, onboarding and topic selection should be displayed, then return correct launch routes`() {
        val appLaunchNavigation = AppLaunchNavigation(
            AppUiState(
                shouldDisplayAppUnavailable = false,
                shouldDisplayRecommendUpdate = false,
                shouldDisplayAnalyticsConsent = true,
                shouldDisplayOnboarding = true,
                shouldDisplayTopicSelection = true,
                isSearchEnabled = true,
                isRecentActivityEnabled = true,
                isTopicsEnabled = true
            )
        )

        val expected = Stack<String>()
        expected.push(HOME_GRAPH_ROUTE)
        expected.push(TOPICS_GRAPH_ROUTE)
        expected.push(ONBOARDING_GRAPH_ROUTE)
        expected.push(ANALYTICS_GRAPH_ROUTE)

        assertEquals(expected, appLaunchNavigation.launchRoutes)
    }

    @Test
    fun `Given analytics consent and onboarding should be displayed, then return correct launch routes`() {
        val appLaunchNavigation = AppLaunchNavigation(
            AppUiState(
                shouldDisplayAppUnavailable = false,
                shouldDisplayRecommendUpdate = false,
                shouldDisplayAnalyticsConsent = true,
                shouldDisplayOnboarding = true,
                shouldDisplayTopicSelection = false,
                isSearchEnabled = true,
                isRecentActivityEnabled = true,
                isTopicsEnabled = true
            )
        )

        val expected = Stack<String>()
        expected.push(HOME_GRAPH_ROUTE)
        expected.push(ONBOARDING_GRAPH_ROUTE)
        expected.push(ANALYTICS_GRAPH_ROUTE)

        assertEquals(expected, appLaunchNavigation.launchRoutes)
    }

    @Test
    fun `Given analytics consent and topic selection should be displayed, then return correct launch routes`() {
        val appLaunchNavigation = AppLaunchNavigation(
            AppUiState(
                shouldDisplayAppUnavailable = false,
                shouldDisplayRecommendUpdate = false,
                shouldDisplayAnalyticsConsent = true,
                shouldDisplayOnboarding = false,
                shouldDisplayTopicSelection = true,
                isSearchEnabled = true,
                isRecentActivityEnabled = true,
                isTopicsEnabled = true
            )
        )

        val expected = Stack<String>()
        expected.push(HOME_GRAPH_ROUTE)
        expected.push(TOPICS_GRAPH_ROUTE)
        expected.push(ANALYTICS_GRAPH_ROUTE)

        assertEquals(expected, appLaunchNavigation.launchRoutes)
    }

    @Test
    fun `Given analytics consent should be displayed, then return correct launch routes`() {
        val appLaunchNavigation = AppLaunchNavigation(
            AppUiState(
                shouldDisplayAppUnavailable = false,
                shouldDisplayRecommendUpdate = false,
                shouldDisplayAnalyticsConsent = true,
                shouldDisplayOnboarding = false,
                shouldDisplayTopicSelection = false,
                isSearchEnabled = true,
                isRecentActivityEnabled = true,
                isTopicsEnabled = true
            )
        )

        val expected = Stack<String>()
        expected.push(HOME_GRAPH_ROUTE)
        expected.push(ANALYTICS_GRAPH_ROUTE)

        assertEquals(expected, appLaunchNavigation.launchRoutes)
    }

    @Test
    fun `Given onboarding and topic selection should be displayed, then return correct launch routes`() {
        val appLaunchNavigation = AppLaunchNavigation(
            AppUiState(
                shouldDisplayAppUnavailable = false,
                shouldDisplayRecommendUpdate = false,
                shouldDisplayAnalyticsConsent = false,
                shouldDisplayOnboarding = true,
                shouldDisplayTopicSelection = true,
                isSearchEnabled = true,
                isRecentActivityEnabled = true,
                isTopicsEnabled = true
            )
        )

        val expected = Stack<String>()
        expected.push(HOME_GRAPH_ROUTE)
        expected.push(TOPICS_GRAPH_ROUTE)
        expected.push(ONBOARDING_GRAPH_ROUTE)

        assertEquals(expected, appLaunchNavigation.launchRoutes)
    }

    @Test
    fun `Given onboarding should be displayed, then return correct launch routes`() {
        val appLaunchNavigation = AppLaunchNavigation(
            AppUiState(
                shouldDisplayAppUnavailable = false,
                shouldDisplayRecommendUpdate = false,
                shouldDisplayAnalyticsConsent = false,
                shouldDisplayOnboarding = true,
                shouldDisplayTopicSelection = false,
                isSearchEnabled = true,
                isRecentActivityEnabled = true,
                isTopicsEnabled = true
            )
        )

        val expected = Stack<String>()
        expected.push(HOME_GRAPH_ROUTE)
        expected.push(ONBOARDING_GRAPH_ROUTE)

        assertEquals(expected, appLaunchNavigation.launchRoutes)
    }

    @Test
    fun `Given topic selection should be displayed, then return correct launch routes`() {
        val appLaunchNavigation = AppLaunchNavigation(
            AppUiState(
                shouldDisplayAppUnavailable = false,
                shouldDisplayRecommendUpdate = false,
                shouldDisplayAnalyticsConsent = false,
                shouldDisplayOnboarding = false,
                shouldDisplayTopicSelection = true,
                isSearchEnabled = true,
                isRecentActivityEnabled = true,
                isTopicsEnabled = true
            )
        )

        val expected = Stack<String>()
        expected.push(HOME_GRAPH_ROUTE)
        expected.push(TOPICS_GRAPH_ROUTE)

        assertEquals(expected, appLaunchNavigation.launchRoutes)
    }

    @Test
    fun `Given analytics, onboarding and topic selection should not be displayed, then return home as start destination`() {
        val appLaunchNavigation = AppLaunchNavigation(
            AppUiState(
                shouldDisplayAppUnavailable = false,
                shouldDisplayRecommendUpdate = false,
                shouldDisplayAnalyticsConsent = false,
                shouldDisplayOnboarding = false,
                shouldDisplayTopicSelection = false,
                isSearchEnabled = true,
                isRecentActivityEnabled = true,
                isTopicsEnabled = true
            )
        )

        val expected = Stack<String>()
        expected.push(HOME_GRAPH_ROUTE)

        assertEquals(expected, appLaunchNavigation.launchRoutes)
    }
}