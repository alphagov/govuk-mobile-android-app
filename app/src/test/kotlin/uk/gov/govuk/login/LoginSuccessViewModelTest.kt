package uk.gov.govuk.login

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

@OptIn(ExperimentalCoroutinesApi::class)
class LoginSuccessViewModelTest {
    private val analyticsClient = mockk<AnalyticsClient>(relaxed = true)
    private val dispatcher = UnconfinedTestDispatcher()

    private lateinit var viewModel: LoginSuccessViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        viewModel = LoginSuccessViewModel(analyticsClient)
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
                screenClass = "LoginSuccessScreen",
                screenName = "Login Success",
                title = "Login Success"
            )
        }
    }

    @Test
    fun `Given on continue, then log analytics`() {
        viewModel.onContinue("button text")

        verify {
            analyticsClient.buttonClick(
                text = "button text",
                section = "Login"
            )
        }
    }
}
