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
    fun `Given a continue event, then log analytics`() {
        val viewModel = OnboardingViewModel(context, analytics)

        viewModel.onContinue(0, "cta")

        verify {
            analytics.buttonClick(
                screenName = "Onboarding_A",
                cta = "cta",
                action = "continue"
            )
        }
    }

    @Test
    fun `Given a skip event, then log analytics`() {
        val viewModel = OnboardingViewModel(context, analytics)

        viewModel.onSkip(1, "cta")

        verify {
            analytics.buttonClick(
                screenName = "Onboarding_B",
                cta = "cta",
                action = "skip"
            )
        }
    }

    @Test
    fun `Given a done event, then log analytics`() {
        val viewModel = OnboardingViewModel(context, analytics)

        viewModel.onDone(2, "cta")

        verify {
            analytics.buttonClick(
                screenName = "Onboarding_C",
                cta = "cta",
                action = "done"
            )
        }
    }

    @Test
    fun `Given a pager indicator event, then log analytics`() {
        val viewModel = OnboardingViewModel(context, analytics)

        viewModel.onPagerIndicator(0)

        verify {
            analytics.buttonClick(
                screenName = "Onboarding_A",
                cta = "dot",
                action = "dot"
            )
        }
    }

}