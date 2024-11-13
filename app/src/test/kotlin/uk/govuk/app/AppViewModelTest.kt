package uk.govuk.app

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import uk.govuk.app.analytics.Analytics
import uk.govuk.app.config.data.ConfigRepo
import uk.govuk.app.config.data.flags.FlagRepo
import uk.govuk.app.data.AppRepo

@OptIn(ExperimentalCoroutinesApi::class)
class AppViewModelTest {

    private val dispatcher = UnconfinedTestDispatcher()
    private val appRepo = mockk<AppRepo>(relaxed = true)
    private val configRepo = mockk<ConfigRepo>(relaxed = true)
    private val flagRepo = mockk<FlagRepo>(relaxed = true)
    private val analytics = mockk<Analytics>(relaxed = true)

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Given the app is unavailable, When init, then should display app unavailable`() {
        every { flagRepo.isAppAvailable() } returns false

        val viewModel = AppViewModel(appRepo, configRepo, flagRepo, analytics)

        runTest {
            val result = viewModel.uiState.first()
            assertTrue(result!!.shouldDisplayAppUnavailable)
        }
    }

    @Test
    fun `Given the app is available, When init, then should not display app unavailable`() {
        every { flagRepo.isAppAvailable() } returns true

        val viewModel = AppViewModel(appRepo, configRepo, flagRepo, analytics)

        runTest {
            val result = viewModel.uiState.first()
            assertFalse(result!!.shouldDisplayAppUnavailable)
        }
    }

    @Test
    fun `Given recommend update, When init, then should display recommend update`() {
        every { flagRepo.isRecommendUpdate(any()) } returns true

        val viewModel = AppViewModel(appRepo, configRepo, flagRepo, analytics)

        runTest {
            val result = viewModel.uiState.first()
            assertTrue(result!!.shouldDisplayRecommendUpdate)
        }
    }

    @Test
    fun `Given don't recommend update, When init, then should not display recommend update`() {
        every { flagRepo.isRecommendUpdate(any()) } returns false

        val viewModel = AppViewModel(appRepo, configRepo, flagRepo, analytics)

        runTest {
            val result = viewModel.uiState.first()
            assertFalse(result!!.shouldDisplayRecommendUpdate)
        }
    }

    @Test
    fun `Given analytics consent is required, When init, then should display analytics consent`() {
        coEvery { analytics.isAnalyticsConsentRequired() } returns true

        val viewModel = AppViewModel(appRepo, configRepo, flagRepo, analytics)

        runTest {
            val result = viewModel.uiState.first()
            assertTrue(result!!.shouldDisplayAnalyticsConsent)
        }
    }

    @Test
    fun `Given the analytics enabled state is enabled, When init, then should not display analytics consent`() {
        coEvery { analytics.isAnalyticsConsentRequired() } returns false

        val viewModel = AppViewModel(appRepo, configRepo, flagRepo, analytics)

        runTest {
            val result = viewModel.uiState.first()
            assertFalse(result!!.shouldDisplayAnalyticsConsent)
        }
    }

    @Test
    fun `Given the user has previously completed onboarding and onboarding is enabled, When init, then should not display onboarding`() {
        coEvery { appRepo.isOnboardingCompleted() } returns true
        every { flagRepo.isOnboardingEnabled() } returns true

        val viewModel = AppViewModel(appRepo, configRepo, flagRepo, analytics)

        runTest {
            val result = viewModel.uiState.first()
            assertFalse(result!!.shouldDisplayOnboarding)
        }
    }

    @Test
    fun `Given the user has not previously completed onboarding and onboarding is enabled, When init, then should display onboarding`() {
        coEvery { appRepo.isOnboardingCompleted() } returns false
        every { flagRepo.isOnboardingEnabled() } returns true

        val viewModel = AppViewModel(appRepo, configRepo, flagRepo, analytics)

        runTest {
            val result = viewModel.uiState.first()
            assertTrue(result!!.shouldDisplayOnboarding)
        }
    }

    @Test
    fun `Given the user has previously completed onboarding and onboarding is disabled, When init, then should not display onboarding`() {
        coEvery { appRepo.isOnboardingCompleted() } returns true
        every { flagRepo.isOnboardingEnabled() } returns false

        val viewModel = AppViewModel(appRepo, configRepo, flagRepo, analytics)

        runTest {
            val result = viewModel.uiState.first()
            assertFalse(result!!.shouldDisplayOnboarding)
        }
    }

    @Test
    fun `Given the user has not previously completed onboarding and onboarding is disabled, When init, then should not display onboarding`() {
        coEvery { appRepo.isOnboardingCompleted() } returns false
        every { flagRepo.isOnboardingEnabled() } returns false

        val viewModel = AppViewModel(appRepo, configRepo, flagRepo, analytics)

        runTest {
            val result = viewModel.uiState.first()
            assertFalse(result!!.shouldDisplayOnboarding)
        }
    }

    @Test
    fun `Given the user has previously completed topic selection and topics are enabled, When init, then should not display topic selection`() {
        coEvery { appRepo.isTopicSelectionCompleted() } returns true
        every { flagRepo.isTopicsEnabled() } returns true

        val viewModel = AppViewModel(appRepo, configRepo, flagRepo, analytics)

        runTest {
            val result = viewModel.uiState.first()
            assertFalse(result!!.shouldDisplayTopicSelection)
        }
    }

    @Test
    fun `Given the user has not previously completed topic selection and topics are enabled, When init, then should display topic selection`() {
        coEvery { appRepo.isTopicSelectionCompleted() } returns false
        every { flagRepo.isTopicsEnabled() } returns true

        val viewModel = AppViewModel(appRepo, configRepo, flagRepo, analytics)

        runTest {
            val result = viewModel.uiState.first()
            assertTrue(result!!.shouldDisplayTopicSelection)
        }
    }

    @Test
    fun `Given the user has previously completed topic selection and topics are disabled, When init, then should not display topic selection`() {
        coEvery { appRepo.isTopicSelectionCompleted() } returns true
        every { flagRepo.isTopicsEnabled() } returns false

        val viewModel = AppViewModel(appRepo, configRepo, flagRepo, analytics)

        runTest {
            val result = viewModel.uiState.first()
            assertFalse(result!!.shouldDisplayTopicSelection)
        }
    }

    @Test
    fun `Given the user has not previously completed topic selection and topics are disabled, When init, then should not display topic selection`() {
        coEvery { appRepo.isTopicSelectionCompleted() } returns false
        every { flagRepo.isTopicsEnabled() } returns false

        val viewModel = AppViewModel(appRepo, configRepo, flagRepo, analytics)

        runTest {
            val result = viewModel.uiState.first()
            assertFalse(result!!.shouldDisplayTopicSelection)
        }
    }

    @Test
    fun `Given the search feature is enabled, When init, then emit search enabled state`() {
        coEvery { flagRepo.isSearchEnabled() } returns true

        val viewModel = AppViewModel(appRepo, configRepo,  flagRepo, analytics)

        runTest {
            val result = viewModel.uiState.first()
            assertTrue(result!!.isSearchEnabled)
        }
    }

    @Test
    fun `Given the search feature is disabled, When init, then emit search disabled state`() {
        coEvery { flagRepo.isSearchEnabled() } returns false

        val viewModel = AppViewModel(appRepo, configRepo, flagRepo, analytics)

        runTest {
            val result = viewModel.uiState.first()
            assertFalse(result!!.isSearchEnabled)
        }
    }

    @Test
    fun `Given the recent activity feature is enabled, When init, then emit recent activity enabled state`() {
        coEvery { flagRepo.isRecentActivityEnabled() } returns true

        val viewModel = AppViewModel(appRepo, configRepo, flagRepo, analytics)

        runTest {
            val result = viewModel.uiState.first()
            assertTrue(result!!.isRecentActivityEnabled)
        }
    }

    @Test
    fun `Given the recent activity feature is disabled, When init, then emit recent activity disabled state`() {
        coEvery { flagRepo.isRecentActivityEnabled() } returns false

        val viewModel = AppViewModel(appRepo, configRepo, flagRepo, analytics)

        runTest {
            val result = viewModel.uiState.first()
            assertFalse(result!!.isRecentActivityEnabled)
        }
    }

    @Test
    fun `Given the topics feature is enabled, When init, then emit topics enabled state`() {
        coEvery { flagRepo.isTopicsEnabled() } returns true

        val viewModel = AppViewModel(appRepo, configRepo,  flagRepo, analytics)

        runTest {
            val result = viewModel.uiState.first()
            assertTrue(result!!.isTopicsEnabled)
        }
    }

    @Test
    fun `Given the topics feature is disabled, When init, then emit topics disabled state`() {
        coEvery { flagRepo.isTopicsEnabled() } returns false

        val viewModel = AppViewModel(appRepo, configRepo, flagRepo, analytics)

        runTest {
            val result = viewModel.uiState.first()
            assertFalse(result!!.isTopicsEnabled)
        }
    }

    @Test
    fun `When onboarding completed, then call repo onboarding completed`() {
        val viewModel = AppViewModel(appRepo, configRepo, flagRepo, analytics)

        runTest {
            viewModel.onboardingCompleted()

            coVerify { appRepo.onboardingCompleted() }
        }
    }

    @Test
    fun `When topic selection completed, then call repo topic selection completed`() {
        val viewModel = AppViewModel(appRepo, configRepo, flagRepo, analytics)

        runTest {
            viewModel.topicSelectionCompleted()

            coVerify { appRepo.topicSelectionCompleted() }
        }
    }

    @Test
    fun `When tab is clicked, then log analytics`() {
        val viewModel = AppViewModel(appRepo, configRepo, flagRepo, analytics)

        runTest {
            viewModel.onTabClick("text")

            coVerify {
                analytics.tabClick("text")
            }
        }
    }

    @Test
    fun `When widget is clicked, then log analytics`() {
        val viewModel = AppViewModel(appRepo, configRepo, flagRepo, analytics)

        runTest {
            viewModel.onWidgetClick("text")

            coVerify {
                analytics.widgetClick("text")
            }
        }
    }

}