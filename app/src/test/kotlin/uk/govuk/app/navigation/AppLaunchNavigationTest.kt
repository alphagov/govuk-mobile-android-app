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
    fun `Given analytics consent, onboarding and topic selection should be displayed, then return correct launch routes`() {
        val appLaunchNavigation = AppLaunchNavigation(
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