package uk.govuk.app.search.data

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import uk.govuk.app.search.data.remote.AutocompleteApi
import uk.govuk.app.search.data.remote.model.AutocompleteResponse
import java.net.UnknownHostException

class AutocompleteRepoTest {
    private val autocompleteApi = mockk<AutocompleteApi>(relaxed = true)
    private val searchTerm = "house"
    private val responseWithNoSuggestions = AutocompleteResponse(suggestions = emptyList())
    private val responseWithOneSuggestion = AutocompleteResponse(suggestions = listOf("companies house"))
    private lateinit var autocompleteRepo: AutocompleteRepo

    @Before
    fun setup() {
        autocompleteRepo = AutocompleteRepo(autocompleteApi)
    }

    @Test
    fun `Perform autocomplete returns Success status when suggestions are found`() {
        coEvery { autocompleteApi.getSuggestions(searchTerm) } returns responseWithOneSuggestion

        val expected = Result.success(responseWithOneSuggestion)

        runTest {
            val actual = autocompleteRepo.performLookup(searchTerm)
            assertEquals(expected, actual)
        }
    }

    @Test
    fun `Perform autocomplete returns Empty status when no suggestions are found`() {
        coEvery { autocompleteApi.getSuggestions(searchTerm) } returns responseWithNoSuggestions

        val expected = Result.success(responseWithNoSuggestions)

        runTest {
            val actual = autocompleteRepo.performLookup(searchTerm)
            assertEquals(expected, actual)
        }
    }

    @Test
    fun `Perform autocomplete returns DeviceOffline status when the device is offline`() {
        coEvery { autocompleteApi.getSuggestions(searchTerm) } throws UnknownHostException()

        runTest {
            val result = autocompleteRepo.performLookup(searchTerm)
            assertTrue(result.isFailure)
        }
    }

    @Test
    fun `Perform autocomplete returns ServiceNotResponding status when the Autocomplete API is offline`() {
        val httpException = mockk<HttpException>(relaxed = true)
        coEvery { httpException.code() } returns 503
        coEvery { httpException.message() } returns "Service Unavailable"
        coEvery { autocompleteApi.getSuggestions(searchTerm) } throws httpException

        runTest {
            val result = autocompleteRepo.performLookup(searchTerm)
            assertTrue(result.isFailure)
        }
    }

    @Test
    fun `Perform autocomplete returns Error status when any unknown error occurs that has a message`() {
        val message = "Something very bad happened"
        coEvery { autocompleteApi.getSuggestions(searchTerm) } throws Exception(message)

        runTest {
            val result = autocompleteRepo.performLookup(searchTerm)
            assertTrue(result.isFailure)
        }
    }

    @Test
    fun `Perform autocomplete returns Error status when any unknown error occurs that has no message`() {
        coEvery { autocompleteApi.getSuggestions(searchTerm) } throws Exception()

        runTest {
            val result = autocompleteRepo.performLookup(searchTerm)
            assertTrue(result.isFailure)
        }
    }
}
