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
import uk.govuk.app.analytics.data.local.model.EcommerceEvent
import uk.govuk.app.topics.data.TopicsRepo
import uk.govuk.app.topics.data.remote.model.RemoteTopic.RemoteTopicContent
import uk.govuk.app.topics.navigation.TOPIC_REF_ARG
import uk.govuk.app.topics.ui.model.TopicUi.TopicContent
import uk.govuk.app.visited.Visited

@OptIn(ExperimentalCoroutinesApi::class)
class AllStepByStepsViewModelTest {

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
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Given a topic with step by steps, When init, then emit step by steps`() {
        coEvery { topicsRepo.stepBySteps } returns listOf(
            RemoteTopicContent(
                url = "url",
                title = "title",
                isStepByStep = true,
                isPopular = false
            )
        )

        val expected = listOf(
            TopicContent(
                url = "url",
                title = "title"
            )
        )

        val viewModel = AllStepByStepsViewModel(topicsRepo, analyticsClient, visited)

        runTest {
            val result = viewModel.stepBySteps.first()
            assertEquals(expected, result)
        }
    }

    @Test
    fun `Given a page view, then log analytics`() {
        val viewModel = AllStepByStepsViewModel(topicsRepo, analyticsClient, visited)

        viewModel.onPageView(title = "title")

        verify {
            analyticsClient.screenView(
                screenClass = "AllStepByStepsScreen",
                screenName = "title",
                title = "title"
            )
        }
    }

    @Test
    fun `Given a page view, then log ecommerce analytics`() {
        val viewModel = AllStepByStepsViewModel(topicsRepo, analyticsClient, visited)

        viewModel.onPageView(title = "title")

        verify {
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
    fun `Given a step by step click, then log analytics`() {
        val viewModel = AllStepByStepsViewModel(topicsRepo, analyticsClient, visited)

        viewModel.onStepByStepClick(
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
    fun `Given a step by step click, then log ecommerce analytics`() {
        val viewModel = AllStepByStepsViewModel(topicsRepo, analyticsClient, visited)

        viewModel.onStepByStepClick(
            section = "section",
            text = "text",
            url = "url"
        )

        verify {
            analyticsClient.selectItemEvent(
                ecommerceEvent = EcommerceEvent(
                    itemListName = "Topics",
                    itemListId = "text",
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
    fun `Given a step by step click, then log visited item`() {
        val viewModel = AllStepByStepsViewModel(topicsRepo, analyticsClient, visited)

        viewModel.onStepByStepClick(
            section = "section",
            text = "text",
            url = "url"
        )

        coVerify {
            visited.visitableItemClick(title = "text", url = "url")
        }
    }
}
