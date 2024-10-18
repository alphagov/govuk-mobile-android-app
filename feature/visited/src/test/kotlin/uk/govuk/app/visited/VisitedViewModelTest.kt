package uk.govuk.app.visited

import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.experimental.runners.Enclosed
import org.junit.runner.RunWith
import uk.govuk.app.analytics.Analytics

@RunWith(Enclosed::class)
@OptIn(ExperimentalCoroutinesApi::class)
class VisitedViewModelTest {
    class AnalyticsTest {
        private val analytics = mockk<Analytics>(relaxed = true)
        private val viewModel = VisitedViewModel(analytics)
        private val dispatcher = UnconfinedTestDispatcher()

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
            Dispatchers.setMain(dispatcher)

            viewModel.onVisitedItemClicked("visited item title", "visited item title")

            runTest {
                coVerify {
                    analytics.visitedItemClick("visited item title", "visited item title")
                }
            }
        }
    }

    class UiStateTest {
        private val analytics = mockk<Analytics>(relaxed = true)
        private val viewModel = VisitedViewModel(analytics)

        @Test
        fun `Given there is a visited item, then the status in the view model is correct`() {
            val visitedItems = VisitedUiState(
                visited = listOf(
                    VisitedUi("GOV.UK", "https://www.gov.uk", "24 July 2024")
                )
            )

            /* TODO...
            coEvery { /* mock where the visited items are retrieved from */ } returns visitedItems
            */

            viewModel.onPageView()

            runTest {
                val result = viewModel.uiState.first()

                /* TODO: assertEquals(visitedItems, result) */
            }
        }

        @Test
        fun `Given there are no visited items, then the status in the view model are correct`() {
            val visitedItems = VisitedUiState(visited = emptyList())

            viewModel.onPageView()

            runTest {
                val result = viewModel.uiState.first()

                assertEquals(visitedItems, result)
            }
        }
    }
}
