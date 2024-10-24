package uk.govuk.app.config.data.flags

import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import uk.govuk.app.config.data.ConfigRepo

class FlagRepoTest {

    private val debugFlags = mockk<DebugFlags>(relaxed = true)
    private val configRepo = mockk<ConfigRepo>(relaxed = true)

    @After
    fun tearDown() {
        // Remove mocks to prevent side effects
        unmockkAll()
    }

    @Test
    fun `Given debug is enabled, debug flag is true and remote flag is true, When is enabled, then return true`() {
        val enabled = isEnabled(
            debugEnabled = true,
            debugFlag = true,
            remoteFlag = true
        )

        assertTrue(enabled)
    }

    @Test
    fun `Given debug is enabled, debug flag is true and remote flag is false, When is enabled, then return true`() {
        val enabled = isEnabled(
            debugEnabled = true,
            debugFlag = true,
            remoteFlag = false
        )

        assertTrue(enabled)
    }

    @Test
    fun `Given debug is enabled, debug flag is false and remote flag is true, When is enabled, then return false`() {
        val enabled = isEnabled(
            debugEnabled = true,
            debugFlag = false,
            remoteFlag = true
        )

        assertFalse(enabled)
    }

    @Test
    fun `Given debug is enabled, debug flag is false and remote flag is false, When is enabled, then return false`() {
        val enabled = isEnabled(
            debugEnabled = true,
            debugFlag = false,
            remoteFlag = false
        )

        assertFalse(enabled)
    }

    @Test
    fun `Given debug is enabled, debug flag is unset and remote flag is true, When is enabled, then return true`() {
        val enabled = isEnabled(
            debugEnabled = true,
            debugFlag = null,
            remoteFlag = true
        )

        assertTrue(enabled)
    }

    @Test
    fun `Given debug is enabled, debug flag is unset and remote flag is false, When is enabled, then return false`() {
        val enabled = isEnabled(
            debugEnabled = true,
            debugFlag = null,
            remoteFlag = false
        )

        assertFalse(enabled)
    }

    @Test
    fun `Given debug is disabled, debug flag is true and remote flag is true, When is enabled, then return true`() {
        val enabled = isEnabled(
            debugEnabled = false,
            debugFlag = true,
            remoteFlag = true
        )

        assertTrue(enabled)
    }

    @Test
    fun `Given debug is disabled, debug flag is true and remote flag is false, When is enabled, then return false`() {
        val enabled = isEnabled(
            debugEnabled = false,
            debugFlag = true,
            remoteFlag = false
        )

        assertFalse(enabled)
    }

    @Test
    fun `Given debug is disabled, debug flag is false and remote flag is true, When is enabled, then return true`() {
        val enabled = isEnabled(
            debugEnabled = false,
            debugFlag = false,
            remoteFlag = true
        )

        assertTrue(enabled)
    }

    @Test
    fun `Given debug is disabled, debug flag is false and remote flag is false, When is enabled, then return false`() {
        val enabled = isEnabled(
            debugEnabled = false,
            debugFlag = false,
            remoteFlag = false
        )

        assertFalse(enabled)
    }

    @Test
    fun `Given debug is disabled, debug flag is unset and remote flag is true, When is enabled, then return true`() {
        val enabled = isEnabled(
            debugEnabled = false,
            debugFlag = null,
            remoteFlag = true
        )

        assertTrue(enabled)
    }

    @Test
    fun `Given debug is disabled, debug flag is unset and remote flag is false, When is enabled, then return false`() {
        val enabled = isEnabled(
            debugEnabled = false,
            debugFlag = null,
            remoteFlag = false
        )

        assertFalse(enabled)
    }

    @Test
    fun `Given onboarding is enabled, When is onboarding enabled, then return true`() {
        mockkStatic(::isEnabled)
        every { isEnabled(any(), any(), any()) } returns true

        val flagRepo = FlagRepo(debugFlags, configRepo)

        assertTrue(flagRepo.isOnboardingEnabled())
    }

    @Test
    fun `Given onboarding is disabled, When is onboarding enabled, then return false`() {
        mockkStatic(::isEnabled)
        every { isEnabled(any(), any(), any()) } returns false

        val flagRepo = FlagRepo(debugFlags, configRepo)

        assertFalse(flagRepo.isOnboardingEnabled())
    }

    @Test
    fun `Given search is enabled, When is search enabled, then return true`() {
        mockkStatic(::isEnabled)
        every { isEnabled(any(), any(), any()) } returns true

        val flagRepo = FlagRepo(debugFlags, configRepo)

        assertTrue(flagRepo.isSearchEnabled())
    }

    @Test
    fun `Given search is disabled, When is search enabled, then return false`() {
        mockkStatic(::isEnabled)
        every { isEnabled(any(), any(), any()) } returns false

        val flagRepo = FlagRepo(debugFlags, configRepo)

        assertFalse(flagRepo.isSearchEnabled())
    }

    @Test
    fun `Given topics is enabled, When is topics enabled, then return true`() {
        mockkStatic(::isEnabled)
        every { isEnabled(any(), any(), any()) } returns true

        val flagRepo = FlagRepo(debugFlags, configRepo)

        assertTrue(flagRepo.isTopicsEnabled())
    }

    @Test
    fun `Given topics is disabled, When is topics enabled, then return false`() {
        mockkStatic(::isEnabled)
        every { isEnabled(any(), any(), any()) } returns false

        val flagRepo = FlagRepo(debugFlags, configRepo)

        assertFalse(flagRepo.isTopicsEnabled())
    }
}