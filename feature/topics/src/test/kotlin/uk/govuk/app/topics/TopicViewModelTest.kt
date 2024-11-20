package uk.govuk.app.topics

import androidx.lifecycle.SavedStateHandle
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import uk.govuk.app.analytics.AnalyticsClient
import uk.govuk.app.topics.data.TopicsRepo
import uk.govuk.app.topics.data.remote.model.RemoteTopic
import uk.govuk.app.topics.navigation.TOPIC_REF_ARG
import uk.govuk.app.topics.navigation.TOPIC_SUBTOPIC_ARG
import uk.govuk.app.topics.ui.model.TopicUi
import uk.govuk.app.visited.Visited

@OptIn(ExperimentalCoroutinesApi::class)
class TopicViewModelTest {

    companion object {
        private const val REF = "ref"
    }

    private val dispatcher = UnconfinedTestDispatcher()
    private val topicsRepo = mockk<TopicsRepo>(relaxed = true)
    private val analyticsClient = mockk<AnalyticsClient>(relaxed = true)
    private val savedStateHandle = mockk<SavedStateHandle>(relaxed = true)
    private val visited = mockk<Visited>(relaxed = true)

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        every { savedStateHandle.get<String>(TOPIC_REF_ARG) } returns REF
        every { savedStateHandle.get<Boolean>(TOPIC_SUBTOPIC_ARG) } returns false
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Given a topic is returned, When init, then emit topic`() {
        val topic = RemoteTopic(
            title = "title",
            description = "description",
            subtopics = emptyList(),
            content = emptyList()
        )

        coEvery { topicsRepo.getTopic(REF) } returns topic

        val expected = TopicUi(
            title = "title",
            description = "description",
            popularPages = emptyList(),
            stepBySteps = emptyList(),
            displayStepByStepSeeAll = false,
            services = emptyList(),
            subtopics = emptyList(),
            subtopicsTitle = R.string.browseTitle
        )

        val viewModel = TopicViewModel(topicsRepo, analyticsClient, visited, savedStateHandle)

        runTest {
            val result = viewModel.topic.first()
            assertEquals(expected, result)
        }
    }

    @Test
    fun `Given a page view, then log analytics`() {
        val viewModel = TopicViewModel(topicsRepo, analyticsClient, visited, savedStateHandle)

        viewModel.onPageView("title")

        verify {
            analyticsClient.screenView(
                screenClass = "TopicScreen",
                screenName = "title",
                title = "title"
            )
        }
    }

    @Test
    fun `Given a content click, then log analytics`() {
        val viewModel = TopicViewModel(topicsRepo, analyticsClient, visited, savedStateHandle)

        viewModel.onContentClick(
            section = "section",
            text = "text",
            url = "url"
        )

        verify {
            analyticsClient.buttonClick(
                text = "text",
                url = "url",
                external = true,
                section = "section"
            )
        }
    }

    @Test
    fun `Given a content click, then log visited item`() {
        val viewModel = TopicViewModel(topicsRepo, analyticsClient, visited, savedStateHandle)

        viewModel.onContentClick(
            section = "section",
            text = "text",
            url = "url"
        )

        coVerify {
            visited.visitableItemClick(title = "text", url = "url")
        }
    }

    @Test
    fun `Given a see all click, then log analytics`() {
        val viewModel = TopicViewModel(topicsRepo, analyticsClient, visited, savedStateHandle)

        viewModel.onSeeAllClick(
            section = "section",
            text = "text",
        )

        verify {
            analyticsClient.buttonClick(
                text = "text",
                external = false,
                section = "section"
            )
        }
    }

    @Test
    fun `Given a subtopic click, then log analytics`() {
        val viewModel = TopicViewModel(topicsRepo, analyticsClient, visited, savedStateHandle)

        viewModel.onSubtopicClick("text")

        verify {
            analyticsClient.buttonClick(
                text = "text",
                external = false,
                section = "Sub topics"
            )
        }
    }
}
