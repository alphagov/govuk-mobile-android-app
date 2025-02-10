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
        val viewModel =
            VisitedViewModel(visitedRepo, visitedLocalDataSource, visited, analyticsClient)

        viewModel.onPageView()

        coVerify {
            analyticsClient.screenView(
                screenClass = "VisitedScreen",
                screenName = "Pages you've visited",
                title = "Pages you've visited"
            )
        }
    }

    @Test
    fun `Given an edit page view, then log analytics`() {
        val viewModel =
            VisitedViewModel(visitedRepo, visitedLocalDataSource, visited, analyticsClient)

        viewModel.onEditPageView()

        coVerify {
            analyticsClient.screenView(
                screenClass = "EditVisitedScreen",
                screenName = "Pages you've visited",
                title = "Pages you've visited"
            )
        }
    }

    @Test
    fun `Given a visited item is removed, then log analytics`() {
        val viewModel =
            VisitedViewModel(visitedRepo, visitedLocalDataSource, visited, analyticsClient)

        viewModel.onRemoveVisitedItem("Title")

        coVerify {
            analyticsClient.buttonFunction(
                text = "Title",
                section = "Pages you've visited",
                action = "Remove"
            )
        }
    }

    @Test
    fun `Given an edit button click, then log analytics`() {
        val viewModel =
            VisitedViewModel(visitedRepo, visitedLocalDataSource, visited, analyticsClient)

        viewModel.onEditClick()

        coVerify {
            analyticsClient.buttonFunction(
                text = "",
                section = "Pages you've visited",
                action = "Edit"
            )
        }
    }

    @Test
    fun `Given a remove button click, then log analytics`() {
        val viewModel =
            VisitedViewModel(visitedRepo, visitedLocalDataSource, visited, analyticsClient)

        viewModel.onRemoveClick()

        coVerify {
            analyticsClient.buttonFunction(
                text = "",
                section = "Pages you've visited",
                action = "Remove"
            )
        }
    }

    @Test
    fun `Given a select all button click, then log analytics`() {
        val viewModel =
            VisitedViewModel(visitedRepo, visitedLocalDataSource, visited, analyticsClient)

        viewModel.onSelectAllClick()

        coVerify {
            analyticsClient.buttonFunction(
                text = "",
                section = "Pages you've visited",
                action = "Select all"
            )
        }
    }

    @Test
    fun `Given a deselect all button click, then log analytics`() {
        val viewModel =
            VisitedViewModel(visitedRepo, visitedLocalDataSource, visited, analyticsClient)

        viewModel.onDeselectAllClick()

        coVerify {
            analyticsClient.buttonFunction(
                text = "",
                section = "Pages you've visited",
                action = "Deselect all"
            )
        }
    }

    @Test
    fun `Given a done button click, then log analytics`() {
        val viewModel =
            VisitedViewModel(visitedRepo, visitedLocalDataSource, visited, analyticsClient)

        viewModel.onDoneClick()

        coVerify {
            analyticsClient.buttonFunction(
                text = "",
                section = "Pages you've visited",
                action = "Done"
            )
        }
    }

    @Test
    fun `Given the user re-views a visited item, then run the insert or update function`() {
        val viewModel =
            VisitedViewModel(visitedRepo, visitedLocalDataSource, visited, analyticsClient)

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
                ),
                hasSelectedItems = false,
                hasAllSelectedItems = false
            )

        coEvery { visitedRepo.visitedItems } returns flowOf(visitedItems)

        val viewModel =
            VisitedViewModel(visitedRepo, visitedLocalDataSource, visited, analyticsClient)

        runTest {
            assertEquals(expected, viewModel.uiState.first())
        }
    }

    @Test
    fun `Given there are no visited items, then the status in the view model are correct`() {
        val visitedItems = emptyList<VisitedItemUi>()
        val expected = VisitedUiState(
            visited = emptyMap(),
            hasSelectedItems = false,
            hasAllSelectedItems = false
        )

        coEvery { visitedRepo.visitedItems } returns flowOf(visitedItems)

        val viewModel =
            VisitedViewModel(visitedRepo, visitedLocalDataSource, visited, analyticsClient)
        viewModel.onPageView()

        runTest {
            assertEquals(expected, viewModel.uiState.first())
        }
    }

    @Test
    fun `Given selected items, when onRemove is called, then the correct visited items are removed`() {
        val initialVisitedItems = listOf(
            VisitedItemUi("Title 1", "Url 1", 1024),
            VisitedItemUi("Title 2", "Url 2", 1024),
            VisitedItemUi("Title 3", "Url 3", 1024)
        )

        coEvery { visitedRepo.visitedItems } returns flowOf(initialVisitedItems)

        val viewModel =
            VisitedViewModel(visitedRepo, visitedLocalDataSource, visited, analyticsClient)

        viewModel.onSelect("Title 1", "Url 1")
        viewModel.onSelect("Title 3", "Url 3")

        runTest {
            viewModel.onRemove()

            coVerify(exactly = 2) { visitedLocalDataSource.remove(any(), any()) }
            coVerify { visitedLocalDataSource.remove("Title 1", "Url 1") }
            coVerify { visitedLocalDataSource.remove("Title 3", "Url 3") }
            coVerify { viewModel.onRemoveVisitedItem("Title 1") }
            coVerify { viewModel.onRemoveVisitedItem("Title 3") }
        }
    }

    @Test
    fun `Given an edit page view, then the initial ui state value of the selected items is false`() {
        val viewModel =
            VisitedViewModel(visitedRepo, visitedLocalDataSource, visited, analyticsClient)

        viewModel.uiState.value?.hasSelectedItems?.let { assertFalse(it) }
        viewModel.uiState.value?.hasAllSelectedItems?.let { assertFalse(it) }
    }

    @Test
    fun `Given no selected items, then the ui state value of the selected items is false`() {
        val initialVisitedItems = listOf(
            VisitedItemUi("Title 1", "Url 1", 1024),
            VisitedItemUi("Title 2", "Url 2", 1024)
        )

        coEvery { visitedRepo.visitedItems } returns flowOf(initialVisitedItems)

        val viewModel =
            VisitedViewModel(visitedRepo, visitedLocalDataSource, visited, analyticsClient)

        viewModel.uiState.value?.hasSelectedItems?.let { assertFalse(it) }
        viewModel.uiState.value?.hasAllSelectedItems?.let { assertFalse(it) }
    }

    @Test
    fun `Given an empty state, when onRemove is called, then there is nothing to do`() {
        val initialVisitedItems = emptyList<VisitedItemUi>()

        coEvery { visitedRepo.visitedItems } returns flowOf(initialVisitedItems)

        val viewModel =
            VisitedViewModel(visitedRepo, visitedLocalDataSource, visited, analyticsClient)

        runTest {
            viewModel.onRemove()

            coVerify(exactly = 0) { visitedLocalDataSource.remove(any(), any()) }
            coVerify(exactly = 0) { viewModel.onRemoveVisitedItem(any()) }
        }
    }

    @Test
    fun `Given a null state, when onRemove is called, then there is nothing to do`() {
        val initialVisitedItems = emptyList<VisitedItemUi>()

        coEvery { visitedRepo.visitedItems } returns flowOf(initialVisitedItems)

        val viewModel =
            VisitedViewModel(visitedRepo, visitedLocalDataSource, visited, analyticsClient)

        runTest {
            viewModel.onRemove()

            coVerify(exactly = 0) { visitedLocalDataSource.remove(any(), any()) }
            coVerify(exactly = 0) { viewModel.onRemoveVisitedItem(any()) }
        }
    }

    @Test
    fun `Given some visited items, when onSelectAll is called, then all items are selected and the selected status is correct`() {
        val initialVisitedItems = listOf(
            VisitedItemUi("Title 1", "Url 1", 1024),
            VisitedItemUi("Title 2", "Url 2", 1024)
        )

        coEvery { visitedRepo.visitedItems } returns flowOf(initialVisitedItems)

        val viewModel =
            VisitedViewModel(visitedRepo, visitedLocalDataSource, visited, analyticsClient)

        viewModel.onSelectAll()

        viewModel.uiState.value?.visited?.values?.forEach { items ->
            items.forEach { item ->
                assertTrue(item.isSelected)
            }
        }

        viewModel.uiState.value?.hasSelectedItems?.let { assertTrue(it) }
        viewModel.uiState.value?.hasAllSelectedItems?.let { assertTrue(it) }
    }

    @Test
    fun `Given no items, when onSelectAll is called, then no items are selected and the selected status is correct`() {
        val initialVisitedItems = emptyList<VisitedItemUi>()

        coEvery { visitedRepo.visitedItems } returns flowOf(initialVisitedItems)

        val viewModel =
            VisitedViewModel(visitedRepo, visitedLocalDataSource, visited, analyticsClient)

        viewModel.onSelectAll()

        viewModel.uiState.value?.hasSelectedItems?.let { assertTrue(it) }
        viewModel.uiState.value?.hasAllSelectedItems?.let { assertTrue(it) }
    }

    @Test
    fun `Given all selected items, when onDeselectAll is called, then all items are deselected and the selected status is correct`() {
        val initialVisitedItems = listOf(
            VisitedItemUi("Title 1", "Url 1", 1024),
            VisitedItemUi("Title 2", "Url 2", 1024)
        )

        coEvery { visitedRepo.visitedItems } returns flowOf(initialVisitedItems)

        val viewModel =
            VisitedViewModel(visitedRepo, visitedLocalDataSource, visited, analyticsClient)

        viewModel.onSelectAll()
        viewModel.onDeselectAll()

        viewModel.uiState.value?.visited?.values?.forEach { items ->
            items.forEach { item ->
                assertFalse(item.isSelected)
            }
        }

        viewModel.uiState.value?.hasSelectedItems?.let { assertFalse(it) }
        viewModel.uiState.value?.hasAllSelectedItems?.let { assertFalse(it) }
    }

    @Test
    fun `Given no selected items, when onDeselectAll is called, then all items are deselected and the selected status is correct`() {
        val initialVisitedItems = listOf(
            VisitedItemUi("Title 1", "Url 1", 1024),
            VisitedItemUi("Title 2", "Url 2", 1024)
        )

        coEvery { visitedRepo.visitedItems } returns flowOf(initialVisitedItems)

        val viewModel =
            VisitedViewModel(visitedRepo, visitedLocalDataSource, visited, analyticsClient)

        viewModel.onDeselectAll()

        viewModel.uiState.value?.visited?.values?.forEach { items ->
            items.forEach { item ->
                assertFalse(item.isSelected)
            }
        }

        viewModel.uiState.value?.hasSelectedItems?.let { assertFalse(it) }
        viewModel.uiState.value?.hasAllSelectedItems?.let { assertFalse(it) }
    }

    @Test
    fun `Given no items, when onDeselectAll is called, then no items are deselected and the selected status is correct`() {
        val initialVisitedItems = emptyList<VisitedItemUi>()

        coEvery { visitedRepo.visitedItems } returns flowOf(initialVisitedItems)

        val viewModel =
            VisitedViewModel(visitedRepo, visitedLocalDataSource, visited, analyticsClient)

        viewModel.onDeselectAll()

        viewModel.uiState.value?.hasSelectedItems?.let { assertFalse(it) }
        viewModel.uiState.value?.hasAllSelectedItems?.let { assertFalse(it) }
    }

    @Test
    fun `Given no selected items, when onSelect is called, then the appropriate item is selected and the selected status is correct`() {
        val initialVisitedItems = listOf(
            VisitedItemUi("Title 1", "Url 1", 1024),
            VisitedItemUi("Title 2", "Url 2", 1024)
        )

        coEvery { visitedRepo.visitedItems } returns flowOf(initialVisitedItems)

        val viewModel =
            VisitedViewModel(visitedRepo, visitedLocalDataSource, visited, analyticsClient)

        viewModel.onSelect("Title 2", "Url 2")

        viewModel.uiState.value?.visited?.values?.forEach { items ->
            items.forEach { item ->
                if (item.title == "Title 2" && item.url == "Url 2") {
                    assertTrue(item.isSelected)
                } else {
                    assertFalse(item.isSelected)
                }
            }
        }

        viewModel.uiState.value?.hasSelectedItems?.let { assertTrue(it) }
        viewModel.uiState.value?.hasAllSelectedItems?.let { assertFalse(it) }
    }

    @Test
    fun `Given one selected items, when onSelect is called for another, then the appropriate item is selected and the selected status is correct`() {
        val initialVisitedItems = listOf(
            VisitedItemUi("Title 1", "Url 1", 1024),
            VisitedItemUi("Title 2", "Url 2", 1024)
        )

        coEvery { visitedRepo.visitedItems } returns flowOf(initialVisitedItems)

        val viewModel =
            VisitedViewModel(visitedRepo, visitedLocalDataSource, visited, analyticsClient)

        viewModel.onSelect("Title 1", "Url 1")
        viewModel.onSelect("Title 2", "Url 2")

        viewModel.uiState.value?.visited?.values?.forEach { items ->
            items.forEach { item ->
                assertTrue(item.isSelected)
            }
        }

        viewModel.uiState.value?.hasSelectedItems?.let { assertTrue(it) }
        viewModel.uiState.value?.hasAllSelectedItems?.let { assertTrue(it) }
    }

    @Test
    fun `Given selected items, when onSelect is called, then the appropriate item is deselected and the selected status is correct`() {
        val initialVisitedItems = listOf(
            VisitedItemUi("Title 1", "Url 1", 1024),
            VisitedItemUi("Title 2", "Url 2", 1024)
        )

        coEvery { visitedRepo.visitedItems } returns flowOf(initialVisitedItems)

        val viewModel =
            VisitedViewModel(visitedRepo, visitedLocalDataSource, visited, analyticsClient)

        viewModel.onSelect("Title 1", "Url 1")
        viewModel.onSelect("Title 2", "Url 2")
        viewModel.onSelect("Title 2", "Url 2")

        viewModel.uiState.value?.visited?.values?.forEach { items ->
            items.forEach { item ->
                if (item.title == "Title 2" && item.url == "Url 2") {
                    assertFalse(item.isSelected)
                } else {
                    assertTrue(item.isSelected)
                }
            }
        }

        viewModel.uiState.value?.hasSelectedItems?.let { assertTrue(it) }
        viewModel.uiState.value?.hasAllSelectedItems?.let { assertFalse(it) }
    }

    @Test
    fun `Given one selected item, when onSelect is called, then the appropriate item is deselected and the selected status is correct`() {
        val initialVisitedItems = listOf(
            VisitedItemUi("Title 1", "Url 1", 1024),
            VisitedItemUi("Title 2", "Url 2", 1024)
        )

        coEvery { visitedRepo.visitedItems } returns flowOf(initialVisitedItems)

        val viewModel =
            VisitedViewModel(visitedRepo, visitedLocalDataSource, visited, analyticsClient)

        viewModel.onSelect("Title 2", "Url 2")
        viewModel.onSelect("Title 2", "Url 2")

        viewModel.uiState.value?.visited?.values?.forEach { items ->
            items.forEach { item ->
                assertFalse(item.isSelected)
            }
        }

        viewModel.uiState.value?.hasSelectedItems?.let { assertFalse(it) }
        viewModel.uiState.value?.hasAllSelectedItems?.let { assertFalse(it) }
    }

    @Test
    fun `Given no items, when onSelect is called, then nothing is selected and the selected status is correct`() {
        val initialVisitedItems = emptyList<VisitedItemUi>()

        coEvery { visitedRepo.visitedItems } returns flowOf(initialVisitedItems)

        val viewModel =
            VisitedViewModel(visitedRepo, visitedLocalDataSource, visited, analyticsClient)

        viewModel.onSelect("Title 1", "Url 1")

        viewModel.uiState.value?.visited?.values?.forEach { items ->
            items.forEach { item ->
                assertFalse(item.isSelected)
            }
        }

        viewModel.uiState.value?.hasSelectedItems?.let { assertFalse(it) }
    }

    @Test
    fun `Given some unselected items, when onSelect is called for an item that does not exist, then nothing is selected and the selected status is correct`() {
        val initialVisitedItems = listOf(
            VisitedItemUi("Title 1", "Url 1", 1024),
            VisitedItemUi("Title 2", "Url 2", 1024)
        )

        coEvery { visitedRepo.visitedItems } returns flowOf(initialVisitedItems)

        val viewModel =
            VisitedViewModel(visitedRepo, visitedLocalDataSource, visited, analyticsClient)

        viewModel.onSelect("Title 99", "Url 99")

        viewModel.uiState.value?.visited?.values?.forEach { items ->
            items.forEach { item ->
                assertFalse(item.isSelected)
            }
        }

        viewModel.uiState.value?.hasSelectedItems?.let { assertFalse(it) }
        viewModel.uiState.value?.hasAllSelectedItems?.let { assertFalse(it) }
    }
}
