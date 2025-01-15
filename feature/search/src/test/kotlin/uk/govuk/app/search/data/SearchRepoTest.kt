package uk.govuk.app.search.data

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import uk.govuk.app.search.data.local.SearchLocalDataSource
import uk.govuk.app.search.data.local.model.LocalSearchItem
import uk.govuk.app.search.data.remote.SearchApi
import uk.govuk.app.search.data.remote.model.SearchResponse
import uk.govuk.app.search.data.remote.model.SearchResult
import uk.govuk.app.search.domain.SearchConfig
import java.net.UnknownHostException

class SearchRepoTest {

    private val searchApi = mockk<SearchApi>(relaxed = true)
    private val localDataSource = mockk<SearchLocalDataSource>(relaxed = true)
    private val searchTerm = "search term"
    private val responseWithNoSearchResults = SearchResponse(total = 0, results = emptyList())
    private val responseWithOneSearchResult = SearchResponse(
        total = 1,
        results = listOf(
            SearchResult(
                title = "title",
                description = "description",
                link = "link"
            )
        )
    )
    private lateinit var searchRepo: SearchRepo

    @Before
    fun setup() {
        searchRepo = SearchRepo(searchApi, localDataSource)
    }

    @Test
    fun `Fetch previous searches returns previous searches`() {
        coEvery { localDataSource.fetchPreviousSearches() } returns listOf(
            LocalSearchItem().apply { searchTerm = "dog" },
            LocalSearchItem().apply { searchTerm = "cat" },
            LocalSearchItem().apply { searchTerm = "tax" }
        )

        val expected = listOf("dog", "cat", "tax")

        runTest {
            val actual = searchRepo.fetchPreviousSearches()
            assertEquals(expected, actual)
        }
    }

    @Test
    fun `Remove previous search updates local data source`() {
        runTest {
            searchRepo.removePreviousSearch("searchTerm")
        }

        coVerify {
            localDataSource.removePreviousSearch("searchTerm")
        }
    }

    @Test
    fun `Remove all previous searches updates local data source`() {
        runTest {
            searchRepo.removeAllPreviousSearches()
        }

        coVerify {
            localDataSource.removeAllPreviousSearches()
        }
    }

    @Test
    fun `Perform search returns Success status when results are found`() {
        coEvery { searchApi.getSearchResults(searchTerm, SearchConfig.DEFAULT_RESULTS_PER_PAGE) } returns responseWithOneSearchResult

        val expected = Result.success(responseWithOneSearchResult)

        runTest {
            val actual = searchRepo.performSearch(searchTerm, 10)
            assertEquals(expected, actual)
        }
    }

    @Test
    fun `Perform search returns Empty status when no results are found`() {
        coEvery { searchApi.getSearchResults(searchTerm, SearchConfig.DEFAULT_RESULTS_PER_PAGE) } returns responseWithNoSearchResults

        val expected = Result.success(responseWithNoSearchResults)

        runTest {
            val actual = searchRepo.performSearch(searchTerm, 10)
            assertEquals(expected, actual)
        }
    }

    @Test
    fun `Perform search returns DeviceOffline status when the device is offline`() {
        coEvery { searchApi.getSearchResults(searchTerm, SearchConfig.DEFAULT_RESULTS_PER_PAGE) } throws UnknownHostException()

        runTest {
            val result = searchRepo.performSearch(searchTerm, 10)
            assertTrue(result.isFailure)
        }
    }

    @Test
    fun `Perform search returns ServiceNotResponding status when the Search API is offline`() {
        val httpException = mockk<HttpException>(relaxed = true)
        coEvery { httpException.code() } returns 503
        coEvery { httpException.message() } returns "Service Unavailable"
        coEvery { searchApi.getSearchResults(searchTerm, SearchConfig.DEFAULT_RESULTS_PER_PAGE) } throws httpException

        runTest {
            val result = searchRepo.performSearch(searchTerm, 10)
            assertTrue(result.isFailure)
        }
    }

    @Test
    fun `Perform search returns Error status when any unknown error occurs that has a message`() {
        val message = "Something very bad happened"
        coEvery { searchApi.getSearchResults(searchTerm, SearchConfig.DEFAULT_RESULTS_PER_PAGE) } throws Exception(message)

        runTest {
            val result = searchRepo.performSearch(searchTerm, 10)
            assertTrue(result.isFailure)
        }
    }

    @Test
    fun `Perform search returns Error status when any unknown error occurs that has no message`() {
        coEvery { searchApi.getSearchResults(searchTerm, SearchConfig.DEFAULT_RESULTS_PER_PAGE) } throws Exception()

        runTest {
            val result = searchRepo.performSearch(searchTerm, 10)
            assertTrue(result.isFailure)
        }
    }
}
