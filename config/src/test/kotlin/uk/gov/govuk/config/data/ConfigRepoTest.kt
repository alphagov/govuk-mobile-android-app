package uk.gov.govuk.config.data

import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertThrows
import org.junit.Test
import uk.gov.govuk.config.data.remote.model.Config
import uk.gov.govuk.config.data.remote.source.FirebaseConfigDataSource
import uk.gov.govuk.config.data.remote.source.GovUkConfigDataSource
import uk.gov.govuk.data.model.Result.Success

class ConfigRepoTest {

    private val govUkDataSource = mockk<GovUkConfigDataSource>(relaxed = true)
    private val firebaseDataSource = mockk<FirebaseConfigDataSource>(relaxed = true)
    private val config = mockk<Config>(relaxed = true)

    @Test
    fun `Given a successful config init, when config properties are requested, then return correct values`() {
        coEvery { govUkDataSource.fetchConfig() } returns Success(config)
        coEvery { firebaseDataSource.fetch() } returns true

        val repo = ConfigRepoImpl(govUkDataSource, firebaseDataSource)

        runTest {
            repo.initConfig()
            assertEquals(config.available, repo.isAvailable)
            assertEquals(config.minimumVersion, repo.minimumVersion)
            assertEquals(config.releaseFlags.search, repo.isSearchEnabled)
            assertEquals(config.chatUrls, repo.chatUrls)
        }
    }

    @Test
    fun `Given no config init, when any config property is requested, then throw exception`() {
        val repo = ConfigRepoImpl(govUkDataSource, firebaseDataSource)
        val exception = assertThrows(IllegalStateException::class.java) {
            repo.isAvailable
        }

        assertEquals("You must init config successfully before use!!!", exception.message)
    }

    @Test
    fun `Given initialized config, when object properties are requested multiple times, they remain consistent`() {
        coEvery { govUkDataSource.fetchConfig() } returns Success(config)
        val repo = ConfigRepoImpl(govUkDataSource, firebaseDataSource)

        runTest {
            repo.initConfig()
            val firstCall = repo.chatUrls
            val secondCall = repo.chatUrls

            assertSame(firstCall, secondCall)
        }
    }

    @Test
    fun `Given a chat poll interval property, when retrieving the chat poll interval, then return property value`() = runTest {
        every { config.chatPollIntervalSeconds } returns 0.5
        coEvery { govUkDataSource.fetchConfig() } returns Success(config)

        val repo = ConfigRepoImpl(govUkDataSource, firebaseDataSource)
        repo.initConfig()

        assertEquals(0.5, repo.chatPollIntervalSeconds, 0.0)
    }

    @Test
    fun `Given the chat poll interval property is missing, when retrieving the chat poll interval, then return fallback value`() = runTest {
        every { config.chatPollIntervalSeconds } returns null
        coEvery { govUkDataSource.fetchConfig() } returns Success(config)

        val repo = ConfigRepoImpl(govUkDataSource, firebaseDataSource)
        repo.initConfig()

        assertEquals(3.0, repo.chatPollIntervalSeconds, 0.0)
    }

    @Test
    fun `Given the chat poll interval property is 0, when retrieving the chat poll interval, then return fallback value`() = runTest {
        every { config.chatPollIntervalSeconds } returns 0.0
        coEvery { govUkDataSource.fetchConfig() } returns Success(config)

        val repo = ConfigRepoImpl(govUkDataSource, firebaseDataSource)
        repo.initConfig()

        assertEquals(3.0, repo.chatPollIntervalSeconds, 0.0)
    }
}
