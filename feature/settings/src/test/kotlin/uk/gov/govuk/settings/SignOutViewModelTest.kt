package uk.gov.govuk.settings

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
import uk.gov.govuk.analytics.AnalyticsClient
import uk.gov.govuk.data.auth.AuthRepo

@OptIn(ExperimentalCoroutinesApi::class)
class SignOutViewModelTest {

    private val dispatcher = UnconfinedTestDispatcher()
    private val authRepo = mockk<AuthRepo>(relaxed = true)
    private val analyticsClient = mockk<AnalyticsClient>(relaxed = true)

    private lateinit var viewModel: SignOutViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)

        viewModel = SignOutViewModel(authRepo, analyticsClient)
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
                screenClass = "SignOutScreen",
                screenName = "Sign Out",
                title = "Sign Out"
            )
        }
    }

    @Test
    fun `Given an error page view, then log analytics`() {
        viewModel.onErrorPageView()

        verify {
            analyticsClient.screenView(
                screenClass = "SignOutErrorScreen",
                screenName = "Sign Out",
                title = "Sign Out"
            )
        }
    }

    @Test
    fun `Given a back to settings button click, then log analytics`() {
        viewModel.onBack("button text")

        verify {
            analyticsClient.buttonClick(
                text = "button text",
                section = "Settings"
            )
        }
    }

    @Test
    fun `Given a user signs out successfully, then log analytics, disable analytics and sign out`() {
        every { authRepo.signOut() } returns true

        viewModel.onSignOut("text")

        coVerify {
            analyticsClient.buttonClick(
                text = "text",
                section = "Settings"
            )

            analyticsClient.disable()
            authRepo.signOut()
        }
    }

    @Test
    fun `Given a user signs out unsuccessfully, then log analytics and do not disable analytics`() {
        every { authRepo.signOut() } returns false

        viewModel.onSignOut("text")

        coVerify {
            analyticsClient.buttonClick(
                text = "text",
                section = "Settings"
            )

            authRepo.signOut()
        }

        coVerify (exactly = 0) { analyticsClient.disable() }
    }
}
