package uk.govuk.app.topics

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import uk.govuk.app.design.R
import uk.govuk.app.topics.data.remote.model.TopicItem

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
    fun `Given topics are not null, When init, then emit topics`() {
        coEvery { topicsRepo.getTopics() } returns listOf(TopicItem("benefits", "Benefits"))

        val expected = listOf(
            TopicUi(
                ref = "benefits",
                icon = R.drawable.ic_topic_benefits,
                title = "Benefits"
            )
        )

        val viewModel = TopicsViewModel(topicsRepo)

        runTest {
            val result = viewModel.uiState.first()
            assertEquals(expected, result!!.topics)
        }
    }

    @Test
    fun `Given topics are not null, When init, then emit null`() {
        coEvery { topicsRepo.getTopics() } returns null

        val viewModel = TopicsViewModel(topicsRepo)

        runTest {
            val result = viewModel.uiState.first()
            assertNull(result)
        }
    }
}