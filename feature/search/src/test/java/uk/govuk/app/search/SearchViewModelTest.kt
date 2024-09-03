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
                screenName = "Search",
                title = "Search"
            )
        }
    }

    @Test
    fun `Given a search, then log analytics`() {
        val viewModel = SearchViewModel(analytics)

        viewModel.onSearch("search term")

        verify {
            analytics.search("search term")
        }
    }
}