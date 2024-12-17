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
    fun `Given the topics api response is unsuccessful, when sync, then return false`() {
        coEvery { topicsApi.getTopics() } returns topicsResponse
        every { topicsResponse.isSuccessful } returns false

        val repo = TopicsRepo(topicsApi, localDataSource)

        runTest {
            assertFalse(repo.sync())
        }
    }

    @Test
    fun `Given the topics api response body is empty, when sync, then return false`() {
        coEvery { topicsApi.getTopics() } returns topicsResponse
        every { topicsResponse.isSuccessful } returns true
        every { topicsResponse.body() } returns null

        val repo = TopicsRepo(topicsApi, localDataSource)

        runTest {
            assertFalse(repo.sync())
        }
    }

    @Test
    fun `Given the topics api throws an exception, when sync, then return false`() {
        coEvery { topicsApi.getTopics() } throws IOException()

        val repo = TopicsRepo(topicsApi, localDataSource)

        runTest {
            assertFalse(repo.sync())
        }
    }

    @Test
    fun `Given the topics api is successful, when sync, then sync local data source and return true`() {
        coEvery { topicsApi.getTopics() } returns topicsResponse
        every { topicsResponse.isSuccessful } returns true
        every { topicsResponse.body() } returns emptyList()

        val repo = TopicsRepo(topicsApi, localDataSource)

        runTest {
            assertTrue(repo.sync())
        }
    }

    @Test
    fun `Given locally cached topics, when get topics, then emit topic items`() {
        every { localDataSource.topics } returns flowOf(
            listOf(
                LocalTopicItem().apply {
                    ref = "ref1"
                    title = "title1"
                    description = "desc1"
                    isSelected = true
                },
                LocalTopicItem().apply {
                    ref = "ref2"
                    title = "title2"
                    description = "desc2"
                    isSelected = false
                }
            )
        )

        val repo = TopicsRepo(topicsApi, localDataSource)

        runTest {
            val topics = repo.topics

            val expected = listOf(
                TopicItem("ref1", "title1", "desc1", true),
                TopicItem("ref2", "title2", "desc2", false),
            )

            assertEquals(expected, topics.first())
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
    fun `Given the user it toggling the selection of a topic, when toggle selection, then update local data source`() {
        val repo = TopicsRepo(topicsApi, localDataSource)

        runTest {
            repo.toggleSelection("ref", true)

            coVerify{
                localDataSource.toggleSelection("ref", true)
            }
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
    fun `Given an unsuccessful topic response, then return failure`() {
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
    fun `Get topic returns failure when the device is offline`() {
        coEvery { topicsApi.getTopic("ref") } throws UnknownHostException()

        val repo = TopicsRepo(topicsApi, localDataSource)

        runTest {
            val result = repo.getTopic("ref")
            assertTrue(result.isFailure)
        }
    }

    @Test
    fun `Get topic returns failure when the Topic API is offline`() {
        val httpException = mockk<HttpException>(relaxed = true)
        coEvery { topicsApi.getTopic("ref") } throws httpException

        val repo = TopicsRepo(topicsApi, localDataSource)

        runTest {
            val result = repo.getTopic("ref")
            assertTrue(result.isFailure)
        }
    }

    @Test
    fun `Get topic returns failure when an API error occurs`() {
        coEvery { topicsApi.getTopic("ref") } throws ApiException()

        val repo = TopicsRepo(topicsApi, localDataSource)

        runTest {
            val result = repo.getTopic("ref")
            assertTrue(result.isFailure)
        }
    }

    @Test
    fun `Get topic returns failure when any unknown error occurs`() {
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
    fun `Given a user customises topics, when topics customised, then update local data source`() {
        val repo = TopicsRepo(topicsApi, localDataSource)

        runTest {
            repo.topicsCustomised()

            coVerify {
                localDataSource.topicsCustomised()
            }
        }
    }
}