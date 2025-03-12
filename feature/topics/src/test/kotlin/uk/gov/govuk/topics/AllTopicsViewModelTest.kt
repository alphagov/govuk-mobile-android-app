package uk.gov.govuk.topics

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
import uk.gov.govuk.analytics.AnalyticsClient
import uk.gov.govuk.topics.data.TopicsRepo
import uk.gov.govuk.topics.domain.model.TopicItem
import uk.gov.govuk.topics.ui.model.TopicItemUi

@OptIn(ExperimentalCoroutinesApi::class)
class AllTopicsViewModelTest {

    private val dispatcher = UnconfinedTestDispatcher()
    private val topicsRepo = mockk<TopicsRepo>(relaxed = true)
    private val analyticsClient = mockk<AnalyticsClient>(relaxed = true)

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Given topics are emitted, When init, then emit topics`() {
        val topics = listOf(
            TopicItem(
                ref = "benefits",
                title = "Benefits",
                description = "description",
                isSelected = true
            )
        )

        every { topicsRepo.topics } returns flowOf(topics)

        val expected = listOf(
            TopicItemUi(
                ref = "benefits",
                icon = R.drawable.ic_topic_benefits,
                title = "Benefits",
                description = "description",
                isSelected = true
            )
        )

        val viewModel = AllTopicsViewModel(topicsRepo, analyticsClient)

        runTest {
            val result = viewModel.topics.first()
            assertEquals(expected, result)
        }
    }

    @Test
    fun `Given a page view, then log analytics`() {
        val viewModel = AllTopicsViewModel(topicsRepo, analyticsClient)

        viewModel.onPageView("title")

        verify {
            analyticsClient.screenView(
                screenClass = "AllTopicsScreen",
                screenName = "All Topics",
                title = "title"
            )
        }
    }

    @Test
    fun `Given a click, then log analytics`() {
        val viewModel = AllTopicsViewModel(topicsRepo, analyticsClient)

        viewModel.onClick("title")

        verify {
            analyticsClient.buttonClick("title")
        }
    }

}