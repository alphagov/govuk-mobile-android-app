package uk.gov.govuk

import androidx.navigation.NavController
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import uk.gov.govuk.analytics.AnalyticsClient
import uk.gov.govuk.config.data.ConfigRepo
import uk.gov.govuk.config.data.flags.FlagRepo
import uk.gov.govuk.data.AppRepo
import uk.gov.govuk.data.model.Result.Error
import uk.gov.govuk.data.model.Result.InvalidSignature
import uk.gov.govuk.data.model.Result.Success
import uk.gov.govuk.navigation.AppLaunchNavigation
import uk.gov.govuk.search.SearchFeature
import uk.gov.govuk.topics.TopicsFeature
import uk.gov.govuk.ui.model.HomeWidget
import uk.gov.govuk.visited.Visited
import uk.govuk.app.local.LocalFeature

@OptIn(ExperimentalCoroutinesApi::class)
class AppViewModelTest {

    private val dispatcher = UnconfinedTestDispatcher()
    private val appRepo = mockk<AppRepo>(relaxed = true)
    private val configRepo = mockk<ConfigRepo>(relaxed = true)
    private val flagRepo = mockk<FlagRepo>(relaxed = true)
    private val topicsFeature = mockk<TopicsFeature>(relaxed = true)
    private val localFeature = mockk<LocalFeature>(relaxed = true)
    private val searchFeature = mockk<SearchFeature>(relaxed = true)
    private val visited = mockk<Visited>(relaxed = true)
    private val analyticsClient = mockk<AnalyticsClient>(relaxed = true)
    private val appLaunchNavigation = mockk<AppLaunchNavigation>(relaxed = true)
    private val navController = mockk<NavController>(relaxed = true)

    private lateinit var viewModel: AppViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)

        // Default setup state, overridden by individual tests if/when required
        coEvery { configRepo.initConfig() } returns Success(Unit)
        every { localFeature.hasLocalAuthority() } returns flowOf(false)
        every { appRepo.suppressedHomeWidgets } returns flowOf(emptySet())
        every { flagRepo.isAppAvailable() } returns true

        viewModel = AppViewModel(appRepo, configRepo, flagRepo, topicsFeature, localFeature,
            searchFeature, visited, analyticsClient, appLaunchNavigation)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Given there is an error when retrieving the remote config, When init, then should display app unavailable`() {
        coEvery { configRepo.initConfig() } returns Error()

        val viewModel = AppViewModel(appRepo, configRepo, flagRepo, topicsFeature, localFeature,
            searchFeature, visited, analyticsClient, appLaunchNavigation)

        runTest {
            val result = viewModel.uiState.first()
            assertTrue(result is AppUiState.AppUnavailable)
        }
    }

    @Test
    fun `Given the config signature is invalid, When init, then should display forced update`() {
        coEvery { configRepo.initConfig() } returns InvalidSignature()

        val viewModel = AppViewModel(appRepo, configRepo, flagRepo, topicsFeature, localFeature,
            searchFeature, visited, analyticsClient, appLaunchNavigation)

        runTest {
            val result = viewModel.uiState.first()
            assertTrue(result is AppUiState.ForcedUpdate)
        }
    }

    @Test
    fun `Given the app is unavailable, When init, then should display app unavailable`() {
        every { flagRepo.isAppAvailable() } returns false

        val viewModel = AppViewModel(appRepo, configRepo, flagRepo, topicsFeature, localFeature,
            searchFeature, visited, analyticsClient, appLaunchNavigation)

        runTest {
            val result = viewModel.uiState.first()
            assertTrue(result is AppUiState.AppUnavailable)
        }
    }

    @Test
    fun `Given the app is available, When init, then should not display app unavailable`() {
        runTest {
            val result = viewModel.uiState.first()
            assertTrue(result is AppUiState.Default)
        }
    }

    @Test
    fun `Given forced update, When init, then should display forced update`() {
        every { flagRepo.isForcedUpdate(any()) } returns true

        val viewModel = AppViewModel(appRepo, configRepo, flagRepo, topicsFeature, localFeature,
            searchFeature, visited, analyticsClient, appLaunchNavigation)

        runTest {
            val result = viewModel.uiState.first()
            assertTrue(result is AppUiState.ForcedUpdate)
        }
    }

    @Test
    fun `Given don't forced update, When init, then should not display forced update`() {
        every { flagRepo.isForcedUpdate(any()) } returns false

        val viewModel = AppViewModel(appRepo, configRepo, flagRepo, topicsFeature, localFeature,
            searchFeature, visited, analyticsClient, appLaunchNavigation)

        runTest {
            val result = viewModel.uiState.first()
            assertTrue(result is AppUiState.Default)
        }
    }

    @Test
    fun `Given recommend update, When init, then should display recommend update`() {
        every { flagRepo.isRecommendUpdate(any()) } returns true

        val viewModel = AppViewModel(appRepo, configRepo, flagRepo, topicsFeature, localFeature,
            searchFeature, visited, analyticsClient, appLaunchNavigation)

        runTest {
            val result = viewModel.uiState.first() as AppUiState.Default
            assertTrue(result.shouldDisplayRecommendUpdate)
        }
    }

    @Test
    fun `Given don't recommend update, When init, then should not display recommend update`() {
        every { flagRepo.isRecommendUpdate(any()) } returns false

        val viewModel = AppViewModel(appRepo, configRepo, flagRepo, topicsFeature, localFeature,
            searchFeature, visited, analyticsClient, appLaunchNavigation)

        runTest {
            val result = viewModel.uiState.first() as AppUiState.Default
            assertFalse(result.shouldDisplayRecommendUpdate)
        }
    }

    @Test
    fun `Given analytics consent is required, When init, then should display analytics consent`() {
        coEvery { analyticsClient.isAnalyticsConsentRequired() } returns true

        val viewModel = AppViewModel(appRepo, configRepo, flagRepo, topicsFeature, localFeature,
            searchFeature, visited, analyticsClient, appLaunchNavigation)

        runTest {
            val result = viewModel.uiState.first() as AppUiState.Default
            assertTrue(result.shouldDisplayAnalyticsConsent)
        }
    }

    @Test
    fun `Given the analytics enabled state is enabled, When init, then should not display analytics consent`() {
        coEvery { analyticsClient.isAnalyticsConsentRequired() } returns false

        val viewModel = AppViewModel(appRepo, configRepo, flagRepo, topicsFeature, localFeature,
            searchFeature, visited, analyticsClient, appLaunchNavigation)

        runTest {
            val result = viewModel.uiState.first() as AppUiState.Default
            assertFalse(result.shouldDisplayAnalyticsConsent)
        }
    }

    @Test
    fun `Given the user has previously completed onboarding and onboarding is enabled, When init, then should not display onboarding`() {
        coEvery { appRepo.isOnboardingCompleted() } returns true
        every { flagRepo.isOnboardingEnabled() } returns true

        val viewModel = AppViewModel(appRepo, configRepo, flagRepo, topicsFeature, localFeature,
            searchFeature, visited, analyticsClient, appLaunchNavigation)

        runTest {
            val result = viewModel.uiState.first() as AppUiState.Default
            assertFalse(result.shouldDisplayOnboarding)
        }
    }

    @Test
    fun `Given the user has not previously completed onboarding and onboarding is enabled, When init, then should display onboarding`() {
        coEvery { appRepo.isOnboardingCompleted() } returns false
        every { flagRepo.isOnboardingEnabled() } returns true

        val viewModel = AppViewModel(appRepo, configRepo, flagRepo, topicsFeature, localFeature,
            searchFeature, visited, analyticsClient, appLaunchNavigation)

        runTest {
            val result = viewModel.uiState.first() as AppUiState.Default
            assertTrue(result.shouldDisplayOnboarding)
        }
    }

    @Test
    fun `Given the user has previously completed onboarding and onboarding is disabled, When init, then should not display onboarding`() {
        coEvery { appRepo.isOnboardingCompleted() } returns true
        every { flagRepo.isOnboardingEnabled() } returns false

        val viewModel = AppViewModel(appRepo, configRepo, flagRepo, topicsFeature, localFeature,
            searchFeature, visited, analyticsClient, appLaunchNavigation)

        runTest {
            val result = viewModel.uiState.first() as AppUiState.Default
            assertFalse(result.shouldDisplayOnboarding)
        }
    }

    @Test
    fun `Given the user has not previously completed onboarding and onboarding is disabled, When init, then should not display onboarding`() {
        coEvery { appRepo.isOnboardingCompleted() } returns false
        every { flagRepo.isOnboardingEnabled() } returns false

        val viewModel = AppViewModel(appRepo, configRepo, flagRepo, topicsFeature, localFeature,
            searchFeature, visited, analyticsClient, appLaunchNavigation)

        runTest {
            val result = viewModel.uiState.first() as AppUiState.Default
            assertFalse(result.shouldDisplayOnboarding)
        }
    }

    @Test
    fun `Given topics are disabled, When init, then should not display topic selection`() {
        every { flagRepo.isTopicsEnabled() } returns false

        val viewModel = AppViewModel(appRepo, configRepo, flagRepo, topicsFeature, localFeature,
            searchFeature, visited, analyticsClient, appLaunchNavigation)

        runTest {
            val result = viewModel.uiState.first() as AppUiState.Default
            assertFalse(result.shouldDisplayTopicSelection)
        }
    }

    @Test
    fun `Given topic selection has been completed, When init, then should not display topic selection`() {
        every { flagRepo.isTopicsEnabled() } returns true
        coEvery { appRepo.isTopicSelectionCompleted() } returns true

        val viewModel = AppViewModel(appRepo, configRepo, flagRepo, topicsFeature, localFeature,
            searchFeature, visited, analyticsClient, appLaunchNavigation)

        runTest {
            val result = viewModel.uiState.first() as AppUiState.Default
            assertFalse(result.shouldDisplayTopicSelection)
        }
    }

    @Test
    fun `Given topic init was not successful, When init, then should not display topic selection`() {
        every { flagRepo.isTopicsEnabled() } returns true
        coEvery { appRepo.isTopicSelectionCompleted() } returns false
        coEvery { topicsFeature.init() } returns false

        val viewModel = AppViewModel(appRepo, configRepo, flagRepo, topicsFeature, localFeature,
            searchFeature, visited, analyticsClient, appLaunchNavigation)

        runTest {
            val result = viewModel.uiState.first() as AppUiState.Default
            assertFalse(result.shouldDisplayTopicSelection)
        }
    }

    @Test
    fun `Given topics is enabled, selection has not been completed and init was successful, When init, then should display topic selection`() {
        every { flagRepo.isTopicsEnabled() } returns true
        coEvery { appRepo.isTopicSelectionCompleted() } returns false
        coEvery { topicsFeature.init() } returns true

        val viewModel = AppViewModel(appRepo, configRepo, flagRepo, topicsFeature, localFeature,
            searchFeature, visited, analyticsClient, appLaunchNavigation)

        runTest {
            val result = viewModel.uiState.first() as AppUiState.Default
            assertTrue(result.shouldDisplayTopicSelection)
        }
    }

    @Test
    fun `Given notifications are enabled, When init, then emit notifications enabled state`() {
        every { flagRepo.isNotificationsEnabled() } returns true

        val viewModel = AppViewModel(appRepo, configRepo, flagRepo, topicsFeature, localFeature,
            searchFeature, visited, analyticsClient, appLaunchNavigation)

        runTest {
            val result = viewModel.uiState.first() as AppUiState.Default
            assertTrue(result.shouldDisplayNotificationsOnboarding)
        }
    }

    @Test
    fun `Given notifications are disabled, When init, then emit notifications disabled state`() {
        every { flagRepo.isNotificationsEnabled() } returns false

        val viewModel = AppViewModel(appRepo, configRepo, flagRepo, topicsFeature, localFeature,
            searchFeature, visited, analyticsClient, appLaunchNavigation)

        runTest {
            val result = viewModel.uiState.first() as AppUiState.Default
            assertFalse(result.shouldDisplayNotificationsOnboarding)
        }
    }

    @Test
    fun `Given the search feature is enabled, When init, then emit search enabled state`() {
        coEvery { flagRepo.isSearchEnabled() } returns true

        val viewModel = AppViewModel(appRepo, configRepo, flagRepo, topicsFeature, localFeature,
            searchFeature, visited, analyticsClient, appLaunchNavigation)

        runTest {
            viewModel.homeWidgets.first()
                ?.let { assertTrue(it.contains(HomeWidget.SEARCH)) }
        }
    }


    @Test
    fun `Given the search feature is disabled, When init, then emit search disabled state`() {
        coEvery { flagRepo.isSearchEnabled() } returns false

        val viewModel = AppViewModel(appRepo, configRepo, flagRepo, topicsFeature, localFeature,
            searchFeature, visited, analyticsClient, appLaunchNavigation)

        runTest {
            viewModel.homeWidgets.first()
                ?.let { assertFalse(it.contains(HomeWidget.SEARCH)) }
        }
    }

    @Test
    fun `Given the recent activity feature is enabled, When init, then emit recent activity enabled state`() {
        coEvery { flagRepo.isRecentActivityEnabled() } returns true

        val viewModel = AppViewModel(appRepo, configRepo, flagRepo, topicsFeature, localFeature,
            searchFeature, visited, analyticsClient, appLaunchNavigation)

        runTest {
            viewModel.homeWidgets.first()
                ?.let { assertTrue(it.contains(HomeWidget.RECENT_ACTIVITY)) }
        }
    }

    @Test
    fun `Given the recent activity feature is disabled, When init, then emit recent activity disabled state`() {
        coEvery { flagRepo.isRecentActivityEnabled() } returns false

        val viewModel = AppViewModel(appRepo, configRepo, flagRepo, topicsFeature, localFeature,
            searchFeature, visited, analyticsClient, appLaunchNavigation)

        runTest {
            viewModel.homeWidgets.first()
                ?.let { assertFalse(it.contains(HomeWidget.RECENT_ACTIVITY)) }
        }
    }

    @Test
    fun `Given the topics feature is enabled, When init, then emit topics enabled state`() {
        coEvery { flagRepo.isTopicsEnabled() } returns true

        val viewModel = AppViewModel(appRepo, configRepo, flagRepo, topicsFeature, localFeature,
            searchFeature, visited, analyticsClient, appLaunchNavigation)

        runTest {
            viewModel.homeWidgets.first()
                ?.let { assertTrue(it.contains(HomeWidget.TOPICS)) }
        }
    }

    @Test
    fun `Given the topics feature is disabled, When init, then emit topics disabled state`() {
        coEvery { flagRepo.isTopicsEnabled() } returns false

        val viewModel = AppViewModel(appRepo, configRepo, flagRepo, topicsFeature, localFeature,
            searchFeature, visited, analyticsClient, appLaunchNavigation)

        runTest {
            viewModel.homeWidgets.first()
                ?.let { assertFalse(it.contains(HomeWidget.TOPICS)) }
        }
    }

    @Test
    fun `Given the notifications widget is enabled, When init, then emit notifications widget enabled state`() {
        coEvery { flagRepo.isNotificationsEnabled() } returns true

        val viewModel = AppViewModel(appRepo, configRepo, flagRepo, topicsFeature, localFeature,
            searchFeature, visited, analyticsClient, appLaunchNavigation)

        runTest {
            viewModel.homeWidgets.first()
                ?.let { assertTrue(it.contains(HomeWidget.NOTIFICATIONS)) }
        }
    }

    @Test
    fun `Given the notifications widget is disabled, When init, then emit notifications widget disabled state`() {
        coEvery { flagRepo.isNotificationsEnabled() } returns false

        val viewModel = AppViewModel(appRepo, configRepo, flagRepo, topicsFeature, localFeature,
            searchFeature, visited, analyticsClient, appLaunchNavigation)

        runTest {
            viewModel.homeWidgets.first()
                ?.let { assertFalse(it.contains(HomeWidget.NOTIFICATIONS)) }
        }
    }

    @Test
    fun `Given the notifications widget is not suppressed, When init, then emit notifications widget enabled state`() {
        coEvery { flagRepo.isNotificationsEnabled() } returns true

        val viewModel = AppViewModel(appRepo, configRepo, flagRepo, topicsFeature, localFeature,
            searchFeature, visited, analyticsClient, appLaunchNavigation)

        runTest {
            viewModel.homeWidgets.first()
                ?.let { assertTrue(it.contains(HomeWidget.NOTIFICATIONS)) }
        }
    }

    @Test
    fun `Given the notifications widget is suppressed, When init, then emit notifications widget disabled state`() {
        every { appRepo.suppressedHomeWidgets } returns flowOf(setOf(HomeWidget.NOTIFICATIONS.serializedName))
        coEvery { flagRepo.isNotificationsEnabled() } returns true

        val viewModel = AppViewModel(appRepo, configRepo, flagRepo, topicsFeature, localFeature,
            searchFeature, visited, analyticsClient, appLaunchNavigation)

        runTest {
            viewModel.homeWidgets.first()
                ?.let { assertFalse(it.contains(HomeWidget.NOTIFICATIONS)) }
        }
    }

    @Test
    fun `When onboarding completed, then call repo onboarding completed`() {
        runTest {
            viewModel.onboardingCompleted()

            coVerify { appRepo.onboardingCompleted() }
        }
    }

    @Test
    fun `When topic selection completed, then call repo topic selection completed`() {
        runTest {
            viewModel.topicSelectionCompleted()

            coVerify { appRepo.topicSelectionCompleted() }
        }
    }

    @Test
    fun `When tab is clicked, then log analytics`() {
        runTest {
            viewModel.onTabClick("text")

            coVerify {
                analyticsClient.tabClick("text")
            }
        }
    }

    @Test
    fun `When an external widget is clicked, then log analytics`() {
        runTest {
            viewModel.onWidgetClick("text", "url", true, "section")

            coVerify {
                analyticsClient.widgetClick("text", "url", true, "section")
            }
        }
    }

    @Test
    fun `When an internal widget is clicked, then log analytics`() {
        runTest {
            viewModel.onWidgetClick("text", "url", false, "section")

            coVerify(exactly = 1) {
                analyticsClient.widgetClick("text", "url", false, "section")
            }
        }
    }

    @Test
    fun `When an suppress widget is clicked, then log analytics`() {
        runTest {
            viewModel.onSuppressWidgetClick("text", "section", HomeWidget.NOTIFICATIONS)

            coVerify {
                appRepo.suppressHomeWidget(HomeWidget.NOTIFICATIONS)
                analyticsClient.suppressWidgetClick("text", "section")
            }
        }
    }

    @Test
    fun `Given a deep link is received, When the app has the deep link, then log analytics`() {
        runTest {
            viewModel.onDeepLinkReceived(true, "url")

            coVerify {
                analyticsClient.deepLinkEvent(true, "url")
            }
        }
    }

    @Test
    fun `Given a deep link is received, When the app doesn't have the deep link, then log analytics`() {
        runTest {
            viewModel.onDeepLinkReceived(false, "url")

            coVerify {
                analyticsClient.deepLinkEvent(false, "url")
            }
        }
    }

    @Test
    fun `Given the user tries again, then emit loading state and fetch config`() {
        runTest {
            val uiStates = mutableListOf<AppUiState?>()
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                viewModel.uiState.toList(uiStates)
            }

            clearMocks(configRepo)
            coEvery { configRepo.initConfig() } returns Success(Unit)

            viewModel.onTryAgain()
            assertTrue(uiStates[1] is AppUiState.Loading)
            coVerify {
                configRepo.initConfig()
            }
        }
    }

    @Test
    fun `Given the local feature is disabled, When init, then emit local disabled state`() {
        coEvery { flagRepo.isLocalServicesEnabled() } returns false

        val viewModel = AppViewModel(appRepo, configRepo, flagRepo, topicsFeature, localFeature,
            searchFeature, visited, analyticsClient, appLaunchNavigation)

        runTest {
            viewModel.homeWidgets.first()
                ?.let { assertFalse(it.contains(HomeWidget.LOCAL)) }
        }
    }

    @Test
    fun `Given the local feature is enabled and a local authority is not selected, When init, then emit local enabled state`() {
        coEvery { flagRepo.isLocalServicesEnabled() } returns true
        coEvery { flagRepo.isTopicsEnabled() } returns true

        val viewModel = AppViewModel(appRepo, configRepo, flagRepo, topicsFeature, localFeature,
            searchFeature, visited, analyticsClient, appLaunchNavigation)

        runTest {
            val homeWidgets = viewModel.homeWidgets.value!!
            assertEquals(HomeWidget.LOCAL, homeWidgets.first())
            assertNotEquals(HomeWidget.LOCAL, homeWidgets.last())
        }
    }

    @Test
    fun `Given the local feature is enabled and a local authority is selected, When init, then emit local enabled state`() {
        coEvery { flagRepo.isLocalServicesEnabled() } returns true
        coEvery { flagRepo.isTopicsEnabled() } returns true
        every { localFeature.hasLocalAuthority() } returns flowOf(true)

        val viewModel = AppViewModel(appRepo, configRepo, flagRepo, topicsFeature, localFeature,
            searchFeature, visited, analyticsClient, appLaunchNavigation)

        runTest {
            val homeWidgets = viewModel.homeWidgets.value!!
            assertNotEquals(HomeWidget.LOCAL, homeWidgets.first())
            assertEquals(HomeWidget.LOCAL, homeWidgets.last())
        }
    }

    @Test
    fun `Given a new user or the same user has logged in, When on login, then navigate to next nav destination`() {
        runTest {
            viewModel.onLogin(false, navController)

            coVerify(exactly = 0) {
                appRepo.clear()
                topicsFeature.clear()
                localFeature.clear()
                searchFeature.clear()
                visited.clear()
                appLaunchNavigation.onDifferentUserLogin(any())
            }

            coVerify {
                appLaunchNavigation.onNext(navController)
            }
        }
    }

    @Test
    fun `Given a different user has logged in, When on login, then clear data, reconfigure nav and navigate to next destination`() {
        runTest {
            viewModel.onLogin(true, navController)

            coVerify {
                appRepo.clear()
                topicsFeature.clear()
                localFeature.clear()
                searchFeature.clear()
                visited.clear()
                appLaunchNavigation.onDifferentUserLogin(any())
                appLaunchNavigation.onNext(navController)
            }
        }
    }

    @Test
    fun `Given a user has signed out, When on sign out, then call app launch nav`() {
        viewModel.onSignOut()

        verify {
            appLaunchNavigation.onSignOut()
        }
    }
}
