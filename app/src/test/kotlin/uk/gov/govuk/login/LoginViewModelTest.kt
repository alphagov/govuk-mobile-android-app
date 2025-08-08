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
import uk.gov.govuk.data.auth.AuthRepo
import uk.gov.govuk.login.data.LoginRepo
import java.util.Date

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    private val authRepo = mockk<AuthRepo>(relaxed = true)
    private val loginRepo = mockk<LoginRepo>(relaxed = true)
    private val activity = mockk<FragmentActivity>(relaxed = true)
    private val dispatcher = UnconfinedTestDispatcher()

    private lateinit var viewModel: LoginViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        viewModel = LoginViewModel(authRepo, loginRepo)
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
    fun `Given the user is signed in and the id token issue date is in the future, when init is successful, then emit ui state`() {
        every { authRepo.isUserSignedIn() } returns true
        coEvery { authRepo.refreshTokens(any(), any()) } returns true
        coEvery { loginRepo.getRefreshTokenExpiryDate() } returns Date().toInstant().epochSecond + 10000L

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
    fun `Given the user is signed in and the id token issue date is not in the future, then end user session and clear auth repo`() {
        every { authRepo.isUserSignedIn() } returns true
        coEvery { authRepo.refreshTokens(any(), any()) } returns true
        coEvery { loginRepo.getRefreshTokenExpiryDate() } returns Date().toInstant().epochSecond

        runTest {
            val events = mutableListOf<LoginEvent>()
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                viewModel.loginCompleted.toList(events)
            }
            viewModel.init(activity)

            assertTrue(events.isEmpty())

            coVerify(exactly = 1) {
                authRepo.endUserSession()
                authRepo.clear()
            }
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
    fun `Given an auth response, when success and id token issue date is not stored, then emit ui state`() {
        coEvery { authRepo.handleAuthResponse(any()) } returns true
        every { authRepo.getIdTokenIssueDate() } returns null

        runTest {
            val events = mutableListOf<LoginEvent>()
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                viewModel.loginCompleted.toList(events)
            }
            viewModel.onAuthResponse(null)

            assertFalse(events.first().isBiometricLogin)

            verify(exactly = 1) {
                authRepo.getIdTokenIssueDate()
            }
            coVerify(exactly = 0) {
                loginRepo.setRefreshTokenExpiryDate(any())
            }
        }
    }

    @Test
    fun `Given an auth response, when success and id token issue date is stored, then emit ui state`() {
        coEvery { authRepo.handleAuthResponse(any()) } returns true
        every { authRepo.getIdTokenIssueDate() } returns 12345L

        runTest {
            val events = mutableListOf<LoginEvent>()
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                viewModel.loginCompleted.toList(events)
            }
            viewModel.onAuthResponse(null)

            assertFalse(events.first().isBiometricLogin)

            verify(exactly = 1) {
                authRepo.getIdTokenIssueDate()
            }
            coVerify(exactly = 1) {
                loginRepo.setRefreshTokenExpiryDate(12345L + 601200L)
            }
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
