package uk.govuk.app.config

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import retrofit2.Response
import uk.govuk.app.config.data.remote.ConfigApi
import uk.govuk.app.config.data.remote.model.Config
import uk.govuk.app.config.data.remote.model.ConfigResponse
import java.io.IOException

class ConfigRepoTest {

    private val configApi = mockk<ConfigApi>(relaxed = true)
    private val response = mockk<Response<ConfigResponse>>(relaxed = true)
    private val configResponse = mockk<ConfigResponse>(relaxed = true)
    private val config = mockk<Config>(relaxed = true)

    @Test
    fun `Given a successful config response with a body, then return config`() {
        coEvery { configApi.getConfig() } returns response
        coEvery { response.isSuccessful } returns true
        coEvery { response.body() } returns configResponse
        coEvery { configResponse.config } returns config

        val repo = ConfigRepo(configApi)

        runTest {
            assertEquals(config, repo.getConfig())
        }
    }

    @Test
    fun `Given a successful config response with an empty body, then return null`() {
        coEvery { configApi.getConfig() } returns response
        coEvery { response.isSuccessful } returns true
        coEvery { response.body() } returns null

        val repo = ConfigRepo(configApi)

        runTest {
            assertNull(repo.getConfig())
        }
    }

    @Test
    fun `Given an unsuccessful config response, then return null`() {
        coEvery { configApi.getConfig() } returns response
        coEvery { response.isSuccessful } returns false

        val repo = ConfigRepo(configApi)

        runTest {
            assertNull(repo.getConfig())
        }
    }

    @Test
    fun `Given an exception is thrown fetching the config response, then return null`() {
        coEvery { configApi.getConfig() } throws IOException()

        val repo = ConfigRepo(configApi)

        runTest {
            assertNull(repo.getConfig())
        }
    }

}