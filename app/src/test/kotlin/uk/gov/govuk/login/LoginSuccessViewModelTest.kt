package uk.gov.govuk.login

import io.mockk.coEvery
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
import org.junit.Before
import org.junit.Test
import uk.gov.govuk.data.AppRepo
import uk.gov.govuk.data.auth.AuthRepo
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class LoginSuccessViewModelTest {
    private val appRepo = mockk<AppRepo>(relaxed = true)
    private val authRepo = mockk<AuthRepo>(relaxed = true)
    private val dispatcher = UnconfinedTestDispatcher()

    private lateinit var viewModel: LoginSuccessViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        viewModel = LoginSuccessViewModel(appRepo, authRepo)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Given authentication is not enabled, when on continue, then log emit event`() {
        every { authRepo.isAuthenticationEnabled() } returns false

        runTest {
            val events = mutableListOf<LoginSuccessEvent>()
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                viewModel.loginSuccessCompleted.toList(events)
            }
            viewModel.onContinue()

            assertEquals(LoginSuccessEvent(false), events.first())
        }
    }

    @Test
    fun `Given the user has skipped biometrics, when on continue, then log emit event`() {
        every { authRepo.isAuthenticationEnabled() } returns true
        coEvery { appRepo.hasSkippedBiometrics() } returns true

        runTest {
            val events = mutableListOf<LoginSuccessEvent>()
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                viewModel.loginSuccessCompleted.toList(events)
            }
            viewModel.onContinue()

            assertEquals(LoginSuccessEvent(false), events.first())
        }
    }

    @Test
    fun `Given authentication is enabled and the user has not skipped biometrics, when on continue, then log emit event`() {
        every { authRepo.isAuthenticationEnabled() } returns true
        coEvery { appRepo.hasSkippedBiometrics() } returns false

        runTest {
            val events = mutableListOf<LoginSuccessEvent>()
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                viewModel.loginSuccessCompleted.toList(events)
            }
            viewModel.onContinue()

            assertEquals(LoginSuccessEvent(true), events.first())
        }
    }
}
