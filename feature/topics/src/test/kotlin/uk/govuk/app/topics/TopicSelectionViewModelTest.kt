package uk.govuk.app.topics

import io.mockk.clearMocks
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
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import uk.govuk.app.analytics.Analytics
import uk.govuk.app.topics.data.TopicsRepo
import uk.govuk.app.topics.domain.model.TopicItem
import uk.govuk.app.topics.ui.TopicSelectionViewModel
import uk.govuk.app.topics.ui.model.TopicItemUi

@OptIn(ExperimentalCoroutinesApi::class)
class TopicSelectionViewModelTest {

    private val dispatcher = UnconfinedTestDispatcher()
    private val topicsRepo = mockk<TopicsRepo>(relaxed = true)
    private val analytics = mockk<Analytics>(relaxed = true)

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Given topics, When init, then emit topics as unselected and done as disabled`() {
        val topics = listOf(
            TopicItem(
                ref = "benefits",
                title = "Benefits",
                description = "description",
                isSelected = true
            ),
            TopicItem(
                ref = "travel-abroad",
                title = "Travel",
                description = "description",
                isSelected = false
            )
        )

        every { topicsRepo.topics } returns flowOf(topics)

        val expected = listOf(
            TopicItemUi(
                ref = "benefits",
                icon = R.drawable.ic_topic_benefits,
                title = "Benefits",
                description = "description",
                isSelected = false
            ),
            TopicItemUi(
                ref = "travel-abroad",
                icon = R.drawable.ic_topic_travel,
                title = "Travel",
                description = "description",
                isSelected = false
            ),
        )

        val viewModel = TopicSelectionViewModel(topicsRepo, analytics)

        runTest {
            val result = viewModel.uiState.first()!!
            assertEquals(expected, result.topics)
            assertFalse(result.isDoneEnabled)
        }
    }

    @Test
    fun `Given a user has selected a topic, When the topic is selected, then emit ui state, send analytics and enable done`() {
        val topics = listOf(
            TopicItem(
                ref = "benefits",
                title = "Benefits",
                description = "description",
                isSelected = false
            ),
            TopicItem(
                ref = "travel-abroad",
                title = "Travel",
                description = "description",
                isSelected = false
            )
        )

        every { topicsRepo.topics } returns flowOf(topics)

        val viewModel = TopicSelectionViewModel(topicsRepo, analytics)

        viewModel.onClick("benefits", "Benefits")

        runTest {
            val result = viewModel.uiState.value!!
            assertEquals("Benefits", result.topics[0].title)
            assertTrue(result.topics[0].isSelected)
            assertEquals("Travel", result.topics[1].title)
            assertFalse(result.topics[1].isSelected)
            assertTrue(result.isDoneEnabled)

            verify{
                analytics.buttonFunction(
                    text = "Benefits",
                    section = "Topics",
                    action = "Add"
                )
            }
        }
    }

    @Test
    fun `Given a user has deselected a topic, When the topic is deselected, then emit ui state, send analytics and disable done`() {
        val topics = listOf(
            TopicItem(
                ref = "benefits",
                title = "Benefits",
                description = "description",
                isSelected = false
            ),
            TopicItem(
                ref = "travel-abroad",
                title = "Travel",
                description = "description",
                isSelected = false
            )
        )

        every { topicsRepo.topics } returns flowOf(topics)

        val viewModel = TopicSelectionViewModel(topicsRepo, analytics)

        viewModel.onClick("benefits", "Benefits")
        clearMocks(analytics)
        viewModel.onClick("benefits", "Benefits")

        runTest {
            val result = viewModel.uiState.value!!
            assertEquals("Benefits", result.topics[0].title)
            assertFalse(result.topics[0].isSelected)
            assertEquals("Travel", result.topics[1].title)
            assertFalse(result.topics[1].isSelected)
            assertFalse(result.isDoneEnabled)

            verify{
                analytics.buttonFunction(
                    text = "Benefits",
                    section = "Topics",
                    action = "Remove"
                )
            }
        }
    }

    @Test
    fun `Given a page view, then log analytics`() {
        val viewModel = TopicSelectionViewModel(topicsRepo, analytics)

        viewModel.onPageView("title")

        verify {
            analytics.screenView(
                screenClass = "TopicSelectionScreen",
                screenName = "Topic Selection",
                title = "title"
            )
        }
    }

    @Test
    fun `Given a user is done, then log analytics and update topics repo`() {
        val viewModel = TopicSelectionViewModel(topicsRepo, analytics)

        viewModel.onClick("ref1", "title")
        viewModel.onClick("ref2", "title")
        viewModel.onDone("Done")

        coVerify {
            analytics.topicsCustomised()
            analytics.buttonClick(
                text = "Done"
            )
            topicsRepo.selectTopic("ref1")
            topicsRepo.selectTopic("ref2")
        }
    }

    @Test
    fun `Given a user has skipped, then log analytics`() {
        val viewModel = TopicSelectionViewModel(topicsRepo, analytics)

        viewModel.onSkip("Skip")

        verify {
            analytics.buttonClick(
                text = "Skip"
            )
        }
    }
}