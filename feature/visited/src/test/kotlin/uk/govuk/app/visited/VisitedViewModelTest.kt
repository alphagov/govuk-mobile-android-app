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
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import uk.govuk.app.analytics.AnalyticsClient
import uk.govuk.app.visited.data.VisitedRepo
import uk.govuk.app.visited.data.localDateFormatter
import uk.govuk.app.visited.data.store.VisitedLocalDataSource
import uk.govuk.app.visited.domain.model.VisitedItemUi
import uk.govuk.app.visited.ui.model.VisitedUi
import java.time.LocalDateTime
import java.time.ZoneOffset

@OptIn(ExperimentalCoroutinesApi::class)
class VisitedViewModelTest {
    private val dispatcher = UnconfinedTestDispatcher()
    private val visitedRepo = mockk<VisitedRepo>(relaxed = true)
    private val analyticsClient = mockk<AnalyticsClient>(relaxed = true)
    private val visitedLocalDataSource = mockk<VisitedLocalDataSource>(relaxed = true)
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
        val viewModel = VisitedViewModel(visitedRepo, visitedLocalDataSource, visited, analyticsClient)

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
        val viewModel = VisitedViewModel(visitedRepo, visitedLocalDataSource, visited, analyticsClient)

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
        val viewModel = VisitedViewModel(visitedRepo, visitedLocalDataSource, visited, analyticsClient)

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

        val viewModel = VisitedViewModel(visitedRepo, visitedLocalDataSource, visited, analyticsClient)

        runTest {
            assertEquals(expected, viewModel.uiState.first())
        }
    }

    @Test
    fun `Given there are no visited items, then the status in the view model are correct`() {
        val visitedItems = emptyList<VisitedItemUi>()
        val expected = VisitedUiState(visited = emptyMap())

        coEvery { visitedRepo.visitedItems } returns flowOf(visitedItems)

        val viewModel = VisitedViewModel(visitedRepo, visitedLocalDataSource, visited, analyticsClient)
        viewModel.onPageView()

        runTest {
            assertEquals(expected, viewModel.uiState.first())
        }
    }

    @Test
    fun `Given selected items, when removeSelectedItems is called, then the correct visited items are removed`() {
        val viewModel = VisitedViewModel(visitedRepo, visitedLocalDataSource, visited, analyticsClient)

        val initialVisitedItems = listOf(
            VisitedUi("Title 1", "Url 1", "1 January", isSelected = true),
            VisitedUi("Title 2", "Url 2", "1 January", isSelected = false),
            VisitedUi("Title 3", "Url 3", "1 January", isSelected = true)
        )

        viewModel.setUiStateForTest(VisitedUiState(visited = mapOf("section" to initialVisitedItems)))

        runTest {
            viewModel.removeSelectedItems()

            coVerify(exactly = 2) { visitedLocalDataSource.remove(any(), any()) }
            coVerify { visitedLocalDataSource.remove("Title 1", "Url 1") }
            coVerify { visitedLocalDataSource.remove("Title 3", "Url 3") }
        }
    }

    @Test
    fun `Given an edit page view, then the initial ui state value of hasSelectedItems is false`() {
        val viewModel = VisitedViewModel(visitedRepo, visitedLocalDataSource, visited, analyticsClient)

        viewModel.updateHasSelectedItems()

        viewModel.uiState.value?.hasSelectedItems?.let { assertFalse(it) }
    }

    @Test
    fun `Given a selected item, then the ui state value of hasSelectedItems is true`() {
        val viewModel = VisitedViewModel(visitedRepo, visitedLocalDataSource, visited, analyticsClient)

        val initialVisitedItems = listOf(
            VisitedUi("Title 1", "Url 1", "1 January", isSelected = true),
            VisitedUi("Title 2", "Url 2", "1 January", isSelected = false)
        )

        viewModel.setUiStateForTest(VisitedUiState(visited = mapOf("section" to initialVisitedItems)))

        viewModel.updateHasSelectedItems()

        viewModel.uiState.value?.hasSelectedItems?.let { assertTrue(it) }
    }

    @Test
    fun `Given no selected items, then the ui state value of hasSelectedItems is false`() {
        val viewModel = VisitedViewModel(visitedRepo, visitedLocalDataSource, visited, analyticsClient)

        val initialVisitedItems = listOf(
            VisitedUi("Title 1", "Url 1", "1 January", isSelected = false),
            VisitedUi("Title 2", "Url 2", "1 January", isSelected = false)
        )

        viewModel.setUiStateForTest(VisitedUiState(visited = mapOf("section" to initialVisitedItems)))

        viewModel.updateHasSelectedItems()

        viewModel.uiState.value?.hasSelectedItems?.let { assertFalse(it) }
    }

    @Test
    fun `Given an empty state, when removeSelectedItems is called, then there is nothing to do`() {
        val viewModel = VisitedViewModel(visitedRepo, visitedLocalDataSource, visited, analyticsClient)
        viewModel.setUiStateForTest(VisitedUiState(visited = emptyMap()))

        runTest {
            viewModel.removeSelectedItems()

            coVerify(exactly = 0) { visitedLocalDataSource.remove(any(), any()) }

            assertTrue(viewModel.getUiStateForTest()?.visited.isNullOrEmpty())
        }
    }

    @Test
    fun `Given a null state, when removeSelectedItems is called, then there is nothing to do`() {
        val viewModel = VisitedViewModel(visitedRepo, visitedLocalDataSource, visited, analyticsClient)
        viewModel.setUiStateForTest(null)

        runTest {
            viewModel.removeSelectedItems()

            coVerify(exactly = 0) { visitedLocalDataSource.remove(any(), any()) }

            assertNull(viewModel.getUiStateForTest())
        }
    }
}
