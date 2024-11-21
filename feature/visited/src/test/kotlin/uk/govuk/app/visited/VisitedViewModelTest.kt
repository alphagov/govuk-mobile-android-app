package uk.govuk.app.visited

import io.mockk.coEvery
import io.mockk.coVerify
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
import uk.govuk.app.analytics.AnalyticsClient
import uk.govuk.app.visited.data.VisitedRepo
import uk.govuk.app.visited.data.localDateFormatter
import uk.govuk.app.visited.domain.model.VisitedItemUi
import uk.govuk.app.visited.ui.model.VisitedUi
import java.time.LocalDateTime
import java.time.ZoneOffset

@OptIn(ExperimentalCoroutinesApi::class)
class VisitedViewModelTest {
    private val dispatcher = UnconfinedTestDispatcher()
    private val visitedRepo = mockk<VisitedRepo>(relaxed = true)
    private val analyticsClient = mockk<AnalyticsClient>(relaxed = true)
    private val visited = mockk<Visited>(relaxed = true)

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
        val viewModel = VisitedViewModel(visitedRepo, visited, analyticsClient)

        viewModel.onPageView()

        verify {
            analyticsClient.screenView(
                screenClass = "VisitedScreen",
                screenName = "Pages you've visited",
                title = "Pages you've visited"
            )
        }
    }

    @Test
    fun `Given an edit page view, then log analytics`() {
        val viewModel = VisitedViewModel(visitedRepo, visited, analyticsClient)

        viewModel.onEditPageView()

        verify {
            analyticsClient.screenView(
                screenClass = "EditVisitedScreen",
                screenName = "Pages you've visited",
                title = "Pages you've visited"
            )
        }
    }

    @Test
    fun `Given the user re-views a visited item, then run the insert or update function`() {
        val viewModel = VisitedViewModel(visitedRepo, visited, analyticsClient)

        runTest {
            viewModel.onVisitedItemClicked("visited item title", "visited item title")

            coVerify {
                visited.visitableItemClick("visited item title", "visited item title")
            }
        }
    }

    @Test
    fun `Given there are visited items, then the status in the view model is correct`() {
        val today = LocalDateTime.now()

        val visitedItems = listOf(
            VisitedItemUi(
                title = "GOV.UK",
                url = "https://www.gov.uk",
                lastVisited = today.toEpochSecond(ZoneOffset.UTC)
            ),
            VisitedItemUi(
                title = "Google",
                url = "https://www.google.com",
                lastVisited = today.toEpochSecond(ZoneOffset.UTC)
            )
        )

        val expected =
            VisitedUiState(
                visited = mapOf(
                    SectionTitles().today to listOf(
                        VisitedUi(
                            title = "GOV.UK",
                            url = "https://www.gov.uk",
                            lastVisited = localDateFormatter(today.toEpochSecond(ZoneOffset.UTC))
                        ),
                        VisitedUi(
                            title = "Google",
                            url = "https://www.google.com",
                            lastVisited = localDateFormatter(today.toEpochSecond(ZoneOffset.UTC))
                        )
                    )
                )
            )

        coEvery { visitedRepo.visitedItems } returns flowOf(visitedItems)

        val viewModel = VisitedViewModel(visitedRepo, visited, analyticsClient)

        runTest {
            assertEquals(expected, viewModel.uiState.first())
        }
    }

    @Test
    fun `Given there are no visited items, then the status in the view model are correct`() {
        val visitedItems = emptyList<VisitedItemUi>()
        val expected = VisitedUiState(visited = emptyMap())

        coEvery { visitedRepo.visitedItems } returns flowOf(visitedItems)

        val viewModel = VisitedViewModel(visitedRepo, visited, analyticsClient)
        viewModel.onPageView()

        runTest {
            assertEquals(expected, viewModel.uiState.first())
        }
    }
}
