package uk.govuk.app.topics

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import retrofit2.Response
import uk.govuk.app.topics.data.remote.TopicsApi
import uk.govuk.app.topics.data.remote.model.TopicItem
import java.io.IOException

class TopicsRepoTest{

    private val topicsApi = mockk<TopicsApi>(relaxed = true)
    private val response = mockk<Response<List<TopicItem>>>(relaxed = true)
    private val topics = mockk<List<TopicItem>>(relaxed = true)

    @Test
    fun `Given a successful topics response with a body, then return topics`() {
        coEvery { topicsApi.getTopics() } returns response
        coEvery { response.isSuccessful } returns true
        coEvery { response.body() } returns topics

        val repo = TopicsRepo(topicsApi)

        runTest {
            assertEquals(repo.getTopics(), topics)
        }
    }

    @Test
    fun `Given a successful topics response with an empty body, then return null`() {
        coEvery { topicsApi.getTopics() } returns response
        coEvery { response.isSuccessful } returns true
        coEvery { response.body() } returns null

        val repo = TopicsRepo(topicsApi)

        runTest {
            assertNull(repo.getTopics())
        }
    }

    @Test
    fun `Given an unsuccessful config response, then return null`() {
        coEvery { topicsApi.getTopics() } returns response
        coEvery { response.isSuccessful } returns false

        val repo = TopicsRepo(topicsApi)

        runTest {
            assertNull(repo.getTopics())
        }
    }

    @Test
    fun `Given an exception is thrown fetching the topics response, then return null`() {
        coEvery { topicsApi.getTopics() } throws IOException()

        val repo = TopicsRepo(topicsApi)

        runTest {
            assertNull(repo.getTopics())
        }
    }
}