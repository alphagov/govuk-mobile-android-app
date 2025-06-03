package uk.gov.govuk.onboarding

import androidx.fragment.app.FragmentActivity
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
import uk.gov.govuk.data.auth.AuthRepo

@OptIn(ExperimentalCoroutinesApi::class)
class OnboardingViewModelTest {

    private val authRepo = mockk<AuthRepo>(relaxed = true)
    private val analyticsClient = mockk<AnalyticsClient>(relaxed = true)
    private val activity = mockk<FragmentActivity>(relaxed = true)
    private val dispatcher = UnconfinedTestDispatcher()

    private lateinit var viewModel: OnboardingViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        viewModel = OnboardingViewModel(authRepo, analyticsClient)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Given a page view, then log analytics`() {
        viewModel.onPageView()

        verify {
            analyticsClient.screenView(
                screenClass = "OnboardingScreen",
                screenName = "Onboarding Page",
                title = "Onboarding Page"
            )
        }
    }

    @Test
    fun `Given a button click, then log analytics`() {
        viewModel.onButtonClick("text")

        verify {
            analyticsClient.buttonClick("text")
        }
    }

    @Test
    fun `Given the user is not signed in, when init, then do nothing`() {
        every { authRepo.isUserSignedIn() } returns false

        viewModel.init(activity)

        coVerify(exactly = 0) {
            authRepo.refreshTokens(any(), any())
        }
    }

    @Test
    fun `Given the user is signed in, when init is successful, then emit ui state`() {
        every { authRepo.isUserSignedIn() } returns true
        coEvery { authRepo.refreshTokens(any(), any()) } returns true

        runTest {
            val events = mutableListOf<Boolean>()
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                viewModel.loginCompleted.toList(events)
            }
            viewModel.init(activity)

            assertFalse(events.first())
        }
    }

    @Test
    fun `Given the user is signed in, when init is unsuccessful, then do nothing`() {
        every { authRepo.isUserSignedIn() } returns true
        coEvery { authRepo.refreshTokens(any(), any()) } returns false

        runTest {
            val events = mutableListOf<Boolean>()
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                viewModel.loginCompleted.toList(events)
            }
            viewModel.init(activity)

            assertTrue(events.isEmpty())
        }
    }

    @Test
    fun `Given an auth response and a different user, when success, then emit ui state`() {
        coEvery { authRepo.handleAuthResponse(any()) } returns true
        every { authRepo.isDifferentUser() } returns true

        runTest {
            val events = mutableListOf<Boolean>()
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                viewModel.loginCompleted.toList(events)
            }
            viewModel.onAuthResponse(null)

            assertTrue(events.first())
        }
    }

    @Test
    fun `Given an auth response and the same user, when success, then emit ui state`() {
        coEvery { authRepo.handleAuthResponse(any()) } returns true
        every { authRepo.isAuthenticationEnabled() } returns false

        runTest {
            val events = mutableListOf<Boolean>()
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                viewModel.loginCompleted.toList(events)
            }
            viewModel.onAuthResponse(null)

            assertFalse(events.first())
        }
    }

    @Test
    fun `Given an auth response, when failure, then do not emit ui state`() {
        coEvery { authRepo.handleAuthResponse(any()) } returns false

        runTest {
            val events = mutableListOf<Boolean>()
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                viewModel.loginCompleted.toList(events)
            }
            viewModel.onAuthResponse(null)

            assertTrue(events.isEmpty())
        }
    }
}
