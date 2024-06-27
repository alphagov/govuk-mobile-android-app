package uk.govuk.app.launch

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
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AppLaunchViewModelTest {

    private val dispatcher = UnconfinedTestDispatcher()
    private val onboardingRepo = mockk<AppLaunchRepo>(relaxed = true)

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
    fun `Given the user has previously completed onboarding, When init, then return onboarding completed state`() {
        coEvery { onboardingRepo.isOnboardingCompleted() } returns true

        val viewModel = AppLaunchViewModel(onboardingRepo)

        runTest {
            val result = viewModel.appLaunchState.first()
            assertEquals(AppLaunchState.ONBOARDING_COMPLETED, result)
        }
    }

    @Test
    fun `Given the user has not previously completed onboarding, When init, then return onboarding required state`() {
        coEvery { onboardingRepo.isOnboardingCompleted() } returns false

        val viewModel = AppLaunchViewModel(onboardingRepo)

        runTest {
            val result = viewModel.appLaunchState.first()
            assertEquals(AppLaunchState.ONBOARDING_REQUIRED, result)
        }
    }

    @Test
    fun `When onboarding completed, then call repo onboarding completed`() {
        val viewModel = AppLaunchViewModel(onboardingRepo)

        runTest {
            viewModel.onboardingCompleted()

            coVerify { onboardingRepo.onboardingCompleted() }
        }
    }

}