package uk.gov.govuk.onboarding

import android.content.Context
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Test
import uk.gov.govuk.analytics.AnalyticsClient

class OnboardingViewModelTest {

    private val context = mockk<Context>(relaxed = true)
    private val analyticsClient = mockk<AnalyticsClient>(relaxed = true)

    @Test
    fun `Given a page view, then log analytics`() {
        val viewModel = OnboardingViewModel(context, analyticsClient)

        every { context.getString(R.string.getThingsDoneScreenTitle) } returns "title"

        viewModel.onPageView(0)

        verify {
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

        verify {
            analyticsClient.buttonClick("text")
        }
    }

    @Test
    fun `Given a pager indicator event, then log analytics`() {
        val viewModel = OnboardingViewModel(context, analyticsClient)

        viewModel.onPagerIndicatorClick()

        verify {
            analyticsClient.pageIndicatorClick()
        }
    }

}