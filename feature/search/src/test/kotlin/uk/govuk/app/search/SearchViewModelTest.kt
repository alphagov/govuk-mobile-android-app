package uk.govuk.app.search

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.experimental.runners.Enclosed
import org.junit.runner.RunWith
import uk.govuk.app.analytics.AnalyticsClient
import uk.govuk.app.data.model.Result.DeviceOffline
import uk.govuk.app.data.model.Result.Error
import uk.govuk.app.data.model.Result.ServiceNotResponding
import uk.govuk.app.data.model.Result.Success
import uk.govuk.app.search.data.SearchRepo
import uk.govuk.app.search.data.local.SearchLocalDataSource
import uk.govuk.app.search.data.remote.AutocompleteApi
import uk.govuk.app.search.data.remote.SearchApi
import uk.govuk.app.search.data.remote.model.AutocompleteResponse
import uk.govuk.app.search.data.remote.model.SearchResponse
import uk.govuk.app.search.data.remote.model.SearchResult
import uk.govuk.app.visited.Visited

@RunWith(Enclosed::class)
@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModelTest {
    class AnalyticsTest {
        private val analyticsClient = mockk<AnalyticsClient>(relaxed = true)
        private val visited = mockk<Visited>(relaxed = true)
        private val searchApi = mockk<SearchApi>(relaxed = true)
        private val autocompleteApi = mockk<AutocompleteApi>(relaxed = true)
        private val searchLocalDataSource = mockk<SearchLocalDataSource>(relaxed = true)
        private val repository = SearchRepo(searchApi, autocompleteApi, searchLocalDataSource)
        private val dispatcher = UnconfinedTestDispatcher()
        private val searchTerm = "search term"

        private lateinit var viewModel: SearchViewModel

        @Before
        fun setup() {
            Dispatchers.setMain(dispatcher)
            viewModel = SearchViewModel(analyticsClient, visited, repository)
        }

        @After
        fun tearDown() {
            Dispatchers.resetMain()
        }

        @Test
        fun `Given a page view, then log analytics`() {
            viewModel.onPageView()

            verify {
                analyticsClient.screenView(
                    screenClass = "SearchScreen",
                    screenName = "Search",
                    title = "Search"
                )
            }
        }

        @Test
        fun `Given a search, then log analytics`() {
            viewModel.onSearch(searchTerm)

            runTest {
                coVerify {
                    analyticsClient.search(searchTerm)
                }
            }
        }

        @Test
        fun `Given an autocomplete API lookup, then we do not log analytics`() {
            viewModel.onAutocomplete(searchTerm)

            runTest {
                coVerify(exactly = 0) {
                    analyticsClient.autocomplete(searchTerm)
                }
            }
        }

        @Test
        fun `Given an autocomplete suggestion click, then log analytics`() {
            viewModel.onAutocompleteResultClick(searchTerm)

            runTest {
                coVerify {
                    analyticsClient.autocomplete(searchTerm)
                }
            }
        }

        @Test
        fun `Given a previous search click, then log analytics`() {
            viewModel.onPreviousSearchClick(searchTerm)

            runTest {
                coVerify {
                    analyticsClient.history(searchTerm)
                }
            }
        }

        @Test
        fun `Given a search, and a search result is clicked, then log analytics`() {
            runTest {
                viewModel.onSearchResultClicked("search result title", "search result link")

                coVerify {
                    analyticsClient.searchResultClick("search result title", "search result link")
                }
            }
        }

        @Test
        fun `Given a search, and a search result is clicked, then log visited item`() {
            runTest {
                viewModel.onSearchResultClicked("search result title", "search result link")

                coVerify {
                    visited.visitableItemClick(title = "search result title", url = "search result link")
                }
            }
        }
    }

    class UiStateTest {
        private val analyticsClient = mockk<AnalyticsClient>(relaxed = true)
        private val visited = mockk<Visited>(relaxed = true)
        private val dispatcher = UnconfinedTestDispatcher()
        private val repository = mockk<SearchRepo>(relaxed = true)
        private val searchTerm = "search term"
        private val resultWithNoSearchResponse = SearchResponse(total = 0, results = emptyList())
        private val resultWithOneResult = SearchResponse(
            total = 1,
            results = listOf(
                SearchResult(
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
        fun `Given a user has previous searches, when init, then emit previous searches`() {
            val previousSearches = listOf("dog", "cat", "tax")
            every { repository.previousSearches } returns flowOf(previousSearches)

            runTest {
                val viewModel = SearchViewModel(analyticsClient, visited, repository)
                val uiState = viewModel.uiState.value

                assertEquals(previousSearches, uiState.previousSearches)
                assertNull(uiState.suggestions)
                assertNull(uiState.searchResults)
                assertNull(uiState.error)
            }
        }

        @Test
        fun `Given a user clears the search, then emit previous searches`() {
            val previousSearches = listOf("dog", "cat", "tax")
            every { repository.previousSearches } returns flowOf(previousSearches)

            runTest {
                val viewModel = SearchViewModel(analyticsClient, visited, repository)
                viewModel.onClear()

                val uiState = viewModel.uiState.value

                assertEquals(previousSearches, uiState.previousSearches)
                assertNull(uiState.suggestions)
                assertNull(uiState.searchResults)
                assertNull(uiState.error)
            }
        }

        @Test
        fun `Given a user removes a previous search, then update repo`() {
            runTest {
                val viewModel = SearchViewModel(analyticsClient, visited, repository)
                viewModel.onRemovePreviousSearch("dog")
            }

            coVerify {
                repository.removePreviousSearch("dog")
            }
        }

        @Test
        fun `Given a user removes all previous searches, then update repo and emit previous searches`() {
            runTest {
                val viewModel = SearchViewModel(analyticsClient, visited, repository)
                viewModel.onRemoveAllPreviousSearches()
            }

            coVerify {
                repository.removeAllPreviousSearches()
            }
        }

        @Test
        fun `Given a search with a blank search term, then do nothing`() {
            val viewModel = SearchViewModel(analyticsClient, visited, repository)
            viewModel.onSearch(" ")

            runTest {
                val uiStates = mutableListOf<SearchUiState>()
                backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                    viewModel.uiState.toList(uiStates)
                }
                assertEquals(1, uiStates.size)
                assertNull(uiStates[0].searchResults)
                assertNull(uiStates[0].error)
            }

            coVerify(exactly = 0) {
                repository.performSearch(any())
            }
        }

        @Test
        fun `Given a search with a result, then emit search results`() {
            coEvery { repository.performSearch(searchTerm) } returns Success(resultWithOneResult)

            val viewModel = SearchViewModel(analyticsClient, visited, repository)
            viewModel.onSearch(searchTerm)

            runTest {
                val uiState = viewModel.uiState.value

                assertEquals(searchTerm, uiState.searchResults!!.searchTerm)
                assertNull(uiState.suggestions)
                assertEquals(1, uiState.searchResults.values.size)
                assertNull(uiState.error)
            }
        }

        @Test
        fun `Given a search without any results, then emit empty state`() {
            coEvery { repository.performSearch(searchTerm) } returns Success(resultWithNoSearchResponse)

            val viewModel = SearchViewModel(analyticsClient, visited, repository)
            viewModel.onSearch(searchTerm)

            runTest {
                val uiState = viewModel.uiState.value

                assertNull(uiState.suggestions)
                assertNull(uiState.searchResults)
                assertTrue(uiState.error is SearchUiState.Error.Empty)
            }
        }

        @Test
        fun `Given a search when the device is offline, then emit offline state`() {
            coEvery { repository.performSearch(searchTerm) } returns DeviceOffline()

            val viewModel = SearchViewModel(analyticsClient, visited, repository)
            viewModel.onSearch(searchTerm)

            runTest {
                val uiState = viewModel.uiState.value
                assertNull(uiState.suggestions)
                assertNull(uiState.searchResults)
                assertTrue(uiState.error is SearchUiState.Error.Offline)
            }
        }

        @Test
        fun `Given a search when the Search API is unavailable, then emit service error state`() {
            coEvery { repository.performSearch(searchTerm) } returns ServiceNotResponding()

            val viewModel = SearchViewModel(analyticsClient, visited, repository)
            viewModel.onSearch(searchTerm)

            runTest {
                val uiState = viewModel.uiState.value
                assertNull(uiState.suggestions)
                assertNull(uiState.searchResults)
                assertTrue(uiState.error is SearchUiState.Error.ServiceError)
            }
        }

        @Test
        fun `Given a search that returns an error, then emit service error state`() {
            coEvery { repository.performSearch(searchTerm) } returns Error()

            val viewModel = SearchViewModel(analyticsClient, visited, repository)
            viewModel.onSearch(searchTerm)

            runTest {
                val uiState = viewModel.uiState.value
                assertNull(uiState.suggestions)
                assertNull(uiState.searchResults)
                assertTrue(uiState.error is SearchUiState.Error.ServiceError)
            }
        }

        @Test
        fun `Given an autocomplete with one suggestion, then emit autocomplete suggestions`() {
            coEvery { repository.performLookup(searchTerm) } returns Success(AutocompleteResponse(suggestions = listOf("dog")))

            val viewModel = SearchViewModel(analyticsClient, visited, repository)
            viewModel.onAutocomplete(searchTerm)

            runTest {
                val uiState = viewModel.uiState.value

                assertEquals(searchTerm, uiState.suggestions!!.searchTerm)
                assertEquals(1, uiState.suggestions.values.size)
                assertNull(uiState.searchResults)
                assertNull(uiState.error)
            }
        }

        @Test
        fun `Given an autocomplete with no suggestions, then emit no autocomplete suggestions`() {
            coEvery { repository.performLookup(searchTerm) } returns Success(AutocompleteResponse(suggestions = emptyList()))

            val viewModel = SearchViewModel(analyticsClient, visited, repository)
            viewModel.onAutocomplete(searchTerm)

            runTest {
                val uiState = viewModel.uiState.value

                assertEquals(searchTerm, uiState.suggestions!!.searchTerm)
                assertEquals(0, uiState.suggestions.values.size)
                assertNull(uiState.searchResults)
                assertNull(uiState.error)
            }
        }

        @Test
        fun `Given an autocomplete with less than AUTOCOMPLETE_MIN_LENGTH chars, then emit previous suggestions`() {
            val previousSearches = listOf("dog", "cat", "tax")
            coEvery { repository.previousSearches } returns flowOf(previousSearches)

            runTest {
                val viewModel = SearchViewModel(analyticsClient, visited, repository)
                viewModel.onAutocomplete("a")

                val uiState = viewModel.uiState.value

                assertEquals(previousSearches, uiState.previousSearches)
                assertNull(uiState.suggestions)
                assertNull(uiState.searchResults)
                assertNull(uiState.error)
            }
        }
    }
}