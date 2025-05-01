package uk.govuk.app.local

import android.content.Context
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import uk.gov.govuk.analytics.AnalyticsClient
import uk.gov.govuk.data.model.Result.Success
import uk.govuk.app.local.data.LocalRepo
import uk.govuk.app.local.data.remote.model.LocalAuthorityResult

@OptIn(ExperimentalCoroutinesApi::class)
class LocalViewModelTest {
    private val analyticsClient = mockk<AnalyticsClient>(relaxed = true)
    private val localRepo = mockk<LocalRepo>(relaxed = true)
    private val context = mockk<Context>(relaxed = true)
    private val dispatcher = UnconfinedTestDispatcher()

    private lateinit var viewModel: LocalViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        viewModel = LocalViewModel(analyticsClient, localRepo, context)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Given an explainer page view, then log analytics`() {
        viewModel.onExplainerPageView()

        verify {
            analyticsClient.screenView(
                screenClass = "LocalExplainerScreen",
                screenName = "Local Explainer",
                title = "Local Explainer"
            )
        }
    }

    @Test
    fun `Given an explainer button click, then log analytics`() {
        viewModel.onExplainerButtonClick("button text")

        verify {
            analyticsClient.buttonClick(
                text = "button text",
                section = "Local"
            )
        }
    }

    @Test
    fun `Given a lookup page view, then log analytics`() {
        viewModel.onLookupPageView()

        verify {
            analyticsClient.screenView(
                screenClass = "LocalLookupScreen",
                screenName = "Local Lookup",
                title = "Local Lookup"
            )
        }
    }

    @Test
    fun `Given a postcode lookup, then log analytics`() {
        viewModel.onSearchPostcode("button text", "")

        verify {
            analyticsClient.buttonClick(
                text = "button text",
                section = "Local"
            )
        }
    }

    @Test
    fun `Given a uiState that is an error, when the postcode is changed, then is clears the error`() {
        viewModel.onSearchPostcode("text", " ")

        viewModel.onPostcodeChange()

        assertNull(viewModel.uiState.value)
    }

    @Test
    fun `onSearchPostcode calls analyticsClient and performGetLocalPostcode`() = runTest {
        val buttonText = "Search"
        val postcode = "E18QS"
        val localAuthorityResult: LocalAuthorityResult = mockk<LocalAuthorityResult>(relaxed = true)

        coEvery { localRepo.performGetLocalPostcode(postcode) } returns Success(localAuthorityResult)

        viewModel.onSearchPostcode(buttonText, postcode)

        verify { analyticsClient.buttonClick(text = buttonText, section = "Local") }
        coVerify { localRepo.performGetLocalPostcode(postcode) }
    }

    @Test
    fun `onSearchPostcode calls analyticsClient and performGetLocalPostcode - returns invalid postcode - emits error`() = runTest {
        val buttonText = "Search"
        val postcode = "E18QS"

        coEvery {
            localRepo.performGetLocalPostcode(postcode)
        } returns Success(LocalAuthorityResult.InvalidPostcode)

        val errorMessage = "invalid postcode error"

        every { context.getString(R.string.local_invalid_postcode_message) } returns errorMessage

        viewModel.onSearchPostcode(buttonText, postcode)

        verify {
            analyticsClient.buttonClick(text = buttonText, section = "Local")
            analyticsClient.screenView(
                screenClass = "LocalLookupScreen",
                screenName = "Local Lookup",
                title = errorMessage
            )
        }
        coVerify { localRepo.performGetLocalPostcode(postcode) }
        val uiState = viewModel.uiState.value as LocalUiState.Error
        assertEquals(R.string.local_invalid_postcode_message, uiState.message)
    }

    @Test
    fun `onSearchPostcode calls analyticsClient and performGetLocalPostcode - returns postcode not found - emits error`() = runTest {
        val buttonText = "Search"
        val postcode = "E18QS"

        coEvery {
            localRepo.performGetLocalPostcode(postcode)
        } returns Success(LocalAuthorityResult.PostcodeNotFound)

        val errorMessage = "postcode not found error"

        every { context.getString(R.string.local_not_found_postcode_message) } returns errorMessage

        viewModel.onSearchPostcode(buttonText, postcode)

        verify { analyticsClient.buttonClick(text = buttonText, section = "Local") }
        coVerify { localRepo.performGetLocalPostcode(postcode) }
        val uiState = viewModel.uiState.value as LocalUiState.Error
        assertEquals(R.string.local_not_found_postcode_message, uiState.message)
    }

    @Test
    fun `onSearchPostcode calls analyticsClient - performGetLocalPostcode is not called - returns no postcode - emits error`() = runTest {
        val buttonText = "Search"
        val postcode = " "

        val errorMessage = "no postcode error"

        every { context.getString(R.string.local_no_postcode_message) } returns errorMessage

        viewModel.onSearchPostcode(buttonText, postcode)

        verify {
            analyticsClient.buttonClick(text = buttonText, section = "Local")
            analyticsClient.screenView(
                screenClass = "LocalLookupScreen",
                screenName = "Local Lookup",
                title = errorMessage
            )
        }
        coVerify(exactly = 0) { localRepo.performGetLocalPostcode(any()) }
        val uiState = viewModel.uiState.value as LocalUiState.Error
        assertEquals(R.string.local_no_postcode_message, uiState.message)
    }

    @Test
    fun `onSearchLocalAuthority calls analyticsClient and performGetLocalAuthority`() = runTest {
        val slug = "dorset"
        val localAuthorityResult: LocalAuthorityResult = mockk<LocalAuthorityResult>(relaxed = true)

        coEvery { localRepo.performGetLocalAuthority(slug) } returns Success(localAuthorityResult)

        viewModel.onSearchLocalAuthority(slug)

        coVerify { localRepo.performGetLocalAuthority(slug) }
    }
}
