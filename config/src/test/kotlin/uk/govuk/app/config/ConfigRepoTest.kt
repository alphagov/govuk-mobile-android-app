package uk.govuk.app.config

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
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
    fun `Given a successful config init, when config is requested, then return config`() {
        coEvery { configApi.getConfig() } returns response
        coEvery { response.isSuccessful } returns true
        coEvery { response.body() } returns configResponse
        coEvery { configResponse.config } returns config

        val repo = ConfigRepo(configApi)

        runTest {
            repo.initConfig()
            assertEquals(config, repo.config)
        }
    }

    @Test (expected = IllegalStateException::class)
    fun `Given no config init, when config is requested, then throw exception`() {
        coEvery { configApi.getConfig() } returns response
        coEvery { response.isSuccessful } returns true
        coEvery { response.body() } returns configResponse
        coEvery { configResponse.config } returns config

        val repo = ConfigRepo(configApi)

        runTest {
            repo.config
        }
    }

    @Test
    fun `Given a successful config response with a body, then return true`() {
        coEvery { configApi.getConfig() } returns response
        coEvery { response.isSuccessful } returns true
        coEvery { response.body() } returns configResponse
        coEvery { configResponse.config } returns config

        val repo = ConfigRepo(configApi)

        runTest {
            assertTrue(repo.initConfig())
        }
    }

    @Test
    fun `Given a successful config response with an empty body, then return false`() {
        coEvery { configApi.getConfig() } returns response
        coEvery { response.isSuccessful } returns true
        coEvery { response.body() } returns null

        val repo = ConfigRepo(configApi)

        runTest {
            assertFalse(repo.initConfig())
        }
    }

    @Test
    fun `Given an unsuccessful config response, then return false`() {
        coEvery { configApi.getConfig() } returns response
        coEvery { response.isSuccessful } returns false

        val repo = ConfigRepo(configApi)

        runTest {
            assertFalse(repo.initConfig())
        }
    }

    @Test
    fun `Given an exception is thrown fetching the config response, then return false`() {
        coEvery { configApi.getConfig() } throws IOException()

        val repo = ConfigRepo(configApi)

        runTest {
            assertFalse(repo.initConfig())
        }
    }

}