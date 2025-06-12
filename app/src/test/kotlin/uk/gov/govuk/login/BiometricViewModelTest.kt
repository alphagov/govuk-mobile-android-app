package uk.gov.govuk.login

import androidx.fragment.app.FragmentActivity
import io.mockk.coEvery
import io.mockk.coVerify
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
import uk.gov.govuk.data.AppRepo
import uk.gov.govuk.data.auth.AuthRepo
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class BiometricViewModelTest {

    private val authRepo = mockk<AuthRepo>(relaxed = true)
    private val appRepo = mockk<AppRepo>(relaxed = true)
    private val analyticsClient = mockk<AnalyticsClient>(relaxed = true)
    private val activity = mockk<FragmentActivity>(relaxed = true)
    private val dispatcher = UnconfinedTestDispatcher()

    private lateinit var viewModel: BiometricViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        viewModel = BiometricViewModel(authRepo, appRepo, analyticsClient)
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
                screenClass = "BiometricScreen",
                screenName = "Biometrics",
                title = "Biometrics"
            )
        }
    }

    @Test
    fun `Given continue, then log analytics`() {
        viewModel.onContinue(activity, "button text")

        verify {
            analyticsClient.buttonClick(
                text = "button text",
                section = "Biometrics"
            )
        }
    }

    @Test
    fun `Given skip, then log analytics and update repo`() {
        viewModel.onSkip("button text")

        coVerify {
            analyticsClient.buttonClick(
                text = "button text",
                section = "Biometrics"
            )

            appRepo.skipBiometrics()
        }
    }

    @Test
    fun `Given continue, when persist token success, then emit ui state`() {
        coEvery { authRepo.persistRefreshToken(any(), any()) } returns true

        viewModel.onContinue(activity, "button text")

        assertTrue(viewModel.uiState.value)
    }

    @Test
    fun `Given continue, when persist token failure, then emit ui state`() {
        coEvery { authRepo.persistRefreshToken(any(), any()) } returns false

        viewModel.onContinue(activity, "button text")

        assertFalse(viewModel.uiState.value)
    }
}