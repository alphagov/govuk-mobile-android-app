package uk.gov.govuk.home

import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import uk.gov.govuk.analytics.AnalyticsClient
import uk.gov.govuk.chat.ChatFeature

class HomeViewModelTest {
    private val analyticsClient = mockk<AnalyticsClient>(relaxed = true)
    private val chatFeature = mockk<ChatFeature>(relaxed = true)
    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        clearAllMocks()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Given a page view, and the user has opted into use Chat, then log analytics`() = runTest {
        val viewModel = HomeViewModel(analyticsClient, chatFeature)

        coEvery { chatFeature.hasOptedIn() } returns flowOf(true)

        viewModel.onPageView()

        advanceUntilIdle()

        verify {
            analyticsClient.screenViewWithType(
                screenClass = "HomeScreen",
                screenName = "Homepage",
                title = "Homepage",
                type = "chatOptIn"
            )
        }
    }

    @Test
    fun `Given a page view, and the user has opted out of using Chat, then log analytics`()  = runTest {
        val viewModel = HomeViewModel(analyticsClient, chatFeature)

        coEvery { chatFeature.hasOptedIn() } returns flowOf(false)

        viewModel.onPageView()

        advanceUntilIdle()

        verify {
            analyticsClient.screenViewWithType(
                screenClass = "HomeScreen",
                screenName = "Homepage",
                title = "Homepage",
                type = "chatOptOut"
            )
        }
    }
}
