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
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import uk.govuk.app.analytics.Analytics
import uk.govuk.app.networking.domain.DeviceOfflineException
import uk.govuk.app.networking.domain.ServiceNotRespondingException
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
    private val analytics = mockk<Analytics>(relaxed = true)
    private val savedStateHandle = mockk<SavedStateHandle>(relaxed = true)
    private val visited = mockk<Visited>(relaxed = true)

    private val remoteTopic = RemoteTopic(
        title = "title",
        description = "description",
        subtopics = emptyList(),
        content = emptyList()
    )

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
        coEvery { topicsRepo.getTopic(REF) } returns Result.success(remoteTopic)

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

        val viewModel = TopicViewModel(topicsRepo, analytics, visited, savedStateHandle)

        runTest {
            val result = viewModel.uiState.first() as TopicUiState.Default
            assertEquals(expected, result.topicUi)
        }
    }

    @Test
    fun `Given a topic is returned, When init, then the results and status in the view model are correct`() {
        coEvery { topicsRepo.getTopic(REF) } returns Result.success(remoteTopic)

        val viewModel = TopicViewModel(topicsRepo, analytics, visited, savedStateHandle)

        runTest {
            val result = viewModel.uiState.first()
            assertTrue(result is TopicUiState.Default)
        }
    }

    @Test
    fun `Given the device is offline, When init, then the results and status in the view model are correct`() {
        coEvery { topicsRepo.getTopic(REF) } returns Result.failure(DeviceOfflineException())

        val viewModel = TopicViewModel(topicsRepo, analytics, visited, savedStateHandle)

        runTest {
            val result = viewModel.uiState.first() as TopicUiState.Offline
            assertEquals(REF, result.topicReference)
        }
    }

    @Test
    fun `Given the topic API is unavailable, When init, then the results and status in the view model are correct`() {
        coEvery { topicsRepo.getTopic(REF) } returns Result.failure(ServiceNotRespondingException())

        val viewModel = TopicViewModel(topicsRepo, analytics, visited, savedStateHandle)

        runTest {
            val result = viewModel.uiState.first() as TopicUiState.ServiceError
            assertEquals(REF, result.topicReference)
        }
    }

    @Test
    fun `Given there is a general error when getting a topic, When init, then the results and status in the view model are correct`() {
        coEvery { topicsRepo.getTopic(REF) } returns Result.failure(Exception())

        val viewModel = TopicViewModel(topicsRepo, analytics, visited, savedStateHandle)

        runTest {
            val result = viewModel.uiState.first() as TopicUiState.ServiceError
            assertEquals(REF, result.topicReference)
        }
    }

    @Test
    fun `Given a page view, then log analytics`() {
        coEvery { topicsRepo.getTopic(REF) } returns Result.success(remoteTopic)

        val viewModel = TopicViewModel(topicsRepo, analytics, visited, savedStateHandle)

        viewModel.onPageView("title")

        verify {
            analytics.screenView(
                screenClass = "TopicScreen",
                screenName = "title",
                title = "title"
            )
        }
    }

    @Test
    fun `Given a content click, then log analytics`() {
        coEvery { topicsRepo.getTopic(REF) } returns Result.success(remoteTopic)

        val viewModel = TopicViewModel(topicsRepo, analytics, visited, savedStateHandle)

        viewModel.onContentClick(
            section = "section",
            text = "text",
            url = "url"
        )

        verify {
            analytics.buttonClick(
                text = "text",
                url = "url",
                external = true,
                section = "section"
            )
        }
    }

    @Test
    fun `Given a content click, then log visited item`() {
        coEvery { topicsRepo.getTopic(REF) } returns Result.success(remoteTopic)

        val viewModel = TopicViewModel(topicsRepo, analytics, visited, savedStateHandle)

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
        coEvery { topicsRepo.getTopic(REF) } returns Result.success(remoteTopic)

        val viewModel = TopicViewModel(topicsRepo, analytics, visited, savedStateHandle)

        viewModel.onSeeAllClick(
            section = "section",
            text = "text",
        )

        verify {
            analytics.buttonClick(
                text = "text",
                external = false,
                section = "section"
            )
        }
    }

    @Test
    fun `Given a subtopic click, then log analytics`() {
        coEvery { topicsRepo.getTopic(REF) } returns Result.success(remoteTopic)

        val viewModel = TopicViewModel(topicsRepo, analytics, visited, savedStateHandle)

        viewModel.onSubtopicClick("text")

        verify {
            analytics.buttonClick(
                text = "text",
                external = false,
                section = "Sub topics"
            )
        }
    }
}
