package uk.govuk.app.topics

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
import uk.govuk.app.design.R
import uk.govuk.app.topics.data.TopicsRepo
import uk.govuk.app.topics.domain.model.TopicItem
import uk.govuk.app.topics.ui.model.TopicItemUi

@OptIn(ExperimentalCoroutinesApi::class)
class AllTopicsViewModelTest {

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
    fun `Given topics are emitted, When init, then select initial topics in repo and emit topics`() {
        val topics = listOf(
            TopicItem(
                ref = "benefits",
                title = "Benefits",
                isSelected = true
            )
        )

        every { topicsRepo.topics } returns flowOf(topics)

        val expected = listOf(
            TopicItemUi(
                ref = "benefits",
                icon = R.drawable.ic_topic_benefits,
                title = "Benefits",
                isSelected = true
            )
        )

        val viewModel = AllTopicsViewModel(topicsRepo, analytics)

        runTest {
            val result = viewModel.topics.first()
            assertEquals(expected, result)

            coVerify { topicsRepo.selectInitialTopics() }
        }
    }

    @Test
    fun `Given a page view, then log analytics`() {
        val viewModel = AllTopicsViewModel(topicsRepo, analytics)

        viewModel.onPageView("title")

        verify {
            analytics.screenView(
                screenClass = "AllTopicsScreen",
                screenName = "All Topics",
                title = "title"
            )
        }
    }

    @Test
    fun `Given a click, then log analytics`() {
        val viewModel = AllTopicsViewModel(topicsRepo, analytics)

        viewModel.onClick("title")

        verify {
            analytics.buttonClick("title")
        }
    }

}