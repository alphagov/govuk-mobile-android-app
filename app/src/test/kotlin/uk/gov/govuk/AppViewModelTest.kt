package uk.gov.govuk

import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import uk.gov.govuk.analytics.AnalyticsClient
import uk.gov.govuk.config.data.ConfigRepo
import uk.gov.govuk.config.data.flags.FlagRepo
import uk.gov.govuk.data.AppRepo
import uk.gov.govuk.data.local.AppDataStore
import uk.gov.govuk.data.model.Result.*
import uk.gov.govuk.home.HomeWidget
import uk.gov.govuk.topics.TopicsFeature

@OptIn(ExperimentalCoroutinesApi::class)
class AppViewModelTest {

    private val dispatcher = UnconfinedTestDispatcher()
    private val appRepo = mockk<AppRepo>(relaxed = true)
    private val configRepo = mockk<ConfigRepo>(relaxed = true)
    private val flagRepo = mockk<FlagRepo>(relaxed = true)
    private val topicsFeature = mockk<TopicsFeature>(relaxed = true)
    private val analyticsClient = mockk<AnalyticsClient>(relaxed = true)
    private val appDataStore = mockk<AppDataStore>(relaxed = true)

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Given there is an error when retrieving the remote config, When init, then should display app unavailable`() {
        coEvery { configRepo.initConfig() } returns Error()

        val viewModel = AppViewModel(appRepo, configRepo, flagRepo, topicsFeature, analyticsClient, appDataStore)

        runTest {
            val result = viewModel.uiState.first()
            assertTrue(result is AppUiState.AppUnavailable)
        }
    }

    @Test
    fun `Given the config signature is invalid, When init, then should display forced update`() {
        coEvery { configRepo.initConfig() } returns InvalidSignature()

        val viewModel = AppViewModel(appRepo, configRepo, flagRepo, topicsFeature, analyticsClient, appDataStore)

        runTest {
            val result = viewModel.uiState.first()
            assertTrue(result is AppUiState.ForcedUpdate)
        }
    }

    @Test
    fun `Given the app is unavailable, When init, then should display app unavailable`() {
        coEvery { configRepo.initConfig() } returns Success(Unit)
        every { flagRepo.isAppAvailable() } returns false

        val viewModel = AppViewModel(appRepo, configRepo, flagRepo, topicsFeature, analyticsClient, appDataStore)

        runTest {
            val result = viewModel.uiState.first()
            assertTrue(result is AppUiState.AppUnavailable)
        }
    }

    @Test
    fun `Given the app is available, When init, then should not display app unavailable`() {
        coEvery { configRepo.initConfig() } returns Success(Unit)
        every { flagRepo.isAppAvailable() } returns true

        val viewModel = AppViewModel(appRepo, configRepo, flagRepo, topicsFeature, analyticsClient, appDataStore)

        runTest {
            val result = viewModel.uiState.first()
            assertTrue(result is AppUiState.Default)
        }
    }

    @Test
    fun `Given forced update, When init, then should display forced update`() {
        coEvery { configRepo.initConfig() } returns Success(Unit)
        every { flagRepo.isAppAvailable() } returns true
        every { flagRepo.isForcedUpdate(any()) } returns true

        val viewModel = AppViewModel(appRepo, configRepo, flagRepo, topicsFeature, analyticsClient, appDataStore)

        runTest {
            val result = viewModel.uiState.first()
            assertTrue(result is AppUiState.ForcedUpdate)
        }
    }

    @Test
    fun `Given don't forced update, When init, then should not display forced update`() {
        coEvery { configRepo.initConfig() } returns Success(Unit)
        every { flagRepo.isAppAvailable() } returns true
        every { flagRepo.isForcedUpdate(any()) } returns false

        val viewModel = AppViewModel(appRepo, configRepo, flagRepo, topicsFeature, analyticsClient, appDataStore)

        runTest {
            val result = viewModel.uiState.first()
            assertTrue(result is AppUiState.Default)
        }
    }

    @Test
    fun `Given recommend update, When init, then should display recommend update`() {
        coEvery { configRepo.initConfig() } returns Success(Unit)
        every { flagRepo.isAppAvailable() } returns true
        every { flagRepo.isRecommendUpdate(any()) } returns true

        val viewModel = AppViewModel(appRepo, configRepo, flagRepo, topicsFeature, analyticsClient, appDataStore)

        runTest {
            val result = viewModel.uiState.first() as AppUiState.Default
            assertTrue(result.shouldDisplayRecommendUpdate)
        }
    }

    @Test
    fun `Given don't recommend update, When init, then should not display recommend update`() {
        coEvery { configRepo.initConfig() } returns Success(Unit)
        every { flagRepo.isAppAvailable() } returns true
        every { flagRepo.isRecommendUpdate(any()) } returns false

        val viewModel = AppViewModel(appRepo, configRepo, flagRepo, topicsFeature, analyticsClient, appDataStore)

        runTest {
            val result = viewModel.uiState.first() as AppUiState.Default
            assertFalse(result.shouldDisplayRecommendUpdate)
        }
    }

    @Test
    fun `Given analytics consent is required, When init, then should display analytics consent`() {
        coEvery { configRepo.initConfig() } returns Success(Unit)
        every { flagRepo.isAppAvailable() } returns true
        coEvery { analyticsClient.isAnalyticsConsentRequired() } returns true

        val viewModel = AppViewModel(appRepo, configRepo, flagRepo, topicsFeature, analyticsClient, appDataStore)

        runTest {
            val result = viewModel.uiState.first() as AppUiState.Default
            assertTrue(result.shouldDisplayAnalyticsConsent)
        }
    }

    @Test
    fun `Given the analytics enabled state is enabled, When init, then should not display analytics consent`() {
        coEvery { configRepo.initConfig() } returns Success(Unit)
        every { flagRepo.isAppAvailable() } returns true
        coEvery { analyticsClient.isAnalyticsConsentRequired() } returns false

        val viewModel = AppViewModel(appRepo, configRepo, flagRepo, topicsFeature, analyticsClient, appDataStore)

        runTest {
            val result = viewModel.uiState.first() as AppUiState.Default
            assertFalse(result.shouldDisplayAnalyticsConsent)
        }
    }

    @Test
    fun `Given the user has previously completed onboarding and onboarding is enabled, When init, then should not display onboarding`() {
        coEvery { configRepo.initConfig() } returns Success(Unit)
        every { flagRepo.isAppAvailable() } returns true
        coEvery { appRepo.isOnboardingCompleted() } returns true
        every { flagRepo.isOnboardingEnabled() } returns true

        val viewModel = AppViewModel(appRepo, configRepo, flagRepo, topicsFeature, analyticsClient, appDataStore)

        runTest {
            val result = viewModel.uiState.first() as AppUiState.Default
            assertFalse(result.shouldDisplayOnboarding)
        }
    }

    @Test
    fun `Given the user has not previously completed onboarding and onboarding is enabled, When init, then should display onboarding`() {
        coEvery { configRepo.initConfig() } returns Success(Unit)
        every { flagRepo.isAppAvailable() } returns true
        coEvery { appRepo.isOnboardingCompleted() } returns false
        every { flagRepo.isOnboardingEnabled() } returns true

        val viewModel = AppViewModel(appRepo, configRepo, flagRepo, topicsFeature, analyticsClient, appDataStore)

        runTest {
            val result = viewModel.uiState.first() as AppUiState.Default
            assertTrue(result.shouldDisplayOnboarding)
        }
    }

    @Test
    fun `Given the user has previously completed onboarding and onboarding is disabled, When init, then should not display onboarding`() {
        coEvery { configRepo.initConfig() } returns Success(Unit)
        every { flagRepo.isAppAvailable() } returns true
        coEvery { appRepo.isOnboardingCompleted() } returns true
        every { flagRepo.isOnboardingEnabled() } returns false

        val viewModel = AppViewModel(appRepo, configRepo, flagRepo, topicsFeature, analyticsClient, appDataStore)

        runTest {
            val result = viewModel.uiState.first() as AppUiState.Default
            assertFalse(result.shouldDisplayOnboarding)
        }
    }

    @Test
    fun `Given the user has not previously completed onboarding and onboarding is disabled, When init, then should not display onboarding`() {
        coEvery { configRepo.initConfig() } returns Success(Unit)
        every { flagRepo.isAppAvailable() } returns true
        coEvery { appRepo.isOnboardingCompleted() } returns false
        every { flagRepo.isOnboardingEnabled() } returns false

        val viewModel = AppViewModel(appRepo, configRepo, flagRepo, topicsFeature, analyticsClient, appDataStore)

        runTest {
            val result = viewModel.uiState.first() as AppUiState.Default
            assertFalse(result.shouldDisplayOnboarding)
        }
    }

    @Test
    fun `Given topics are disabled, When init, then should not display topic selection`() {
        coEvery { configRepo.initConfig() } returns Success(Unit)
        every { flagRepo.isAppAvailable() } returns true
        every { flagRepo.isTopicsEnabled() } returns false

        val viewModel = AppViewModel(appRepo, configRepo, flagRepo, topicsFeature, analyticsClient, appDataStore)

        runTest {
            val result = viewModel.uiState.first() as AppUiState.Default
            assertFalse(result.shouldDisplayTopicSelection)
        }
    }

    @Test
    fun `Given topic selection has been completed, When init, then should not display topic selection`() {
        coEvery { configRepo.initConfig() } returns Success(Unit)
        every { flagRepo.isAppAvailable() } returns true
        every { flagRepo.isTopicsEnabled() } returns true
        coEvery { appRepo.isTopicSelectionCompleted() } returns true

        val viewModel = AppViewModel(appRepo, configRepo, flagRepo, topicsFeature, analyticsClient, appDataStore)

        runTest {
            val result = viewModel.uiState.first() as AppUiState.Default
            assertFalse(result.shouldDisplayTopicSelection)
        }
    }

    @Test
    fun `Given topic init was not successful, When init, then should not display topic selection`() {
        coEvery { configRepo.initConfig() } returns Success(Unit)
        every { flagRepo.isAppAvailable() } returns true
        every { flagRepo.isTopicsEnabled() } returns true
        coEvery { appRepo.isTopicSelectionCompleted() } returns false
        coEvery { topicsFeature.init() } returns false

        val viewModel = AppViewModel(appRepo, configRepo, flagRepo, topicsFeature, analyticsClient, appDataStore)

        runTest {
            val result = viewModel.uiState.first() as AppUiState.Default
            assertFalse(result.shouldDisplayTopicSelection)
        }
    }

    @Test
    fun `Given topics is enabled, selection has not been completed and init was successful, When init, then should display topic selection`() {
        coEvery { configRepo.initConfig() } returns Success(Unit)
        every { flagRepo.isAppAvailable() } returns true
        every { flagRepo.isTopicsEnabled() } returns true
        coEvery { appRepo.isTopicSelectionCompleted() } returns false
        coEvery { topicsFeature.init() } returns true

        val viewModel = AppViewModel(appRepo, configRepo, flagRepo, topicsFeature, analyticsClient, appDataStore)

        runTest {
            val result = viewModel.uiState.first() as AppUiState.Default
            assertTrue(result.shouldDisplayTopicSelection)
        }
    }

    @Test
    fun `Given notifications are enabled, When init, then emit notifications enabled state`() {
        coEvery { configRepo.initConfig() } returns Success(Unit)
        every { flagRepo.isAppAvailable() } returns true
        every { flagRepo.isNotificationsEnabled() } returns true

        val viewModel = AppViewModel(appRepo, configRepo, flagRepo, topicsFeature, analyticsClient, appDataStore)

        runTest {
            val result = viewModel.uiState.first() as AppUiState.Default
            assertTrue(result.shouldDisplayNotificationsOnboarding)
        }
    }

    @Test
    fun `Given notifications are disabled, When init, then emit notifications disabled state`() {
        coEvery { configRepo.initConfig() } returns Success(Unit)
        every { flagRepo.isAppAvailable() } returns true
        every { flagRepo.isNotificationsEnabled() } returns false

        val viewModel = AppViewModel(appRepo, configRepo, flagRepo, topicsFeature, analyticsClient, appDataStore)

        runTest {
            val result = viewModel.uiState.first() as AppUiState.Default
            assertFalse(result.shouldDisplayNotificationsOnboarding)
        }
    }

    @Test
    fun `Given the search feature is enabled, When init, then emit search enabled state`() {
        coEvery { configRepo.initConfig() } returns Success(Unit)
        every { flagRepo.isAppAvailable() } returns true
        coEvery { flagRepo.isSearchEnabled() } returns true

        val viewModel = AppViewModel(appRepo, configRepo, flagRepo, topicsFeature, analyticsClient, appDataStore)

        runTest {
            viewModel.homeWidgets.first()
                ?.let { assertTrue(it.contains(HomeWidget.SEARCH)) }
        }
    }


    @Test
    fun `Given the search feature is disabled, When init, then emit search disabled state`() {
        coEvery { configRepo.initConfig() } returns Success(Unit)
        every { flagRepo.isAppAvailable() } returns true
        coEvery { flagRepo.isSearchEnabled() } returns false

        val viewModel = AppViewModel(appRepo, configRepo, flagRepo, topicsFeature, analyticsClient, appDataStore)

        runTest {
            viewModel.homeWidgets.first()
                ?.let { assertFalse(it.contains(HomeWidget.SEARCH)) }
        }
    }

    @Test
    fun `Given the recent activity feature is enabled, When init, then emit recent activity enabled state`() {
        coEvery { configRepo.initConfig() } returns Success(Unit)
        every { flagRepo.isAppAvailable() } returns true
        coEvery { flagRepo.isRecentActivityEnabled() } returns true

        val viewModel = AppViewModel(appRepo, configRepo, flagRepo, topicsFeature, analyticsClient, appDataStore)

        runTest {
            viewModel.homeWidgets.first()
                ?.let { assertTrue(it.contains(HomeWidget.RECENT_ACTIVITY)) }
        }
    }

    @Test
    fun `Given the recent activity feature is disabled, When init, then emit recent activity disabled state`() {
        coEvery { configRepo.initConfig() } returns Success(Unit)
        every { flagRepo.isAppAvailable() } returns true
        coEvery { flagRepo.isRecentActivityEnabled() } returns false

        val viewModel = AppViewModel(appRepo, configRepo, flagRepo, topicsFeature, analyticsClient, appDataStore)

        runTest {
            viewModel.homeWidgets.first()
                ?.let { assertFalse(it.contains(HomeWidget.RECENT_ACTIVITY)) }
        }
    }

    @Test
    fun `Given the topics feature is enabled, When init, then emit topics enabled state`() {
        coEvery { configRepo.initConfig() } returns Success(Unit)
        every { flagRepo.isAppAvailable() } returns true
        coEvery { flagRepo.isTopicsEnabled() } returns true

        val viewModel = AppViewModel(appRepo, configRepo, flagRepo, topicsFeature, analyticsClient, appDataStore)

        runTest {
            viewModel.homeWidgets.first()
                ?.let { assertTrue(it.contains(HomeWidget.TOPICS)) }
        }
    }

    @Test
    fun `Given the topics feature is disabled, When init, then emit topics disabled state`() {
        coEvery { configRepo.initConfig() } returns Success(Unit)
        every { flagRepo.isAppAvailable() } returns true
        coEvery { flagRepo.isTopicsEnabled() } returns false

        val viewModel = AppViewModel(appRepo, configRepo, flagRepo, topicsFeature, analyticsClient, appDataStore)

        runTest {
            viewModel.homeWidgets.first()
                ?.let { assertFalse(it.contains(HomeWidget.TOPICS)) }
        }
    }

    @Test
    fun `Given the notifications widget is enabled, When init, then emit notifications widget enabled state`() {
        coEvery { configRepo.initConfig() } returns Success(Unit)
        every { flagRepo.isAppAvailable() } returns true
        coEvery { flagRepo.isNotificationsEnabled() } returns true

        val viewModel = AppViewModel(appRepo, configRepo, flagRepo, topicsFeature, analyticsClient, appDataStore)

        runTest {
            viewModel.homeWidgets.first()
                ?.let { assertTrue(it.contains(HomeWidget.NOTIFICATIONS)) }
        }
    }

    @Test
    fun `Given the notifications widget is disabled, When init, then emit notifications widget disabled state`() {
        coEvery { configRepo.initConfig() } returns Success(Unit)
        every { flagRepo.isAppAvailable() } returns true
        coEvery { flagRepo.isNotificationsEnabled() } returns false

        val viewModel = AppViewModel(appRepo, configRepo, flagRepo, topicsFeature, analyticsClient, appDataStore)

        runTest {
            viewModel.homeWidgets.first()
                ?.let { assertFalse(it.contains(HomeWidget.NOTIFICATIONS)) }
        }
    }

    @Test
    fun `Given the notifications widget is not suppressed, When init, then emit notifications widget enabled state`() {
        coEvery { configRepo.initConfig() } returns Success(Unit)
        every { flagRepo.isAppAvailable() } returns true
        coEvery { flagRepo.isNotificationsEnabled() } returns true
        coEvery { appDataStore.isHomeWidgetInSuppressedList(HomeWidget.NOTIFICATIONS) } returns false

        val viewModel = AppViewModel(appRepo, configRepo, flagRepo, topicsFeature, analyticsClient, appDataStore)

        runTest {
            viewModel.homeWidgets.first()
                ?.let { assertTrue(it.contains(HomeWidget.NOTIFICATIONS)) }
        }
    }

    @Test
    fun `Given the notifications widget is suppressed, When init, then emit notifications widget disabled state`() {
        coEvery { configRepo.initConfig() } returns Success(Unit)
        every { flagRepo.isAppAvailable() } returns true
        coEvery { flagRepo.isNotificationsEnabled() } returns true
        coEvery { appDataStore.isHomeWidgetInSuppressedList(HomeWidget.NOTIFICATIONS) } returns true

        val viewModel = AppViewModel(appRepo, configRepo, flagRepo, topicsFeature, analyticsClient, appDataStore)

        runTest {
            viewModel.homeWidgets.first()
                ?.let { assertFalse(it.contains(HomeWidget.NOTIFICATIONS)) }
        }
    }

    @Test
    fun `When onboarding completed, then call repo onboarding completed`() {
        coEvery { configRepo.initConfig() } returns Success(Unit)
        every { flagRepo.isAppAvailable() } returns true

        val viewModel = AppViewModel(appRepo, configRepo, flagRepo, topicsFeature, analyticsClient, appDataStore)

        runTest {
            viewModel.onboardingCompleted()

            coVerify { appRepo.onboardingCompleted() }
        }
    }

    @Test
    fun `When topic selection completed, then call repo topic selection completed`() {
        coEvery { configRepo.initConfig() } returns Success(Unit)
        every { flagRepo.isAppAvailable() } returns true

        val viewModel = AppViewModel(appRepo, configRepo, flagRepo, topicsFeature, analyticsClient, appDataStore)

        runTest {
            viewModel.topicSelectionCompleted()

            coVerify { appRepo.topicSelectionCompleted() }
        }
    }

    @Test
    fun `When tab is clicked, then log analytics`() {
        coEvery { configRepo.initConfig() } returns Success(Unit)
        every { flagRepo.isAppAvailable() } returns true

        val viewModel = AppViewModel(appRepo, configRepo, flagRepo, topicsFeature, analyticsClient, appDataStore)

        runTest {
            viewModel.onTabClick("text")

            coVerify {
                analyticsClient.tabClick("text")
            }
        }
    }

    @Test
    fun `When an external widget is clicked, then log analytics`() {
        coEvery { configRepo.initConfig() } returns Success(Unit)
        every { flagRepo.isAppAvailable() } returns true

        val viewModel = AppViewModel(appRepo, configRepo, flagRepo, topicsFeature, analyticsClient, appDataStore)

        runTest {
            viewModel.onWidgetClick("text", true, "section")

            coVerify {
                analyticsClient.widgetClick("text", true, "section")
            }
        }
    }

    @Test
    fun `When an internal widget is clicked, then log analytics`() {
        coEvery { configRepo.initConfig() } returns Success(Unit)
        every { flagRepo.isAppAvailable() } returns true

        val viewModel = AppViewModel(appRepo, configRepo, flagRepo, topicsFeature, analyticsClient, appDataStore)

        runTest {
            viewModel.onWidgetClick("text", false, "section")

            coVerify(exactly = 1) {
                analyticsClient.widgetClick("text", false, "section")
            }
        }
    }

    @Test
    fun `When an suppress widget is clicked, then log analytics`() {
        coEvery { configRepo.initConfig() } returns Success(Unit)
        every { flagRepo.isAppAvailable() } returns true

        val viewModel = AppViewModel(appRepo, configRepo, flagRepo, topicsFeature, analyticsClient, appDataStore)

        runTest {
            viewModel.onSuppressWidgetClick("text", "section", HomeWidget.NOTIFICATIONS)

            coVerify {
                appDataStore.addHomeWidgetToSuppressedList(HomeWidget.NOTIFICATIONS)
                viewModel.updateHomeWidgets()
                analyticsClient.suppressWidgetClick("text", "section")
            }
        }
    }

    @Test
    fun `Given a deeplink is received, When the app has the deeplink, then log analytics`() {
        coEvery { configRepo.initConfig() } returns Success(Unit)
        every { flagRepo.isAppAvailable() } returns true

        val viewModel = AppViewModel(appRepo, configRepo, flagRepo, topicsFeature, analyticsClient, appDataStore)

        runTest {
            viewModel.onDeeplinkReceived(true, "url")

            coVerify {
                analyticsClient.deeplinkEvent(true, "url")
            }
        }
    }

    @Test
    fun `Given a deeplink is received, When the app doesn't have the deeplink, then log analytics`() {
        coEvery { configRepo.initConfig() } returns Success(Unit)
        every { flagRepo.isAppAvailable() } returns true

        val viewModel = AppViewModel(appRepo, configRepo, flagRepo, topicsFeature, analyticsClient, appDataStore)

        runTest {
            viewModel.onDeeplinkReceived(false, "url")

            coVerify {
                analyticsClient.deeplinkEvent(false, "url")
            }
        }
    }

    @Test
    fun `Given the user tries again, then emit loading state and fetch config`() {
        coEvery { configRepo.initConfig() } returns Success(Unit)

        runTest {
            val viewModel = AppViewModel(appRepo, configRepo, flagRepo, topicsFeature, analyticsClient, appDataStore)

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
}