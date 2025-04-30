package uk.govuk.app.local

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
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
    private val dispatcher = UnconfinedTestDispatcher()

    private lateinit var viewModel: LocalViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        viewModel = LocalViewModel(analyticsClient, localRepo)
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
        val viewModel = LocalViewModel(analyticsClient, localRepo)
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
        val viewModel = LocalViewModel(analyticsClient, localRepo)
        viewModel.onSearchPostcode("button text", "")

        verify {
            analyticsClient.buttonClick(
                text = "button text",
                section = "Local"
            )
        }
    }

    @Test
    fun `Given an error is displayed, then log analytics`() {
        val viewModel = LocalViewModel(analyticsClient, localRepo)
        viewModel.onErrorStatus(message = "No postcode")

        verify {
            analyticsClient.screenView(
                screenClass = "LocalLookupScreen",
                screenName = "Local Lookup",
                title = "No postcode"
            )
        }
    }

    @Test
    fun `onSearchPostcode calls analyticsClient and performGetLocalPostcode`() = runTest {
        val buttonText = "Search"
        val postcode = "E18QS"
        val localAuthorityResult: LocalAuthorityResult = mockk<LocalAuthorityResult>(relaxed = true)

        coEvery { localRepo.performGetLocalPostcode(postcode) } returns Success(localAuthorityResult)

        val viewModel = LocalViewModel(analyticsClient, localRepo)
        viewModel.onSearchPostcode(buttonText, postcode)

        verify { analyticsClient.buttonClick(text = buttonText, section = "Local") }
        coVerify { localRepo.performGetLocalPostcode(postcode) }
    }

    @Test
    fun `onSearchPostcode calls analyticsClient and performGetLocalPostcode - returns 400 invalid postcode`() = runTest {
        val buttonText = "Search"
        val postcode = "E18QS"

        coEvery {
            localRepo.performGetLocalPostcode(postcode)
        } returns Success(LocalAuthorityResult.InvalidPostcode)

        val viewModel = LocalViewModel(analyticsClient, localRepo)
        viewModel.onSearchPostcode(buttonText, postcode)

        verify { analyticsClient.buttonClick(text = buttonText, section = "Local") }
        coVerify { localRepo.performGetLocalPostcode(postcode) }
    }

    @Test
    fun `onSearchPostcode calls analyticsClient and performGetLocalPostcode - returns 404 not found`() = runTest {
        val buttonText = "Search"
        val postcode = "E18QS"

        coEvery {
            localRepo.performGetLocalPostcode(postcode)
        } returns Success(LocalAuthorityResult.PostcodeNotFound)

        val viewModel = LocalViewModel(analyticsClient, localRepo)
        viewModel.onSearchPostcode(buttonText, postcode)

        verify { analyticsClient.buttonClick(text = buttonText, section = "Local") }
        coVerify { localRepo.performGetLocalPostcode(postcode) }
    }

    @Test
    fun `onSearchPostcode calls analyticsClient - performGetLocalPostcode is not called - returns 418 no postcode`() = runTest {
        val buttonText = "Search"
        val postcode = ""

        coEvery {
            localRepo.performGetLocalPostcode(postcode)
        } returns Success(LocalAuthorityResult.PostcodeEmptyOrNull)

        val viewModel = LocalViewModel(analyticsClient, localRepo)
        viewModel.onSearchPostcode(buttonText, postcode)

        verify { analyticsClient.buttonClick(text = buttonText, section = "Local") }
        coVerify(exactly = 0) { localRepo.performGetLocalPostcode(postcode) }
    }

    @Test
    fun `onSearchLocalAuthority calls analyticsClient and performGetLocalAuthority`() = runTest {
        val slug = "dorset"
        val localAuthorityResult: LocalAuthorityResult = mockk<LocalAuthorityResult>(relaxed = true)

        coEvery { localRepo.performGetLocalAuthority(slug) } returns Success(localAuthorityResult)

        val viewModel = LocalViewModel(analyticsClient, localRepo)
        viewModel.onSearchLocalAuthority(slug)

        coVerify { localRepo.performGetLocalAuthority(slug) }
    }
}
