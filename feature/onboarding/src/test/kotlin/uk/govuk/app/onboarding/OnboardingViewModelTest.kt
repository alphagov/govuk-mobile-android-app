package uk.govuk.app.onboarding

import android.content.Context
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Test
import uk.govuk.app.analytics.Analytics

class OnboardingViewModelTest {

    private val context = mockk<Context>(relaxed = true)
    private val analytics = mockk<Analytics>(relaxed = true)

    @Test
    fun `Given a page view, then log analytics`() {
        val viewModel = OnboardingViewModel(context, analytics)

        every { context.getString(R.string.getThingsDoneScreenTitle) } returns "title"

        viewModel.onPageView(0)

        verify {
            analytics.screenView(
                screenClass = "OnboardingScreen",
                screenName = "Onboarding_A",
                title = "title"
            )
        }
    }

    @Test
    fun `Given a button click, then log analytics`() {
        val viewModel = OnboardingViewModel(context, analytics)

        viewModel.onButtonClick("text")

        verify {
            analytics.buttonClick("text")
        }
    }

    @Test
    fun `Given a pager indicator event, then log analytics`() {
        val viewModel = OnboardingViewModel(context, analytics)

        viewModel.onPagerIndicatorClick()

        verify {
            analytics.pageIndicatorClick()
        }
    }

}