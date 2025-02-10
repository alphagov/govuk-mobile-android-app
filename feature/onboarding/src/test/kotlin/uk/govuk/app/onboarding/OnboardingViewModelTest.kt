package uk.govuk.app.onboarding

import android.content.Context
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
import uk.govuk.app.analytics.AnalyticsClient

@OptIn(ExperimentalCoroutinesApi::class)
class OnboardingViewModelTest {

    private val context = mockk<Context>(relaxed = true)
    private val analyticsClient = mockk<AnalyticsClient>(relaxed = true)
    private val dispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Given a page view, then log analytics`() {
        val viewModel = OnboardingViewModel(context, analyticsClient)

        every { context.getString(R.string.getThingsDoneScreenTitle) } returns "title"

        viewModel.onPageView(0)

        coVerify {
            analyticsClient.screenView(
                screenClass = "OnboardingScreen",
                screenName = "Onboarding_A",
                title = "title"
            )
        }
    }

    @Test
    fun `Given a button click, then log analytics`() {
        val viewModel = OnboardingViewModel(context, analyticsClient)

        viewModel.onButtonClick("text")

        coVerify {
            analyticsClient.buttonClick("text")
        }
    }

    @Test
    fun `Given a pager indicator event, then log analytics`() {
        val viewModel = OnboardingViewModel(context, analyticsClient)

        viewModel.onPagerIndicatorClick()

        coVerify {
            analyticsClient.pageIndicatorClick()
        }
    }

}