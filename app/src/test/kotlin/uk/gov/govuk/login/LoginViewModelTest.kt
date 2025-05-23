package uk.gov.govuk.login

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
class LoginViewModelTest {

    private val authRepo = mockk<AuthRepo>(relaxed = true)
    private val analyticsClient = mockk<AnalyticsClient>(relaxed = true)
    private val activity = mockk<FragmentActivity>(relaxed = true)
    private val dispatcher = UnconfinedTestDispatcher()

    private lateinit var viewModel: LoginViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        viewModel = LoginViewModel(authRepo, analyticsClient)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
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
    fun `Given a page view, then log analytics`() {
        viewModel.onPageView()

        verify {
            analyticsClient.screenView(
                screenClass = "LoginScreen",
                screenName = "Login",
                title = "Login"
            )
        }
    }

    @Test
    fun `Given continue, then log analytics`() {
        viewModel.onContinue("button text")

        verify {
            analyticsClient.buttonClick(
                text = "button text",
                section = "Login"
            )
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