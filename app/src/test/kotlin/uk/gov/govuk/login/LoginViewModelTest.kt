package uk.gov.govuk.login

import androidx.fragment.app.FragmentActivity
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
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
import uk.gov.govuk.data.auth.AuthRepo.RefreshStatus.ERROR
import uk.gov.govuk.data.auth.AuthRepo.RefreshStatus.LOADING
import uk.gov.govuk.data.auth.AuthRepo.RefreshStatus.SUCCESS
import uk.gov.govuk.data.auth.ErrorEvent
import uk.gov.govuk.login.data.LoginRepo
import java.util.Date
import kotlin.test.assertEquals

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
    fun `Given the user is signed in and the refresh token issue date is not in the future, then end user session and clear auth repo`() {
        every { authRepo.isUserSignedIn() } returns true
        coEvery { loginRepo.getRefreshTokenExpiryDate() } returns Date().toInstant().epochSecond

        runTest {
            val events = mutableListOf<LoginEvent>()
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                viewModel.loginCompleted.toList(events)
            }
            viewModel.init(activity)

            assertTrue(events.isEmpty())

            coVerify(exactly = 0) {
                authRepo.refreshTokens(any(), any())
            }

            coVerify {
                authRepo.endUserSession()
                authRepo.clear()
            }
        }
    }

    @Test
    fun `Given the user is signed in and the refresh token issue date is in the future, when init is successful, then emit loading and login event`() {
        every { authRepo.isUserSignedIn() } returns true
        coEvery { loginRepo.getRefreshTokenExpiryDate() } returns Date().toInstant().epochSecond + 10000L
        coEvery { authRepo.refreshTokens(any(), any()) } returns flowOf(LOADING, SUCCESS)

        runTest {
            val isLoading = mutableListOf<Boolean?>()
            val events = mutableListOf<LoginEvent>()
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                viewModel.isLoading.toList(isLoading)
            }
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                viewModel.loginCompleted.toList(events)
            }
            viewModel.init(activity)

            assertTrue(isLoading.last() == true)
            assertTrue(events.first().isBiometricLogin)
        }
    }

    @Test
    fun `Given the user is signed in, when init is unsuccessful, then emit loading`() {
        every { authRepo.isUserSignedIn() } returns true
        coEvery { loginRepo.getRefreshTokenExpiryDate() } returns Date().toInstant().epochSecond + 10000L
        coEvery { authRepo.refreshTokens(any(), any()) } returns flowOf(LOADING, ERROR)

        runTest {
            val isLoading = mutableListOf<Boolean?>()
            val events = mutableListOf<LoginEvent>()
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                viewModel.isLoading.toList(isLoading)
            }
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                viewModel.loginCompleted.toList(events)
            }
            viewModel.init(activity)

            assertTrue(isLoading.last() == false)
            assertTrue(events.isEmpty())
        }
    }

    @Test
    fun `Given an auth response, when success and id token issue date is not stored, then emit loading and login event`() {
        coEvery { authRepo.handleAuthResponse(any()) } returns true
        every { authRepo.getIdTokenIssueDate() } returns null

        runTest {
            val isLoading = mutableListOf<Boolean?>()
            val events = mutableListOf<LoginEvent>()
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                viewModel.isLoading.toList(isLoading)
            }
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                viewModel.loginCompleted.toList(events)
            }
            viewModel.onAuthResponse(null)

            assertTrue(isLoading.last() == true)
            assertFalse(events.first().isBiometricLogin)

            coVerify(exactly = 0) {
                loginRepo.setRefreshTokenExpiryDate(any())
            }
        }
    }

    @Test
    fun `Given an auth response, when success and id token issue date is stored, then emit loading, login event and set token expiry`() {
        coEvery { authRepo.handleAuthResponse(any()) } returns true
        every { authRepo.getIdTokenIssueDate() } returns 12345L

        runTest {
            val isLoading = mutableListOf<Boolean?>()
            val events = mutableListOf<LoginEvent>()
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                viewModel.isLoading.toList(isLoading)
            }
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                viewModel.loginCompleted.toList(events)
            }
            viewModel.onAuthResponse(null)

            assertTrue(isLoading.last() == true)
            assertFalse(events.first().isBiometricLogin)

            coVerify(exactly = 1) {
                authRepo.getIdTokenIssueDate()
                loginRepo.setRefreshTokenExpiryDate(12345L + 601200L)
            }
        }
    }

    @Test
    fun `Given an auth response, when failure, then emit error event`() {
        coEvery { authRepo.handleAuthResponse(any()) } returns false

        runTest {
            val isLoading = mutableListOf<Boolean?>()
            val loginEvents = mutableListOf<LoginEvent>()
            val errorEvents = mutableListOf<ErrorEvent>()
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                viewModel.isLoading.toList(isLoading)
            }
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                viewModel.loginCompleted.toList(loginEvents)
            }
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                viewModel.errorEvent.toList(errorEvents)
            }
            viewModel.onAuthResponse(null)

            assertTrue(isLoading.last() == true)
            assertTrue(loginEvents.isEmpty())
            assertEquals(ErrorEvent.UnableToSignInError, errorEvents.first())
        }
    }
}
