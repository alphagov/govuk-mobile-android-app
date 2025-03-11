package uk.govuk.app.home

import io.mockk.mockk
import io.mockk.verify
import org.junit.Test
import uk.govuk.app.analytics.AnalyticsClient

class HomeViewModelTest {

    private val analyticsClient = mockk<AnalyticsClient>(relaxed = true)

    @Test
    fun `Given a page view, then log analytics`() {
        val viewModel = HomeViewModel(analyticsClient)

        viewModel.onPageView()

        verify {
            analyticsClient.screenView(
                screenClass = "HomeScreen",
                screenName = "Homepage",
                title = "Homepage"
            )
        }
    }
}
