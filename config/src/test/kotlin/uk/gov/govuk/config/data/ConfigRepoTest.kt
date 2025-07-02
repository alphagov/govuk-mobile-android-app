package uk.gov.govuk.config.data

import com.google.gson.Gson
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.Response
import uk.gov.govuk.config.SignatureValidator
import uk.gov.govuk.config.data.remote.ConfigApi
import uk.gov.govuk.config.data.remote.model.Config
import uk.gov.govuk.config.data.remote.model.ConfigResponse
import uk.gov.govuk.data.model.Result.*
import java.io.IOException
import java.net.UnknownHostException

class ConfigRepoTest {

    private val configApi = mockk<ConfigApi>(relaxed = true)
    private val response = mockk<Response<String>>(relaxed = true)
    private val configResponse = mockk<ConfigResponse>(relaxed = true)
    private val config = mockk<Config>(relaxed = true)
    private val gson = mockk<Gson>(relaxed = true)
    private val signatureValidator = mockk<SignatureValidator>(relaxed = true)

    @Test
    fun `Given a successful config init, when config is requested, then return config`() {
        coEvery { configApi.getConfig() } returns Response.success(configResponse.toString())
        coEvery { signatureValidator.isValidSignature(any(), any()) } returns true
        coEvery { gson.fromJson(any<String>(), ConfigResponse::class.java) } returns ConfigResponse(config, "signature")

        val repo = ConfigRepo(configApi, gson, signatureValidator)

        runTest {
            repo.initConfig()
            assertEquals(config, repo.config)
        }
    }

    @Test (expected = IllegalStateException::class)
    fun `Given no config init, when config is requested, then throw exception`() {
        coEvery { configApi.getConfig() } returns response
        coEvery { response.isSuccessful } returns true
        coEvery { response.body() } returns configResponse.toString()
        coEvery { configResponse.config } returns config

        val repo = ConfigRepo(configApi, gson, signatureValidator)

        runTest {
            repo.config
        }
    }

    @Test
    fun `Given a successful config response with a body, then return success`() {
        coEvery { configApi.getConfig() } returns Response.success(configResponse.toString())
        coEvery { signatureValidator.isValidSignature(any(), any()) } returns true
        coEvery { gson.fromJson(any<String>(), ConfigResponse::class.java) } returns ConfigResponse(config, "signature")

        val repo = ConfigRepo(configApi, gson, signatureValidator)

        runTest {
            assertTrue(repo.initConfig() is Success)
        }
    }

    @Test
    fun `Given a successful config response with an empty body, then return failure`() {
        coEvery { configApi.getConfig() } returns response
        coEvery { response.isSuccessful } returns true
        coEvery { response.body() } returns null

        val repo = ConfigRepo(configApi, gson, signatureValidator)

        runTest {
            assertTrue(repo.initConfig() is Error)
        }
    }

    @Test
    fun `Given an unsuccessful config response, then return failure`() {
        coEvery { configApi.getConfig() } returns response
        coEvery { response.isSuccessful } returns false

        val repo = ConfigRepo(configApi, gson, signatureValidator)

        runTest {
            assertTrue(repo.initConfig() is Error)
        }
    }

    @Test
    fun `Given an unknown host exception is thrown fetching the config response, then return device offline failure`() {
        coEvery { configApi.getConfig() } throws UnknownHostException()

        val repo = ConfigRepo(configApi, gson, signatureValidator)

        runTest {
            assertTrue(repo.initConfig() is DeviceOffline)
        }
    }

    @Test
    fun `Given an exception is thrown fetching the config response, then return failure`() {
        coEvery { configApi.getConfig() } throws IOException()

        val repo = ConfigRepo(configApi, gson, signatureValidator)

        runTest {
            assertTrue(repo.initConfig() is Error)
        }
    }

    @Test
    fun `Given an invalid signature, when config is requested, then return failure`() {
        coEvery { configApi.getConfig() } returns Response.success(configResponse.toString())
        coEvery { signatureValidator.isValidSignature(any(), any()) } returns false
        coEvery { gson.fromJson(any<String>(), ConfigResponse::class.java) } returns ConfigResponse(config, "signature")

        val repo = ConfigRepo(configApi, gson, signatureValidator)

        runTest {
            assertTrue(repo.initConfig() is InvalidSignature)
        }
    }
}
