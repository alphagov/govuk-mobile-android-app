package uk.govuk.app.topics

import io.mockk.coVerify
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
import uk.govuk.app.design.R
import uk.govuk.app.topics.data.TopicsRepo
import uk.govuk.app.topics.domain.model.TopicItem

@OptIn(ExperimentalCoroutinesApi::class)
class TopicsViewModelTest {

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
                isSelected = true
            )
        )

        every { topicsRepo.topics } returns flowOf(topics)

        val expected = listOf(
            TopicUi(
                ref = "benefits",
                icon = R.drawable.ic_topic_benefits,
                title = "Benefits",
                isSelected = true
            )
        )

        val viewModel = TopicsViewModel(topicsRepo)

        runTest {
            val result = viewModel.uiState.first()
            assertEquals(expected, result!!.topics)
        }
    }

    @Test
    fun `Given a user wants to edit their topics, When on edit, then select initial topics in repo`() {
        val viewModel = TopicsViewModel(topicsRepo)

        viewModel.onEdit()

        coVerify {
            topicsRepo.selectInitialTopics()
        }
    }

    @Test
    fun `Given a user has selected a topic, When the topic is selected, then select topic in repo`() {
        val viewModel = TopicsViewModel(topicsRepo)

        viewModel.onTopicSelectedChanged("ref", true)

        coVerify {
            topicsRepo.selectTopic("ref")
        }
    }

    @Test
    fun `Given a user has deselected a topic, When the topic is deselected, then deselect topic in repo`() {
        val viewModel = TopicsViewModel(topicsRepo)

        viewModel.onTopicSelectedChanged("ref", false)

        coVerify {
            topicsRepo.deselectTopic("ref")
        }
    }
}