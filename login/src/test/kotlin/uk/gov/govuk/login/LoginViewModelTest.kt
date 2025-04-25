package uk.gov.govuk.login

import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import uk.gov.govuk.analytics.AnalyticsClient
import uk.gov.govuk.login.LoginViewModel.LoginUiState
import uk.gov.govuk.login.data.LoginRepo
import kotlin.test.assertEquals
import kotlin.test.assertNull

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    private val loginRepo = mockk<LoginRepo>(relaxed = true)
    private val analyticsClient = mockk<AnalyticsClient>(relaxed = true)
    private val dispatcher = UnconfinedTestDispatcher()

    private lateinit var viewModel: LoginViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        viewModel = LoginViewModel(loginRepo, analyticsClient)
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
        coEvery { loginRepo.handleAuthResponse(any()) } returns true
        every { loginRepo.isAuthenticationEnabled() } returns true

        viewModel.onAuthResponse(null)

        assertEquals(LoginUiState(true), viewModel.uiState.value)
    }

    @Test
    fun `Given an auth response and authentication disabled, when success, then emit ui state`() {
        coEvery { loginRepo.handleAuthResponse(any()) } returns true
        every { loginRepo.isAuthenticationEnabled() } returns false

        viewModel.onAuthResponse(null)

        assertEquals(LoginUiState(false), viewModel.uiState.value)
    }

    @Test
    fun `Given an auth response, when failure, then do not emit ui state`() {
        coEvery { loginRepo.handleAuthResponse(any()) } returns false

        viewModel.onAuthResponse(null)

        assertNull(viewModel.uiState.value)
    }
}