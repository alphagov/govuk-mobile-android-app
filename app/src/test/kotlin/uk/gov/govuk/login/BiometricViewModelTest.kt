package uk.gov.govuk.login

import androidx.fragment.app.FragmentActivity
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import uk.gov.govuk.data.AppRepo
import uk.gov.govuk.data.auth.AuthRepo
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class BiometricViewModelTest {

    private val authRepo = mockk<AuthRepo>(relaxed = true)
    private val appRepo = mockk<AppRepo>(relaxed = true)
    private val activity = mockk<FragmentActivity>(relaxed = true)
    private val dispatcher = UnconfinedTestDispatcher()

    private lateinit var viewModel: BiometricViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        viewModel = BiometricViewModel(authRepo, appRepo)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Given skip, then update repo`() {
        viewModel.onSkip()

        coVerify {
            appRepo.skipBiometrics()
        }
    }

    @Test
    fun `Given continue, when persist token success, then emit ui state`() {
        coEvery { authRepo.persistRefreshToken(any(), any()) } returns true

        viewModel.onContinue(activity)

        assertTrue(viewModel.uiState.value)
    }

    @Test
    fun `Given continue, when persist token failure, then emit ui state`() {
        coEvery { authRepo.persistRefreshToken(any(), any()) } returns false

        viewModel.onContinue(activity)

        assertFalse(viewModel.uiState.value)
    }
}