package uk.govuk.app.topics

import io.mockk.every
import io.mockk.mockk
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
import uk.govuk.app.topics.data.TopicsRepo
import uk.govuk.app.topics.domain.model.TopicItem
import uk.govuk.app.topics.ui.model.TopicItemUi

@OptIn(ExperimentalCoroutinesApi::class)
class TopicsWidgetViewModelTest {

    private val dispatcher = UnconfinedTestDispatcher()
    private val topicsRepo = mockk<TopicsRepo>(relaxed = true)

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Given topics are emitted, When init, then emit ui state`() {
        val topics = listOf(
            TopicItem(
                ref = "benefits",
                title = "Benefits",
                description = "description",
                isSelected = true
            )
        )

        every { topicsRepo.topics } returns flowOf(topics)

        val expected =
            TopicsWidgetUiState(
                topics = listOf(
                    TopicItemUi(
                        ref = "benefits",
                        icon = R.drawable.ic_topic_benefits,
                        title = "Benefits",
                        description = "description",
                        isSelected = true
                    )
                ),
                displayShowAll = false
            )

        val viewModel = TopicsWidgetViewModel(topicsRepo)

        runTest {
            assertEquals(expected, viewModel.uiState.first())
        }
    }

    @Test
    fun `Given topics are emitted, When init, then filter selected topics and emit ui state`() {
        val topics = listOf(
            TopicItem(
                ref = "benefits",
                title = "Benefits",
                description = "description",
                isSelected = true
            ),
            TopicItem(
                ref = "care",
                title = "Care",
                description = "description",
                isSelected = false
            )
        )

        every { topicsRepo.topics } returns flowOf(topics)

        val expected =
            TopicsWidgetUiState(
                topics = listOf(
                    TopicItemUi(
                        ref = "benefits",
                        icon = R.drawable.ic_topic_benefits,
                        title = "Benefits",
                        description = "description",
                        isSelected = true
                    )
                ),
                displayShowAll = true
            )

        val viewModel = TopicsWidgetViewModel(topicsRepo)

        runTest {
            assertEquals(expected, viewModel.uiState.first())
        }
    }
}