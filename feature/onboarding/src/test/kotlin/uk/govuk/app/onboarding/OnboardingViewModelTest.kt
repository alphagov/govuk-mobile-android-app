package uk.govuk.app.onboarding

import android.content.Context
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Test
import uk.govuk.app.onboarding.analytics.OnboardingAnalytics

class OnboardingViewModelTest {

    private val context = mockk<Context>(relaxed = true)
    private val onboardingAnalytics = mockk<OnboardingAnalytics>(relaxed = true)

    @Test
    fun `Given a page view, then log analytics`() {
        val viewModel = OnboardingViewModel(context, onboardingAnalytics)

        every { context.getString(R.string.getThingsDoneScreenTitle) } returns "title"

        viewModel.onPageView(0)

        verify {
            onboardingAnalytics.onboardingScreenView(
                screenClass = "OnboardingScreen",
                alias = "ONBOARDING_A",
                title = "title"
            )
        }
    }

    @Test
    fun `Given a continue event, then log analytics`() {
        val viewModel = OnboardingViewModel(context, onboardingAnalytics)

        viewModel.onContinue(0, "cta")

        verify {
            onboardingAnalytics.onboardingButtonClick(
                screenName = "ONBOARDING_A",
                cta = "cta",
                action = "continue"
            )
        }
    }

    @Test
    fun `Given a skip event, then log analytics`() {
        val viewModel = OnboardingViewModel(context, onboardingAnalytics)

        viewModel.onSkip(1, "cta")

        verify {
            onboardingAnalytics.onboardingButtonClick(
                screenName = "ONBOARDING_B",
                cta = "cta",
                action = "skip"
            )
        }
    }

    @Test
    fun `Given a done event, then log analytics`() {
        val viewModel = OnboardingViewModel(context, onboardingAnalytics)

        viewModel.onDone(2, "cta")

        verify {
            onboardingAnalytics.onboardingButtonClick(
                screenName = "ONBOARDING_C",
                cta = "cta",
                action = "done"
            )
        }
    }

}