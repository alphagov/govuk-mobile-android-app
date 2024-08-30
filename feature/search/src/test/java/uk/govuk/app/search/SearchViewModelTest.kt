package uk.govuk.app.search

import io.mockk.mockk
import io.mockk.verify
import org.junit.Test
import uk.govuk.app.analytics.Analytics

class SearchViewModelTest {

    private val analytics = mockk<Analytics>(relaxed = true)

    @Test
    fun `Given a page view, then log analytics`() {
        val viewModel = SearchViewModel(analytics)

        viewModel.onPageView()

        verify {
            analytics.screenView(
                screenClass = "SearchScreen",
                alias = "SEARCH",
                title = "Search"
            )
        }
    }
}