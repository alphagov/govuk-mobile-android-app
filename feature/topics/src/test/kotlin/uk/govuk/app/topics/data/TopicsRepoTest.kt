package uk.govuk.app.topics.data

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import retrofit2.Response
import uk.govuk.app.topics.data.local.TopicsLocalDataSource
import uk.govuk.app.topics.data.local.model.LocalTopicItem
import uk.govuk.app.topics.data.remote.TopicsApi
import uk.govuk.app.topics.data.remote.model.RemoteTopicItem
import uk.govuk.app.topics.domain.model.TopicItem

class TopicsRepoTest{

    private val topicsApi = mockk<TopicsApi>(relaxed = true)
    private val localDataSource = mockk<TopicsLocalDataSource>(relaxed = true)
    private val response = mockk<Response<List<RemoteTopicItem>>>(relaxed = true)

    @Test
    fun `Given the local data source is empty, when get topics, then emit selected topic items`() {
        every { localDataSource.topics } returns flowOf(emptyList())
        coEvery { topicsApi.getTopics() } returns response
        coEvery { response.isSuccessful } returns true
        coEvery { response.body() } returns listOf(
            RemoteTopicItem("ref1", "title"),
            RemoteTopicItem("ref2", "title")
        )

        val repo = TopicsRepo(topicsApi, localDataSource)

        runTest {
            val topics = repo.topics

            val expected = listOf(
                TopicItem("ref1", "title", true),
                TopicItem("ref2", "title", true)
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
        coEvery { topicsApi.getTopics() } returns response
        coEvery { response.isSuccessful } returns true
        coEvery { response.body() } returns listOf(
            RemoteTopicItem("ref1", "title"),
            RemoteTopicItem("ref2", "title"),
            RemoteTopicItem("ref3", "title")
        )

        val repo = TopicsRepo(topicsApi, localDataSource)

        runTest {
            val topics = repo.topics

            val expected = listOf(
                TopicItem("ref1", "title", true),
                TopicItem("ref2", "title", false),
                TopicItem("ref3", "title", false)
            )

            assertEquals(expected, topics.first())
        }
    }

    @Test
    fun `Given the local data source is empty, when select initial topics, then select all in the local data source`() {
        every { localDataSource.topics } returns flowOf(emptyList())
        coEvery { topicsApi.getTopics() } returns response
        coEvery { response.isSuccessful } returns true
        coEvery { response.body() } returns listOf(
            RemoteTopicItem("ref1", "title"),
            RemoteTopicItem("ref2", "title")
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

    /*
    @Test
    fun `Given a successful topics response with a body, then return topics`() {
        coEvery { topicsApi.getTopics() } returns response
        coEvery { response.isSuccessful } returns true
        coEvery { response.body() } returns topics

        val repo = TopicsRepo(topicsApi, localDataSource)

        runTest {
            assertEquals(repo.getTopics(), topics)
        }
    }

    @Test
    fun `Given a successful topics response with an empty body, then return null`() {
        coEvery { topicsApi.getTopics() } returns response
        coEvery { response.isSuccessful } returns true
        coEvery { response.body() } returns null

        val repo = TopicsRepo(topicsApi, localDataSource)

        runTest {
            assertNull(repo.getTopics())
        }
    }

    @Test
    fun `Given an unsuccessful config response, then return null`() {
        coEvery { topicsApi.getTopics() } returns response
        coEvery { response.isSuccessful } returns false

        val repo = TopicsRepo(topicsApi, localDataSource)

        runTest {
            assertNull(repo.getTopics())
        }
    }

    @Test
    fun `Given an exception is thrown fetching the topics response, then return null`() {
        coEvery { topicsApi.getTopics() } throws IOException()

        val repo = TopicsRepo(topicsApi, localDataSource)

        runTest {
            assertNull(repo.getTopics())
        }
    }
     */
}