package uk.gov.govuk.home

import io.mockk.mockk
import io.mockk.verify
import org.junit.Test
import uk.gov.govuk.analytics.AnalyticsClient

class HomeViewModelTest {

    private val analyticsClient = mockk<AnalyticsClient>(relaxed = true)

    @Test
    fun `Given a page view, and the user has opted into chat, then log analytics`() {
        val viewModel = HomeViewModel(analyticsClient)

        viewModel.onPageView(userChatOptInState = true)

        verify {
            analyticsClient.screenViewWithType(
                screenClass = "HomeScreen",
                screenName = "Homepage",
                title = "Homepage",
                type = "chatOptIn"
            )
        }
    }

    @Test
    fun `Given a page view, and the user has opted out of chat, then log analytics`() {
        val viewModel = HomeViewModel(analyticsClient)

        viewModel.onPageView(userChatOptInState = false)

        verify {
            analyticsClient.screenViewWithType(
                screenClass = "HomeScreen",
                screenName = "Homepage",
                title = "Homepage",
                type = "chatOptOut"
            )
        }
    }
}
