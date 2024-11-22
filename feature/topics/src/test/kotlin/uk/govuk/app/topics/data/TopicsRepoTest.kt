package uk.govuk.app.topics.data

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import uk.govuk.app.networking.domain.ApiException
import uk.govuk.app.topics.data.local.TopicsLocalDataSource
import uk.govuk.app.topics.data.local.model.LocalTopicItem
import uk.govuk.app.topics.data.remote.TopicsApi
import uk.govuk.app.topics.data.remote.model.RemoteTopic
import uk.govuk.app.topics.data.remote.model.RemoteTopicItem
import uk.govuk.app.topics.domain.model.TopicItem
import java.io.IOException
import java.net.UnknownHostException

class TopicsRepoTest{

    private val topicsApi = mockk<TopicsApi>(relaxed = true)
    private val localDataSource = mockk<TopicsLocalDataSource>(relaxed = true)
    private val topicsResponse = mockk<Response<List<RemoteTopicItem>>>(relaxed = true)
    private val topicResponse = mockk<Response<RemoteTopic>>(relaxed = true)
    private val topic = mockk<RemoteTopic>(relaxed = true)

    @Test
    fun `Given the local data source is empty, when get topics, then emit selected topic items`() {
        every { localDataSource.topics } returns flowOf(emptyList())
        coEvery { topicsApi.getTopics() } returns topicsResponse
        coEvery { topicsResponse.isSuccessful } returns true
        coEvery { topicsResponse.body() } returns listOf(
            RemoteTopicItem("ref1", "title", "description"),
            RemoteTopicItem("ref2", "title", "description")
        )

        val repo = TopicsRepo(topicsApi, localDataSource)

        runTest {
            val topics = repo.topics

            val expected = listOf(
                TopicItem("ref1", "title", "description", true),
                TopicItem("ref2", "title", "description", true)
            )

            assertEquals(expected, topics.first())
        }
    }

    @Test
    fun `Given the local data source is not empty, when get topics, then emit topic items`() {
        every { localDataSource.topics } returns flowOf(
            listOf(
                LocalTopicItem().apply {
                    ref = "ref1"
                    isSelected = true
                },
                LocalTopicItem().apply {
                    ref = "ref2"
                    isSelected = false
                }
            )
        )
        coEvery { topicsApi.getTopics() } returns topicsResponse
        coEvery { topicsResponse.isSuccessful } returns true
        coEvery { topicsResponse.body() } returns listOf(
            RemoteTopicItem("ref1", "title", "description"),
            RemoteTopicItem("ref2", "title", "description"),
            RemoteTopicItem("ref3", "title", "description")
        )

        val repo = TopicsRepo(topicsApi, localDataSource)

        runTest {
            val topics = repo.topics

            val expected = listOf(
                TopicItem("ref1", "title", "description", true),
                TopicItem("ref2", "title", "description", false),
                TopicItem("ref3", "title", "description", false)
            )

            assertEquals(expected, topics.first())
        }
    }

    @Test
    fun `Given the local data source is empty, when select initial topics, then select all in the local data source`() {
        every { localDataSource.topics } returns flowOf(emptyList())
        coEvery { topicsApi.getTopics() } returns topicsResponse
        coEvery { topicsResponse.isSuccessful } returns true
        coEvery { topicsResponse.body() } returns listOf(
            RemoteTopicItem("ref1", "title", "description"),
            RemoteTopicItem("ref2", "title", "description")
        )

        val repo = TopicsRepo(topicsApi, localDataSource)

        runTest {
            repo.selectInitialTopics()

            coVerify {
                localDataSource.selectAll(listOf("ref1", "ref2"))
            }
        }
    }

    @Test
    fun `Given the local data source is not empty, when select initial topics, then don't call local data source`() {
        coEvery { localDataSource.topics } returns flowOf(listOf(LocalTopicItem()))

        val repo = TopicsRepo(topicsApi, localDataSource)

        runTest {
            repo.selectInitialTopics()

            coVerify(exactly = 0) {
                localDataSource.selectAll(any())
            }
        }
    }

    @Test
    fun `Given a topic is selected, then select in local data source`() {
        val repo = TopicsRepo(topicsApi, localDataSource)

        runTest {
            repo.selectTopic("ref")

            coVerify {
                localDataSource.select("ref")
            }
        }
    }

    @Test
    fun `Given a topic is deselected, then deselect in local data source`() {
        val repo = TopicsRepo(topicsApi, localDataSource)

        runTest {
            repo.deselectTopic("ref")

            coVerify {
                localDataSource.deselect("ref")
            }
        }
    }

    @Test
    fun `Given a successful topic response with a body, then return success with topic`() {
        coEvery { topicsApi.getTopic("ref") } returns topicResponse
        coEvery { topicResponse.isSuccessful } returns true
        coEvery { topicResponse.body() } returns topic

        val repo = TopicsRepo(topicsApi, localDataSource)

        val expected = Result.success(topic)

        runTest {
            assertEquals(expected, repo.getTopic("ref"))
        }
    }

    @Test
    fun `Given a successful topic response with an empty body, then return failure`() {
        coEvery { topicsApi.getTopic("ref") } returns topicResponse
        coEvery { topicResponse.isSuccessful } returns true
        coEvery { topicResponse.body() } returns null

        val repo = TopicsRepo(topicsApi, localDataSource)

        runTest {
            val result = repo.getTopic("ref")
            assertTrue(result.isFailure)
        }
    }

    @Test
    fun `Given an unsuccessful config response, then return failure`() {
        coEvery { topicsApi.getTopic("ref") } returns topicResponse
        coEvery { topicResponse.isSuccessful } returns false

        val repo = TopicsRepo(topicsApi, localDataSource)

        runTest {
            val result = repo.getTopic("ref")
            assertTrue(result.isFailure)
        }
    }

    @Test
    fun `Given an exception is thrown fetching the topics response, then return failure`() {
        coEvery { topicsApi.getTopic("ref") } throws IOException()

        val repo = TopicsRepo(topicsApi, localDataSource)

        runTest {
            val result = repo.getTopic("ref")
            assertTrue(result.isFailure)
        }
    }

    @Test
    fun `initTopic returns failure when the device is offline`() {
        coEvery { topicsApi.getTopic("ref") } throws UnknownHostException()

        val repo = TopicsRepo(topicsApi, localDataSource)

        runTest {
            val result = repo.getTopic("ref")
            assertTrue(result.isFailure)
        }
    }

    @Test
    fun `initTopic returns failure when the Topic API is offline`() {
        val httpException = mockk<HttpException>(relaxed = true)
        coEvery { topicsApi.getTopic("ref") } throws httpException

        val repo = TopicsRepo(topicsApi, localDataSource)

        runTest {
            val result = repo.getTopic("ref")
            assertTrue(result.isFailure)
        }
    }

    @Test
    fun `initTopic returns failure when an API error occurs`() {
        coEvery { topicsApi.getTopic("ref") } throws ApiException()

        val repo = TopicsRepo(topicsApi, localDataSource)

        runTest {
            val result = repo.getTopic("ref")
            assertTrue(result.isFailure)
        }
    }

    @Test
    fun `initTopic returns failure when any unknown error occurs`() {
        coEvery { topicsApi.getTopic("ref") } throws Exception()

        val repo = TopicsRepo(topicsApi, localDataSource)

        runTest {
            val result = repo.getTopic("ref")
            assertTrue(result.isFailure)
        }
    }

    @Test
    fun `Given topics are customised, when is topics customised, then return true`() {
        val repo = TopicsRepo(topicsApi, localDataSource)

        runTest {
            coEvery { localDataSource.isTopicsCustomised() } returns true

            assertTrue(repo.isTopicsCustomised())
        }
    }

    @Test
    fun `Given topics are not customised, when is topics customised, then return false`() {
        val repo = TopicsRepo(topicsApi, localDataSource)

        runTest {
            coEvery { localDataSource.isTopicsCustomised() } returns false

            assertFalse(repo.isTopicsCustomised())
        }
    }

    @Test
    fun `Given a user customises topics, topics customised, then update local data source`() {
        val repo = TopicsRepo(topicsApi, localDataSource)

        runTest {
            repo.topicsCustomised()

            coVerify {
                localDataSource.topicsCustomised()
            }
        }
    }
}