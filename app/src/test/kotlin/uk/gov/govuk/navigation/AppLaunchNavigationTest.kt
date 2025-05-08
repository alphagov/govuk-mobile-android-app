package uk.gov.govuk.navigation

import org.junit.Assert.assertArrayEquals
import org.junit.Test
import uk.gov.govuk.AppUiState
import uk.gov.govuk.analytics.navigation.ANALYTICS_GRAPH_ROUTE
import uk.gov.govuk.home.navigation.HOME_GRAPH_ROUTE
import uk.gov.govuk.login.navigation.LOGIN_GRAPH_ROUTE
import uk.gov.govuk.notifications.navigation.NOTIFICATIONS_GRAPH_ROUTE
import uk.gov.govuk.onboarding.navigation.ONBOARDING_GRAPH_ROUTE
import uk.gov.govuk.topics.navigation.TOPIC_SELECTION_GRAPH_ROUTE
import java.util.ArrayDeque

class AppLaunchNavigationTest {

    @Test
    fun `Given recommend update, analytics consent, onboarding, topic selection and notifications onboarding should be displayed, then return correct launch routes`() {
        val appLaunchNavigation = AppLaunchNavigation(
            AppUiState.Default(
                shouldDisplayRecommendUpdate = true,
                shouldDisplayAnalyticsConsent = true,
                shouldDisplayOnboarding = true,
                shouldDisplayTopicSelection = true,
                shouldDisplayNotificationsOnboarding = true
            )
        )

        val expected = ArrayDeque<String>()
        expected.push(HOME_GRAPH_ROUTE)
        expected.push(NOTIFICATIONS_GRAPH_ROUTE)
        expected.push(TOPIC_SELECTION_GRAPH_ROUTE)
        expected.push(LOGIN_GRAPH_ROUTE)
        expected.push(ONBOARDING_GRAPH_ROUTE)
        expected.push(ANALYTICS_GRAPH_ROUTE)

        assertArrayEquals(expected.toTypedArray(), appLaunchNavigation.launchRoutes.toTypedArray())
    }

    @Test
    fun `Given analytics consent, onboarding and topic selection should be displayed, then return correct launch routes`() {
        val appLaunchNavigation = AppLaunchNavigation(
            AppUiState.Default(
                shouldDisplayAnalyticsConsent = true,
                shouldDisplayOnboarding = true,
                shouldDisplayTopicSelection = true
            )
        )

        val expected = ArrayDeque<String>()
        expected.push(HOME_GRAPH_ROUTE)
        expected.push(TOPIC_SELECTION_GRAPH_ROUTE)
        expected.push(LOGIN_GRAPH_ROUTE)
        expected.push(ONBOARDING_GRAPH_ROUTE)
        expected.push(ANALYTICS_GRAPH_ROUTE)

        assertArrayEquals(expected.toTypedArray(), appLaunchNavigation.launchRoutes.toTypedArray())
    }

    @Test
    fun `Given analytics consent and onboarding should be displayed, then return correct launch routes`() {
        val appLaunchNavigation = AppLaunchNavigation(
            AppUiState.Default(
                shouldDisplayAnalyticsConsent = true,
                shouldDisplayOnboarding = true
            )
        )

        val expected = ArrayDeque<String>()
        expected.push(HOME_GRAPH_ROUTE)
        expected.push(LOGIN_GRAPH_ROUTE)
        expected.push(ONBOARDING_GRAPH_ROUTE)
        expected.push(ANALYTICS_GRAPH_ROUTE)

        assertArrayEquals(expected.toTypedArray(), appLaunchNavigation.launchRoutes.toTypedArray())
    }

    @Test
    fun `Given analytics consent and topic selection should be displayed, then return correct launch routes`() {
        val appLaunchNavigation = AppLaunchNavigation(
            AppUiState.Default(
                shouldDisplayAnalyticsConsent = true,
                shouldDisplayTopicSelection = true
            )
        )

        val expected = ArrayDeque<String>()
        expected.push(HOME_GRAPH_ROUTE)
        expected.push(TOPIC_SELECTION_GRAPH_ROUTE)
        expected.push(LOGIN_GRAPH_ROUTE)
        expected.push(ANALYTICS_GRAPH_ROUTE)

        assertArrayEquals(expected.toTypedArray(), appLaunchNavigation.launchRoutes.toTypedArray())
    }

    @Test
    fun `Given analytics consent should be displayed, then return correct launch routes`() {
        val appLaunchNavigation = AppLaunchNavigation(
            AppUiState.Default(
                shouldDisplayAnalyticsConsent = true
            )
        )

        val expected = ArrayDeque<String>()
        expected.push(HOME_GRAPH_ROUTE)
        expected.push(LOGIN_GRAPH_ROUTE)
        expected.push(ANALYTICS_GRAPH_ROUTE)

        assertArrayEquals(expected.toTypedArray(), appLaunchNavigation.launchRoutes.toTypedArray())
    }

    @Test
    fun `Given onboarding and topic selection should be displayed, then return correct launch routes`() {
        val appLaunchNavigation = AppLaunchNavigation(
            AppUiState.Default(
                shouldDisplayOnboarding = true,
                shouldDisplayTopicSelection = true
            )
        )

        val expected = ArrayDeque<String>()
        expected.push(HOME_GRAPH_ROUTE)
        expected.push(TOPIC_SELECTION_GRAPH_ROUTE)
        expected.push(LOGIN_GRAPH_ROUTE)
        expected.push(ONBOARDING_GRAPH_ROUTE)

        assertArrayEquals(expected.toTypedArray(), appLaunchNavigation.launchRoutes.toTypedArray())
    }

    @Test
    fun `Given onboarding should be displayed, then return correct launch routes`() {
        val appLaunchNavigation = AppLaunchNavigation(
            AppUiState.Default(
                shouldDisplayOnboarding = true,
            )
        )

        val expected = ArrayDeque<String>()
        expected.push(HOME_GRAPH_ROUTE)
        expected.push(LOGIN_GRAPH_ROUTE)
        expected.push(ONBOARDING_GRAPH_ROUTE)

        assertArrayEquals(expected.toTypedArray(), appLaunchNavigation.launchRoutes.toTypedArray())
    }

    @Test
    fun `Given topic selection should be displayed, then return correct launch routes`() {
        val appLaunchNavigation = AppLaunchNavigation(
            AppUiState.Default(
                shouldDisplayTopicSelection = true,
            )
        )

        val expected = ArrayDeque<String>()
        expected.push(HOME_GRAPH_ROUTE)
        expected.push(TOPIC_SELECTION_GRAPH_ROUTE)
        expected.push(LOGIN_GRAPH_ROUTE)

        assertArrayEquals(expected.toTypedArray(), appLaunchNavigation.launchRoutes.toTypedArray())
    }

    @Test
    fun `Given analytics consent and notifications onboarding should be displayed, then return correct launch routes`() {
        val appLaunchNavigation = AppLaunchNavigation(
            AppUiState.Default(
                shouldDisplayAnalyticsConsent = true,
                shouldDisplayNotificationsOnboarding = true
            )
        )

        val expected = ArrayDeque<String>()
        expected.push(HOME_GRAPH_ROUTE)
        expected.push(NOTIFICATIONS_GRAPH_ROUTE)
        expected.push(LOGIN_GRAPH_ROUTE)
        expected.push(ANALYTICS_GRAPH_ROUTE)

        assertArrayEquals(expected.toTypedArray(), appLaunchNavigation.launchRoutes.toTypedArray())
    }

    @Test
    fun `Given topic selection and notifications onboarding should be displayed, then return correct launch routes`() {
        val appLaunchNavigation = AppLaunchNavigation(
            AppUiState.Default(
                shouldDisplayTopicSelection = true,
                shouldDisplayNotificationsOnboarding = true
            )
        )

        val expected = ArrayDeque<String>()
        expected.push(HOME_GRAPH_ROUTE)
        expected.push(NOTIFICATIONS_GRAPH_ROUTE)
        expected.push(TOPIC_SELECTION_GRAPH_ROUTE)
        expected.push(LOGIN_GRAPH_ROUTE)

        assertArrayEquals(expected.toTypedArray(), appLaunchNavigation.launchRoutes.toTypedArray())
    }

    @Test
    fun `Given onboarding and notifications onboarding should be displayed, then return correct launch routes`() {
        val appLaunchNavigation = AppLaunchNavigation(
            AppUiState.Default(
                shouldDisplayOnboarding = true,
                shouldDisplayNotificationsOnboarding = true
            )
        )

        val expected = ArrayDeque<String>()
        expected.push(HOME_GRAPH_ROUTE)
        expected.push(NOTIFICATIONS_GRAPH_ROUTE)
        expected.push(LOGIN_GRAPH_ROUTE)
        expected.push(ONBOARDING_GRAPH_ROUTE)

        assertArrayEquals(expected.toTypedArray(), appLaunchNavigation.launchRoutes.toTypedArray())
    }

    @Test
    fun `Given notifications onboarding should be displayed, then return correct launch routes`() {
        val appLaunchNavigation = AppLaunchNavigation(
            AppUiState.Default(
                shouldDisplayNotificationsOnboarding = true
            )
        )

        val expected = ArrayDeque<String>()
        expected.push(HOME_GRAPH_ROUTE)
        expected.push(NOTIFICATIONS_GRAPH_ROUTE)
        expected.push(LOGIN_GRAPH_ROUTE)

        assertArrayEquals(expected.toTypedArray(), appLaunchNavigation.launchRoutes.toTypedArray())
    }

    @Test
    fun `Given analytics, onboarding and topic selection should not be displayed, then return correct launch routes`() {
        val appLaunchNavigation = AppLaunchNavigation(
            AppUiState.Default()
        )

        val expected = ArrayDeque<String>()
        expected.push(HOME_GRAPH_ROUTE)
        expected.push(LOGIN_GRAPH_ROUTE)

        assertArrayEquals(expected.toTypedArray(), appLaunchNavigation.launchRoutes.toTypedArray())
    }
}