package uk.gov.govuk.login

import androidx.fragment.app.FragmentActivity
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
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
import uk.gov.govuk.data.auth.AuthRepo

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    private val authRepo = mockk<AuthRepo>(relaxed = true)
    private val activity = mockk<FragmentActivity>(relaxed = true)
    private val dispatcher = UnconfinedTestDispatcher()

    private lateinit var viewModel: LoginViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        viewModel = LoginViewModel(authRepo)
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
            val events = mutableListOf<LoginEvent>()
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                viewModel.loginCompleted.toList(events)
            }
            viewModel.init(activity)

            assertTrue(events.first().isBiometricLogin)
        }
    }

    @Test
    fun `Given the user is signed in, when init is unsuccessful, then do nothing`() {
        every { authRepo.isUserSignedIn() } returns true
        coEvery { authRepo.refreshTokens(any(), any()) } returns false

        runTest {
            val events = mutableListOf<LoginEvent>()
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                viewModel.loginCompleted.toList(events)
            }
            viewModel.init(activity)

            assertTrue(events.isEmpty())
        }
    }

    @Test
    fun `Given an auth response, when success, then emit ui state`() {
        coEvery { authRepo.handleAuthResponse(any()) } returns true

        runTest {
            val events = mutableListOf<LoginEvent>()
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                viewModel.loginCompleted.toList(events)
            }
            viewModel.onAuthResponse(null)

            assertFalse(events.first().isBiometricLogin)
        }
    }

    @Test
    fun `Given an auth response, when failure, then do not emit ui state`() {
        coEvery { authRepo.handleAuthResponse(any()) } returns false

        runTest {
            val events = mutableListOf<LoginEvent>()
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                viewModel.loginCompleted.toList(events)
            }
            viewModel.onAuthResponse(null)

            assertTrue(events.isEmpty())
        }
    }
}