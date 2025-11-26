package uk.gov.govuk.config.data

import com.google.gson.Gson
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import okhttp3.Headers
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.Response
import uk.gov.govuk.config.SignatureValidator
import uk.gov.govuk.config.data.remote.ConfigApi
import uk.gov.govuk.config.data.remote.model.Config
import uk.gov.govuk.config.data.remote.model.ConfigResponse
import uk.gov.govuk.data.model.Result.DeviceOffline
import uk.gov.govuk.data.model.Result.Error
import uk.gov.govuk.data.model.Result.InvalidSignature
import uk.gov.govuk.data.model.Result.Success
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

    @Test
    fun `Given no config init, when config is requested, then throw exception`() {
        coEvery { configApi.getConfig() } returns response
        coEvery { response.isSuccessful } returns true
        coEvery { response.body() } returns configResponse.toString()
        coEvery { configResponse.config } returns config

        val repo = ConfigRepo(configApi, gson, signatureValidator)

        val exception = assertThrows(IllegalStateException::class.java) {
            repo.config
        }

        assertEquals("You must init config successfully before use!!!", exception.message)
    }

    @Test
    fun `Given initialized config, when config is requested multiple times, then return same instance`() {
        coEvery { configApi.getConfig() } returns Response.success(configResponse.toString())
        coEvery { signatureValidator.isValidSignature(any(), any()) } returns true
        coEvery { gson.fromJson(any<String>(), ConfigResponse::class.java) } returns ConfigResponse(config, "signature")

        val repo = ConfigRepo(configApi, gson, signatureValidator)

        runTest {
            repo.initConfig()
            val firstCall = repo.config
            val secondCall = repo.config

            assertEquals(config, firstCall)
            assertEquals(config, secondCall)
            assertEquals(firstCall, secondCall)
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

    @Test
    fun `Given a chat poll interval property, when retrieving the chat poll interval, then return property value`() {
        coEvery { configApi.getConfig() } returns Response.success(configResponse.toString())
        coEvery { signatureValidator.isValidSignature(any(), any()) } returns true
        coEvery { gson.fromJson(any<String>(), ConfigResponse::class.java) } returns ConfigResponse(config, "signature")
        every { config.chatPollIntervalSeconds } returns 0.5

        val repo = ConfigRepo(configApi, gson, signatureValidator)

        runTest {
            repo.initConfig()
            assertEquals(0.5, repo.chatPollIntervalSeconds, 0.0)
        }
    }

    @Test
    fun `Given the chat poll interval property is missing, when retrieving the chat poll interval, then return fallback value`() {
        coEvery { configApi.getConfig() } returns Response.success(configResponse.toString())
        coEvery { signatureValidator.isValidSignature(any(), any()) } returns true
        coEvery { gson.fromJson(any<String>(), ConfigResponse::class.java) } returns ConfigResponse(config, "signature")
        every { config.chatPollIntervalSeconds } returns null

        val repo = ConfigRepo(configApi, gson, signatureValidator)

        runTest {
            repo.initConfig()
            assertEquals(3.0, repo.chatPollIntervalSeconds, 0.0)
        }
    }

    @Test
    fun `Given the chat poll interval property is 0, when retrieving the chat poll interval, then return fallback value`() {
        coEvery { configApi.getConfig() } returns Response.success(configResponse.toString())
        coEvery { signatureValidator.isValidSignature(any(), any()) } returns true
        coEvery { gson.fromJson(any<String>(), ConfigResponse::class.java) } returns ConfigResponse(config, "signature")
        every { config.chatPollIntervalSeconds } returns 0.0

        val repo = ConfigRepo(configApi, gson, signatureValidator)

        runTest {
            repo.initConfig()
            assertEquals(3.0, repo.chatPollIntervalSeconds, 0.0)
        }
    }

    @Test
    fun `Given a response with a signature header, the specific signature is extracted`() {
        val specificSignature = "signature-123"
        val headers = Headers.headersOf("x-amz-meta-govuk-sig", specificSignature)

        coEvery { configApi.getConfig() } returns Response.success(
            configResponse.toString(),
            headers
        )
        coEvery { signatureValidator.isValidSignature(any(), any()) } returns true
        coEvery { gson.fromJson(any<String>(), ConfigResponse::class.java) } returns ConfigResponse(
            config,
            "sig"
        )

        val repo = ConfigRepo(configApi, gson, signatureValidator)

        runTest {
            repo.initConfig()
            coVerify { signatureValidator.isValidSignature(specificSignature, any()) }
        }
    }

    @Test
    fun `Given a response without a signature header, signature defaults to empty string`() {
        val headers = Headers.headersOf() // Empty headers

        coEvery { configApi.getConfig() } returns Response.success(
            configResponse.toString(),
            headers
        )
        coEvery { signatureValidator.isValidSignature(any(), any()) } returns true
        coEvery { gson.fromJson(any<String>(), ConfigResponse::class.java) } returns ConfigResponse(
            config,
            "sig"
        )

        val repo = ConfigRepo(configApi, gson, signatureValidator)

        runTest {
            repo.initConfig()
            coVerify { signatureValidator.isValidSignature("", any()) }
        }
    }
}
