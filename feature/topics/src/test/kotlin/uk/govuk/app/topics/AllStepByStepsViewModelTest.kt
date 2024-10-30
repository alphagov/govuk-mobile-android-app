package uk.govuk.app.topics

import androidx.lifecycle.SavedStateHandle
import io.mockk.coEvery
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
import uk.govuk.app.analytics.Analytics
import uk.govuk.app.topics.data.TopicsRepo
import uk.govuk.app.topics.data.remote.model.RemoteTopic
import uk.govuk.app.topics.data.remote.model.RemoteTopic.RemoteTopicContent
import uk.govuk.app.topics.navigation.TOPIC_REF_ARG
import uk.govuk.app.topics.ui.model.TopicUi.TopicContent

@OptIn(ExperimentalCoroutinesApi::class)
class AllStepByStepsViewModelTest {

    companion object {
        private const val REF = "ref"
    }

    private val dispatcher = UnconfinedTestDispatcher()
    private val topicsRepo = mockk<TopicsRepo>(relaxed = true)
    private val analytics = mockk<Analytics>(relaxed = true)
    private val savedStateHandle = mockk<SavedStateHandle>(relaxed = true)

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
    fun `Given a topic is returned, When init, then emit step by steps`() {
        val topic = RemoteTopic(
            title = "title",
            description = "description",
            content = listOf(
                RemoteTopicContent(
                    url = "url",
                    title = "title",
                    isStepByStep = true,
                    isPopular = false
                )
            ),
            subtopics = emptyList()
        )

        coEvery { topicsRepo.getTopic(REF) } returns topic

        val expected = listOf(
            TopicContent(
                url = "url",
                title = "title"
            )
        )

        val viewModel = AllStepByStepsViewModel(topicsRepo, analytics, savedStateHandle)

        runTest {
            val result = viewModel.stepBySteps.first()
            assertEquals(expected, result)
        }
    }

    @Test
    fun `Given a page view, then log analytics`() {
        val viewModel = AllStepByStepsViewModel(topicsRepo, analytics, savedStateHandle)

        viewModel.onPageView("title")

        verify {
            analytics.screenView(
                screenClass = "AllStepByStepsScreen",
                screenName = "title",
                title = "title"
            )
        }
    }

    @Test
    fun `Given a step by step click, then log analytics`() {
        val viewModel = AllStepByStepsViewModel(topicsRepo, analytics, savedStateHandle)

        viewModel.onStepByStepClick(
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
}