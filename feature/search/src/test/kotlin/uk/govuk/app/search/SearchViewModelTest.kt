package uk.govuk.app.search

import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
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

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `Given a search, then log analytics`() {
        val viewModel = SearchViewModel(analytics)
        val dispatcher = UnconfinedTestDispatcher()

        Dispatchers.setMain(dispatcher)

        viewModel.onSearch("search term")

        runTest {
            coVerify {
                analytics.search("search term")
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `Given a search, and a search result is clicked, then log analytics`() {
        val viewModel = SearchViewModel(analytics)
        val dispatcher = UnconfinedTestDispatcher()

        Dispatchers.setMain(dispatcher)

        viewModel.onSearchResultClicked("result link")

        runTest {
            coVerify {
                analytics.screenView(
                    screenClass = "SearchScreen",
                    screenName = "SearchResult",
                    title = "result link"
                )
            }
        }
    }
}
