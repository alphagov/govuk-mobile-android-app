package uk.gov.govuk.login

import androidx.fragment.app.FragmentActivity
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Test
import uk.gov.govuk.analytics.AnalyticsClient
import uk.gov.govuk.data.auth.AuthRepo
import uk.gov.govuk.login.LoginViewModel.LoginUiState
import kotlin.test.assertEquals
import kotlin.test.assertNull

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
            authRepo.refreshTokens(any(), any(), any(), any())
        }
    }

    @Test
    fun `Given the user is signed in, when init is successful, then emit ui state`() {
        every { authRepo.isUserSignedIn() } returns true
        coEvery { authRepo.refreshTokens(any(), any(), any(), any()) } returns true

        viewModel.init(activity)

        assertFalse(viewModel.loginCompleted.value!!.shouldDisplayLocalAuthOnboarding)
    }

    @Test
    fun `Given the user is signed in, when init is unsuccessful, then do nothing`() {
        every { authRepo.isUserSignedIn() } returns true
        coEvery { authRepo.refreshTokens(any(), any(), any(), any()) } returns false

        viewModel.init(activity)

        assertNull(viewModel.loginCompleted.value)
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
    fun `Given an auth response and authentication enabled, when success, then emit ui state`() {
        coEvery { authRepo.handleAuthResponse(any()) } returns true
        every { authRepo.isAuthenticationEnabled() } returns true

        viewModel.onAuthResponse(null)

        assertEquals(LoginUiState(true), viewModel.loginCompleted.value)
    }

    @Test
    fun `Given an auth response and authentication disabled, when success, then emit ui state`() {
        coEvery { authRepo.handleAuthResponse(any()) } returns true
        every { authRepo.isAuthenticationEnabled() } returns false

        viewModel.onAuthResponse(null)

        assertEquals(LoginUiState(false), viewModel.loginCompleted.value)
    }

    @Test
    fun `Given an auth response, when failure, then do not emit ui state`() {
        coEvery { authRepo.handleAuthResponse(any()) } returns false

        viewModel.onAuthResponse(null)

        assertNull(viewModel.loginCompleted.value)
    }
}