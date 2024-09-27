package uk.govuk.app.search

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.experimental.runners.Enclosed
import org.junit.runner.RunWith
import uk.govuk.app.analytics.Analytics
import uk.govuk.app.search.di.SearchModule
import uk.govuk.app.search.data.remote.model.Result
import uk.govuk.app.search.data.remote.model.SearchResponse
import uk.govuk.app.search.domain.ResultStatus
import uk.govuk.app.search.domain.SearchResult

@RunWith(Enclosed::class)
@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModelTest {
    class AnalyticsTest {
        private val analytics = mockk<Analytics>(relaxed = true)
        private val service = SearchModule().providesSearchApi()
        private val repository = SearchRepo(service)
        private val viewModel = SearchViewModel(analytics, repository)
        private val dispatcher = UnconfinedTestDispatcher()
        private val searchTerm = "search term"

        @Test
        fun `Given a page view, then log analytics`() {
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
            Dispatchers.setMain(dispatcher)

            viewModel.onSearch(searchTerm)

            runTest {
                coVerify {
                    analytics.search(searchTerm)
                }
            }
        }

        @Test
        fun `Given a search, and a search result is clicked, then log analytics`() {
            Dispatchers.setMain(dispatcher)

            viewModel.onSearchResultClicked("search result title", "search result link")

            runTest {
                coVerify {
                    analytics.searchResultClick("search result title", "search result link")
                }
            }
        }
    }

    class UiStateTest {
        private val analytics = mockk<Analytics>(relaxed = true)
        private val dispatcher = UnconfinedTestDispatcher()
        private val repository = mockk<SearchRepo>(relaxed = true)
        private val viewModel = SearchViewModel(analytics, repository)
        private val searchTerm = "search term"
        private val resultWithNoSearchResponse = SearchResponse(total = 0, results = emptyList())
        private val resultWithOneResult = SearchResponse(
            total = 1,
            results = listOf(
                Result(
                    title = "title",
                    description = "description",
                    link = "link"
                )
            )
        )

        @Before
        fun setup() {
            Dispatchers.setMain(dispatcher)
        }

        @After
        fun tearDown() {
            Dispatchers.resetMain()
        }

        @Test
        fun `Given a search with a result, then the results and status in the view model are correct`() {
            coEvery { repository.performSearch(searchTerm) } returns SearchResult(
                status = ResultStatus.Success,
                response = resultWithOneResult
            )

            viewModel.onSearch(searchTerm)

            runTest {
                val result = viewModel.uiState.first()

                assertEquals(searchTerm, result!!.searchTerm)
                assertEquals(ResultStatus.Success, result.resultStatus)
                assertEquals(1, result.searchResults.size)
            }
        }

        @Test
        fun `Given a search without any results, then the results and status in the view model are correct`() {
            coEvery { repository.performSearch(searchTerm) } returns SearchResult(
                status = ResultStatus.Empty,
                response = resultWithNoSearchResponse
            )

            viewModel.onSearch(searchTerm)

            runTest {
                val result = viewModel.uiState.first()

                assertEquals(searchTerm, result!!.searchTerm)
                assertEquals(ResultStatus.Empty, result.resultStatus)
                assertEquals(0, result.searchResults.size)
            }
        }

        @Test
        fun `Given a search when the device is offline, then the results and status in the view model are correct`() {
            coEvery { repository.performSearch(searchTerm) } returns SearchResult(
                status = ResultStatus.DeviceOffline,
                response = resultWithNoSearchResponse
            )

            viewModel.onSearch(searchTerm)

            runTest {
                val result = viewModel.uiState.first()

                assertEquals(searchTerm, result!!.searchTerm)
                assertEquals(ResultStatus.DeviceOffline, result.resultStatus)
                assertEquals(0, result.searchResults.size)
            }
        }

        @Test
        fun `Given a search when the Search API is unavailable, then the results and status in the view model are correct`() {
            coEvery { repository.performSearch(searchTerm) } returns SearchResult(
                status = ResultStatus.ServiceNotResponding,
                response = resultWithNoSearchResponse
            )

            viewModel.onSearch(searchTerm)

            runTest {
                val result = viewModel.uiState.first()

                assertEquals(searchTerm, result!!.searchTerm)
                assertEquals(ResultStatus.ServiceNotResponding, result.resultStatus)
                assertEquals(0, result.searchResults.size)
            }
        }

        @Test
        fun `Given a search that returns an error, then the results and status in the view model are correct`() {
            val errorMessage = "error message"

            coEvery { repository.performSearch(searchTerm) } returns SearchResult(
                status = ResultStatus.Error(errorMessage),
                response = resultWithNoSearchResponse
            )

            viewModel.onSearch(searchTerm)

            runTest {
                val result = viewModel.uiState.first()

                assertEquals(searchTerm, result!!.searchTerm)
                assertEquals(ResultStatus.Error(errorMessage), result.resultStatus)
                assertEquals(0, result.searchResults.size)
            }
        }
    }
}
