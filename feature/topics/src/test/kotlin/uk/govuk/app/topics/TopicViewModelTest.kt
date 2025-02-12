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
import uk.govuk.app.analytics.AnalyticsClient
import uk.govuk.app.analytics.data.local.model.EcommerceEvent
import uk.govuk.app.data.model.Result.*
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
        coEvery { topicsRepo.getTopic(REF) } returns Success(remoteTopic)

        val expected = TopicUi(
            title = "title",
            description = "description",
            popularPages = emptyList(),
            stepBySteps = emptyList(),
            displayStepByStepSeeAll = false,
            services = emptyList(),
            subtopics = emptyList(),
            subtopicsSection = TopicUi.Section(
                title = R.string.browseTitle,
                icon = R.drawable.ic_topic_browse
            )
        )

        val viewModel = TopicViewModel(topicsRepo, analyticsClient, visited, savedStateHandle)

        runTest {
            val result = viewModel.uiState.first() as TopicUiState.Default
            assertEquals(expected, result.topicUi)
        }
    }

    @Test
    fun `Given a topic is returned, When init, then the results and status in the view model are correct`() {
        coEvery { topicsRepo.getTopic(REF) } returns Success(remoteTopic)

        val viewModel = TopicViewModel(topicsRepo, analyticsClient, visited, savedStateHandle)

        runTest {
            val result = viewModel.uiState.first()
            assertTrue(result is TopicUiState.Default)
        }
    }

    @Test
    fun `Given the device is offline, When init, then the results and status in the view model are correct`() {
        coEvery { topicsRepo.getTopic(REF) } returns DeviceOffline()

        val viewModel = TopicViewModel(topicsRepo, analyticsClient, visited, savedStateHandle)

        runTest {
            val result = viewModel.uiState.first() as TopicUiState.Offline
            assertEquals(REF, result.topicReference)
        }
    }

    @Test
    fun `Given the topic API is unavailable, When init, then the results and status in the view model are correct`() {
        coEvery { topicsRepo.getTopic(REF) } returns ServiceNotResponding()

        val viewModel = TopicViewModel(topicsRepo, analyticsClient, visited, savedStateHandle)

        runTest {
            val result = viewModel.uiState.first() as TopicUiState.ServiceError
            assertEquals(REF, result.topicReference)
        }
    }

    @Test
    fun `Given there is a general error when getting a topic, When init, then the results and status in the view model are correct`() {
        coEvery { topicsRepo.getTopic(REF) } returns Error()

        val viewModel = TopicViewModel(topicsRepo, analyticsClient, visited, savedStateHandle)

        runTest {
            val result = viewModel.uiState.first() as TopicUiState.ServiceError
            assertEquals(REF, result.topicReference)
        }
    }

    @Test
    fun `Given a page view, then log analytics`() {
        coEvery { topicsRepo.getTopic(REF) } returns Success(remoteTopic)

        val viewModel = TopicViewModel(topicsRepo, analyticsClient, visited, savedStateHandle)

        viewModel.onPageView(
            topicUi = null,
            title = "title"
        )

        verify {
            analyticsClient.screenView(
                screenClass = "TopicScreen",
                screenName = "title",
                title = "title"
            )
        }
    }

    @Test
    fun `Given an error page view, then don't log ecommerce analytics`() {
        coEvery { topicsRepo.getTopic(REF) } returns Success(remoteTopic)

        val viewModel = TopicViewModel(topicsRepo, analyticsClient, visited, savedStateHandle)

        viewModel.onPageView(
            topicUi = null,
            title = "title"
        )

        verify(exactly = 0) {
            analyticsClient.viewItemListEvent(
                ecommerceEvent = EcommerceEvent(
                    itemListName = "Topics",
                    itemListId = "title",
                    items = emptyList()
                )
            )
        }
    }

    @Test
    fun `Given a page view, then log ecommerce analytics`() {
        coEvery { topicsRepo.getTopic(REF) } returns Success(remoteTopic)

        val viewModel = TopicViewModel(topicsRepo, analyticsClient, visited, savedStateHandle)
        val popularPagesSection = listOf(
            TopicUi.TopicContent(
                title = "title",
                url = "url"
            )
        )
        val topicUi = TopicUi(
            title = "title",
            description = "description",
            popularPages = popularPagesSection,
            stepBySteps = emptyList(),
            displayStepByStepSeeAll = false,
            services = emptyList(),
            subtopics = emptyList(),
            subtopicsSection = TopicUi.Section(
                title = R.string.browseTitle,
                icon = R.drawable.ic_topic_browse
            )
        )

        viewModel.onPageView(
            topicUi = topicUi,
            title = "title"
        )

        verify {
            analyticsClient.viewItemListEvent(
                ecommerceEvent = EcommerceEvent(
                    itemListName = "Topics",
                    itemListId = "title",
                    items = popularPagesSection.map {
                        EcommerceEvent.Item(
                            itemName = it.title,
                            itemCategory = "Popular pages in this topic",
                            locationId = it.url
                        )
                    }
                )
            )
        }
    }

    @Test
    fun `Given a content click, then log analytics`() {
        coEvery { topicsRepo.getTopic(REF) } returns Success(remoteTopic)

        val viewModel = TopicViewModel(topicsRepo, analyticsClient, visited, savedStateHandle)

        viewModel.onContentClick(
            section = "section",
            text = "text",
            url = "url",
            title = "title"
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
    fun `Given a content click, then log ecommerce analytics`() {
        coEvery { topicsRepo.getTopic(REF) } returns Success(remoteTopic)

        val viewModel = TopicViewModel(topicsRepo, analyticsClient, visited, savedStateHandle)

        viewModel.onContentClick(
            section = "section",
            text = "text",
            url = "url",
            title = "title"
        )

        verify {
            analyticsClient.selectItemEvent(
                ecommerceEvent = EcommerceEvent(
                    itemListName = "Topics",
                    itemListId = "title",
                    items = listOf(
                        EcommerceEvent.Item(
                            itemName = "text",
                            itemCategory = "section",
                            locationId = "url"
                        )
                    )
                )
            )
        }
    }

    @Test
    fun `Given a content click, then log visited item`() {
        coEvery { topicsRepo.getTopic(REF) } returns Success(remoteTopic)

        val viewModel = TopicViewModel(topicsRepo, analyticsClient, visited, savedStateHandle)

        viewModel.onContentClick(
            section = "section",
            text = "text",
            url = "url",
            title = "title"
        )

        coVerify {
            visited.visitableItemClick(title = "text", url = "url")
        }
    }

    @Test
    fun `Given a see all click, then log analytics`() {
        coEvery { topicsRepo.getTopic(REF) } returns Success(remoteTopic)

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
        coEvery { topicsRepo.getTopic(REF) } returns Success(remoteTopic)

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

    @Test
    fun `Given a subtopic click, then log ecommerce analytics`() {
        coEvery { topicsRepo.getTopic(REF) } returns Success(remoteTopic)

        val viewModel = TopicViewModel(topicsRepo, analyticsClient, visited, savedStateHandle)

        viewModel.onSubtopicClick("text")

        verify {
            analyticsClient.selectItemEvent(
                ecommerceEvent = EcommerceEvent(
                    itemListName = "Topics",
                    itemListId = "",
                    items = listOf(
                        EcommerceEvent.Item(
                            itemName = "text",
                            itemCategory = "Sub topics",
                            locationId = ""
                        )
                    )
                )
            )
        }
    }
}
