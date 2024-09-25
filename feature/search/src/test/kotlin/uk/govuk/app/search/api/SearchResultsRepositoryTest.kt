package uk.govuk.app.search.api

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import uk.govuk.app.search.api_result.Result
import uk.govuk.app.search.api_result.Results
import uk.govuk.app.search.domain.ResultStatus
import uk.govuk.app.search.domain.SearchResult

class SearchResultsRepositoryTest {
    private val repository = mockk<SearchResultsRepository>(relaxed = true)
    private val searchTerm = "search term"
    private val resultWithOneResult = Results(
        total = 1,
        results = listOf(
            Result(
                title = "title",
                description = "description",
                link = "link"
            )
        )
    )
    private val resultWithNoResults = Results(total = 0, results = emptyList())

    @Test
    fun `getSearchResults returns Success status when results are found`() = runTest {
        coEvery { repository.getSearchResults(searchTerm) } returns
            SearchResult(ResultStatus.Success, resultWithOneResult)

        val expectedResult = SearchResult(ResultStatus.Success, resultWithOneResult)
        val actualResult = repository.getSearchResults(searchTerm)

        assertEquals(expectedResult, actualResult)
    }

    @Test
    fun `getSearchResults returns Empty status when no results are found`() = runTest {
        coEvery { repository.getSearchResults(searchTerm) } returns
            SearchResult(ResultStatus.Empty, resultWithNoResults)

        val expectedResult = SearchResult(ResultStatus.Empty, resultWithNoResults)
        val actualResult = repository.getSearchResults(searchTerm)

        assertEquals(expectedResult, actualResult)
    }

    @Test
    fun `getSearchResults returns DeviceOffline status when the device is offline`() = runTest {
        coEvery { repository.getSearchResults(searchTerm) } returns
            SearchResult(ResultStatus.DeviceOffline, resultWithNoResults)

        val expectedResult = SearchResult(ResultStatus.DeviceOffline, resultWithNoResults)
        val actualResult = repository.getSearchResults(searchTerm)

        assertEquals(expectedResult, actualResult)
    }

    @Test
    fun `getSearchResults returns ServiceNotResponding status when the Search API is offline`() = runTest {
        coEvery { repository.getSearchResults(searchTerm) } returns
            SearchResult(ResultStatus.ServiceNotResponding, resultWithNoResults)

        val expectedResult = SearchResult(ResultStatus.ServiceNotResponding, resultWithNoResults)
        val actualResult = repository.getSearchResults(searchTerm)

        assertEquals(expectedResult, actualResult)
    }

    @Test
    fun `getSearchResults returns Error status when any unknown error occurs`() = runTest {
        val errorMessage = "error message"
        coEvery { repository.getSearchResults(searchTerm) } returns
            SearchResult(ResultStatus.Error(errorMessage), resultWithNoResults)

        val expectedResult = SearchResult(ResultStatus.Error(errorMessage), resultWithNoResults)
        val actualResult = repository.getSearchResults(searchTerm)

        assertEquals(expectedResult, actualResult)
    }
}
