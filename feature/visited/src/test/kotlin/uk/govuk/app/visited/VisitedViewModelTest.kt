package uk.govuk.app.visited

import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import uk.govuk.app.analytics.Analytics
import uk.govuk.app.visited.data.VisitedRepo
import uk.govuk.app.visited.data.model.VisitedItem
import uk.govuk.app.visited.ui.model.VisitedUi

@OptIn(ExperimentalCoroutinesApi::class)
class VisitedViewModelTest {
    private val dispatcher = UnconfinedTestDispatcher()
    private val visitedRepo = mockk<VisitedRepo>(relaxed = true)
    private val analytics = mockk<Analytics>(relaxed = true)
    private val viewModel = VisitedViewModel(visitedRepo, analytics)

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Given a page view, then log analytics`() {
        viewModel.onPageView()

        verify {
            analytics.screenView(
                screenClass = "VisitedScreen",
                screenName = "Pages you've visited",
                title = "Pages you've visited"
            )
        }
    }

    @Test
    fun `Given the user views the visited items screen, and a visited item is clicked, then log analytics`() {
        viewModel.onVisitedItemClicked("visited item title", "visited item title")

        runTest {
            coVerify {
                analytics.visitedItemClick("visited item title", "visited item title")
            }
        }
    }

    @Test
    fun `Given there are visited items, then the status in the view model is correct`() {
        val visitedItems = listOf(
            VisitedItem().apply {
                title = "GOV.UK"
                url = "https://www.gov.uk"
                lastVisited = "24 July 2024"
            },
            VisitedItem().apply {
                title = "Google"
                url = "https://www.google.com"
                lastVisited = "24 July 2024"
            }
        )

        val expected =
            VisitedUiState(
                visited = listOf(
                    VisitedUi(
                        title = "GOV.UK",
                        url = "https://www.gov.uk",
                        lastVisited = "24 July 2024"
                    ),
                    VisitedUi(
                        title = "Google",
                        url = "https://www.google.com",
                        lastVisited = "24 July 2024"
                    )
                )
            )

        val viewModel = VisitedViewModel(visitedRepo, analytics)

        every { visitedRepo.visitedItems } returns flowOf(visitedItems)

        viewModel.onPageView()

        runTest {
            assertEquals(expected, viewModel.uiState.first())
        }
    }

    @Test
    fun `Given there are no visited items, then the status in the view model are correct`() {
        val visitedItems = emptyList<VisitedItem>()
        val expected = VisitedUiState(visited = emptyList())
        val viewModel = VisitedViewModel(visitedRepo, analytics)

        every { visitedRepo.visitedItems } returns flowOf(visitedItems)

        viewModel.onPageView()

        runTest {
            assertEquals(expected, viewModel.uiState.first())
        }
    }
}
