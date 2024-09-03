package uk.govuk.app

import io.mockk.coEvery
import io.mockk.coVerify
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
import uk.govuk.app.config.flags.ReleaseFlagsService

@OptIn(ExperimentalCoroutinesApi::class)
class AppViewModelTest {

    private val dispatcher = UnconfinedTestDispatcher()
    private val onboardingRepo = mockk<AppRepo>(relaxed = true)
    private val releaseFlagsService = mockk<ReleaseFlagsService>(relaxed = true)
    private val analytics = mockk<Analytics>(relaxed = true)

    // Todo - probably want to extract this into a test rule for re-use at some point
    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Given the user has previously completed onboarding, When init, then return onboarding not required state`() {
        coEvery { onboardingRepo.isOnboardingCompleted() } returns true

        val viewModel = AppViewModel(onboardingRepo, releaseFlagsService, analytics)

        runTest {
            val result = viewModel.uiState.first()
            assertFalse(result!!.isOnboardingRequired)
        }
    }

    @Test
    fun `Given the user has not previously completed onboarding, When init, then return onboarding required state`() {
        coEvery { onboardingRepo.isOnboardingCompleted() } returns false

        val viewModel = AppViewModel(onboardingRepo, releaseFlagsService, analytics)

        runTest {
            val result = viewModel.uiState.first()
            assertTrue(result!!.isOnboardingRequired)
        }
    }

    @Test
    fun `Given the search feature is enabled, When init, then return search enabled state`() {
        coEvery { releaseFlagsService.isSearchEnabled() } returns true

        val viewModel = AppViewModel(onboardingRepo, releaseFlagsService, analytics)

        runTest {
            val result = viewModel.uiState.first()
            assertTrue(result!!.isSearchEnabled)
        }
    }

    @Test
    fun `Given the search feature is disabled, When init, then return search disabled state`() {
        coEvery { releaseFlagsService.isSearchEnabled() } returns false

        val viewModel = AppViewModel(onboardingRepo, releaseFlagsService, analytics)

        runTest {
            val result = viewModel.uiState.first()
            assertFalse(result!!.isSearchEnabled)
        }
    }

    @Test
    fun `When onboarding completed, then call repo onboarding completed`() {
        val viewModel = AppViewModel(onboardingRepo, releaseFlagsService, analytics)

        runTest {
            viewModel.onboardingCompleted()

            coVerify { onboardingRepo.onboardingCompleted() }
        }
    }

    @Test
    fun `When tab is clicked, then log analytics`() {
        val viewModel = AppViewModel(onboardingRepo, releaseFlagsService, analytics)

        runTest {
            viewModel.onTabClick("text")

            coVerify {
                analytics.tabClick("text")
            }
        }
    }

    @Test
    fun `When widget is clicked, then log analytics`() {
        val viewModel = AppViewModel(onboardingRepo, releaseFlagsService, analytics)

        runTest {
            viewModel.onWidgetClick(
                screenName = "screenName",
                cta = "cta"
            )

            coVerify {
                analytics.widgetClick(
                    screenName = "screenName",
                    cta = "cta"
                )
            }
        }
    }

}