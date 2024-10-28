package uk.govuk.app.topics.extension

import org.junit.Assert.assertEquals
import org.junit.Test
import uk.govuk.app.topics.data.remote.model.RemoteTopic
import uk.govuk.app.topics.data.remote.model.RemoteTopicItem
import uk.govuk.app.topics.ui.model.TopicUi

class RemoteTopicTest {

    @Test
    fun `Given a remote topic, When mapping to topic ui, then return a topic ui`() {
        val remoteTopic = RemoteTopic(
            title = "title",
            description = "description",
            subtopics = listOf(
                RemoteTopicItem(
                    ref = "ref",
                    title = "title"
                )
            ),
            content = listOf(
                RemoteTopic.RemoteTopicContent(
                    url = "url-1",
                    title = "title-1",
                    isStepByStep = true,
                    isPopular = false
                ),
                RemoteTopic.RemoteTopicContent(
                    url = "url-2",
                    title = "title-2",
                    isStepByStep = false,
                    isPopular = true
                ),
                RemoteTopic.RemoteTopicContent(
                    url = "url-3",
                    title = "title-3",
                    isStepByStep = false,
                    isPopular = false
                )
            )
        )

        val expected = TopicUi(
            title = "title",
            description = "description",
            subtopics = listOf(
                TopicUi.Subtopic(
                    ref = "ref",
                    title = "title"
                )
            ),
            popularPages = listOf(
                TopicUi.TopicContent(
                    url = "url-2",
                    title = "title-2"
                )
            ),
            stepBySteps = listOf(
                TopicUi.TopicContent(
                    url = "url-1",
                    title = "title-1"
                )
            ),
            displayStepByStepSeeAll = false
        )

        assertEquals(expected, remoteTopic.toTopicUi(1))
    }

    @Test
    fun `Given a remote topic with step by steps that exceed the max number, When mapping to topic ui, then return a topic ui`() {
        val remoteTopic = RemoteTopic(
            title = "title",
            description = "description",
            subtopics = listOf(
                RemoteTopicItem(
                    ref = "ref",
                    title = "title"
                )
            ),
            content = listOf(
                RemoteTopic.RemoteTopicContent(
                    url = "url-1",
                    title = "title-1",
                    isStepByStep = true,
                    isPopular = false
                ),
                RemoteTopic.RemoteTopicContent(
                    url = "url-2",
                    title = "title-2",
                    isStepByStep = false,
                    isPopular = true
                ),
                RemoteTopic.RemoteTopicContent(
                    url = "url-3",
                    title = "title-3",
                    isStepByStep = true,
                    isPopular = false
                )
            )
        )

        val expected = TopicUi(
            title = "title",
            description = "description",
            subtopics = listOf(
                TopicUi.Subtopic(
                    ref = "ref",
                    title = "title"
                )
            ),
            popularPages = listOf(
                TopicUi.TopicContent(
                    url = "url-2",
                    title = "title-2"
                )
            ),
            stepBySteps = listOf(
                TopicUi.TopicContent(
                    url = "url-1",
                    title = "title-1"
                )
            ),
            displayStepByStepSeeAll = true
        )

        assertEquals(expected, remoteTopic.toTopicUi(1))
    }
}