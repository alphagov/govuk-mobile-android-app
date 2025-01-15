package uk.govuk.app.search.data

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.HttpException
import uk.govuk.app.search.data.remote.SearchApi
import uk.govuk.app.search.data.remote.model.SearchResult
import uk.govuk.app.search.data.remote.model.SearchResponse
import uk.govuk.app.search.domain.SearchConfig
import java.net.UnknownHostException

class SearchRepoTest {

    private val searchApi = mockk<SearchApi>(relaxed = true)
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

    @Test
    fun `initSearch returns Success status when results are found`() {
        coEvery { searchApi.getSearchResults(searchTerm, SearchConfig.DEFAULT_RESULTS_PER_PAGE) } returns responseWithOneSearchResult

        val repo = SearchRepo(searchApi)

        val expected = Result.success(responseWithOneSearchResult)

        runTest {
            val actual = repo.performSearch(searchTerm, 10)
            assertEquals(expected, actual)
        }
    }

    @Test
    fun `initSearch returns Empty status when no results are found`() {
        coEvery { searchApi.getSearchResults(searchTerm, SearchConfig.DEFAULT_RESULTS_PER_PAGE) } returns responseWithNoSearchResults

        val repo = SearchRepo(searchApi)

        val expected = kotlin.Result.success(responseWithNoSearchResults)

        runTest {
            val actual = repo.performSearch(searchTerm, 10)
            assertEquals(expected, actual)
        }
    }

    @Test
    fun `initSearch returns DeviceOffline status when the device is offline`() {
        coEvery { searchApi.getSearchResults(searchTerm, SearchConfig.DEFAULT_RESULTS_PER_PAGE) } throws UnknownHostException()

        val repo = SearchRepo(searchApi)

        runTest {
            val result = repo.performSearch(searchTerm, 10)
            assertTrue(result.isFailure)
        }
    }

    @Test
    fun `initSearch returns ServiceNotResponding status when the Search API is offline`() {
        val httpException = mockk<HttpException>(relaxed = true)
        coEvery { httpException.code() } returns 503
        coEvery { httpException.message() } returns "Service Unavailable"
        coEvery { searchApi.getSearchResults(searchTerm, SearchConfig.DEFAULT_RESULTS_PER_PAGE) } throws httpException

        val repo = SearchRepo(searchApi)

        runTest {
            val result = repo.performSearch(searchTerm, 10)
            assertTrue(result.isFailure)
        }
    }

    @Test
    fun `initSearch returns Error status when any unknown error occurs that has a message`() {
        val message = "Something very bad happened"
        coEvery { searchApi.getSearchResults(searchTerm, SearchConfig.DEFAULT_RESULTS_PER_PAGE) } throws Exception(message)

        val repo = SearchRepo(searchApi)

        runTest {
            val result = repo.performSearch(searchTerm, 10)
            assertTrue(result.isFailure)
        }
    }

    @Test
    fun `initSearch returns Error status when any unknown error occurs that has no message`() {
        coEvery { searchApi.getSearchResults(searchTerm, SearchConfig.DEFAULT_RESULTS_PER_PAGE) } throws Exception()

        val repo = SearchRepo(searchApi)

        runTest {
            val result = repo.performSearch(searchTerm, 10)
            assertTrue(result.isFailure)
        }
    }
}
