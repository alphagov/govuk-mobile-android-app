package uk.govuk.app.home

import io.mockk.mockk
import io.mockk.verify
import org.junit.Test
import uk.govuk.app.analytics.Analytics

class HomeViewModelTest {

    private val analytics = mockk<Analytics>(relaxed = true)

    @Test
    fun `Given a page view, then log analytics`() {
        val viewModel = HomeViewModel(analytics)

        viewModel.onPageView()

        verify {
            analytics.screenView(
                screenClass = "HomeScreen",
                screenName = "Homepage",
                title = "Homepage"
            )
        }
    }
}