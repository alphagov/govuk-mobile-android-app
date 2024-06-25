package uk.govuk.app.onboarding

import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class OnboardingViewModelTest {

    private val dispatcher = UnconfinedTestDispatcher()
    private val onboardingRepo = mockk<OnboardingRepo>(relaxed = true)

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
    fun `When on done, then call repo onboarding completed`() {
        val viewModel = OnboardingViewModel(onboardingRepo)

        runTest {
            viewModel.onDone()

            coVerify { onboardingRepo.onboardingCompleted() }
        }
    }

    @Test
    fun `When on skip, then call repo onboarding completed`() {
        val viewModel = OnboardingViewModel(onboardingRepo)

        runTest {
            viewModel.onSkip()

            coVerify { onboardingRepo.onboardingCompleted() }
        }
    }
}