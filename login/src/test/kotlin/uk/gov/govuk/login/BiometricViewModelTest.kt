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
import org.junit.Before
import org.junit.Test
import uk.gov.android.securestore.RetrievalEvent
import uk.gov.android.securestore.SecureStore
import uk.gov.android.securestore.error.SecureStoreErrorType
import uk.gov.govuk.analytics.AnalyticsClient
import uk.gov.govuk.login.data.LoginRepo
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class BiometricViewModelTest {

    private val secureStore = mockk<SecureStore>(relaxed = true)
    private val loginRepo = mockk<LoginRepo>(relaxed = true)
    private val analyticsClient = mockk<AnalyticsClient>(relaxed = true)
    private val activity = mockk<FragmentActivity>(relaxed = true)
    private val dispatcher = UnconfinedTestDispatcher()

    private lateinit var viewModel: BiometricViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        viewModel = BiometricViewModel(secureStore, loginRepo, analyticsClient)
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
    fun `Given skip, then log analytics`() {
        viewModel.onSkip("button text")

        verify {
            analyticsClient.buttonClick(
                text = "button text",
                section = "Biometrics"
            )
        }
    }

    @Test
    fun `Given continue, when biometrics success, then store token and emit ui state`() {
        every { loginRepo.accessToken } returns "token-value"
        coEvery {
            secureStore.retrieveWithAuthentication(
                key = arrayOf("token"),
                authPromptConfig = any(),
                context = any()
            )
        } returns RetrievalEvent.Success(emptyMap())

        viewModel.onContinue(activity, "button text")

        coVerify {
            secureStore.upsert("token", "token-value")
        }

        assertEquals(true, viewModel.uiState.value)
    }

    @Test
    fun `Given continue, when biometrics failure, then delete token and do not emit ui state`() {
        every { loginRepo.accessToken } returns "token-value"
        coEvery {
            secureStore.retrieveWithAuthentication(
                key = arrayOf("token"),
                authPromptConfig = any(),
                context = any()
            )
        } returns RetrievalEvent.Failed(SecureStoreErrorType.GENERAL)

        viewModel.onContinue(activity, "button text")

        coVerify {
            secureStore.delete("token")
        }

        assertEquals(false, viewModel.uiState.value)
    }
}