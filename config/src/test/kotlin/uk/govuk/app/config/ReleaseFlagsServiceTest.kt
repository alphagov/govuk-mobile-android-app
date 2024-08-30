package uk.govuk.app.config

import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import uk.govuk.app.config.flags.ReleaseFlagsService
import uk.govuk.app.config.flags.local.LocalFlagRepo
import uk.govuk.app.config.flags.remote.RemoteFlagRepo

class ReleaseFlagsServiceTest {
    // Scenario - Global vs local feature flag hierarchy:
    //    GIVEN a local feature flag is set
    //    WHEN the global feature flag is delivered
    //    THEN the global feature flag is respected when set, even if it differs from the local feature flag
    //
    //    Which yields the following results table:
    //
    //    | Local | Global | Result |
    //    |-------|--------|--------|
    //    | false | unset  | false  |
    //    | true  | unset  | true   |
    //    | false | false  | false  |
    //    | false | true   | true   |
    //    | true  | false  | false  |
    //    | true  | true   | true   |

    private val localFlagRepo = mockk<LocalFlagRepo>(relaxed = true)
    private val remoteFlagRepo = mockk<RemoteFlagRepo>(relaxed = true)

    @Test
    fun globalIsUnsetAndLocalIsFalse() {
        every { remoteFlagRepo.isSearchEnabled() } returns null
        every { localFlagRepo.isSearchEnabled() } returns false

        assertFalse(ReleaseFlagsService(localFlagRepo, remoteFlagRepo).isSearchEnabled())
    }

    @Test
    fun globalIsUnsetAndLocalIsTrue() {
        every { remoteFlagRepo.isSearchEnabled() } returns null
        every { localFlagRepo.isSearchEnabled() } returns true

        assertTrue(ReleaseFlagsService(localFlagRepo, remoteFlagRepo).isSearchEnabled())
    }

    @Test
    fun localIsFalseAndGlobalIsFalse() {
        every { remoteFlagRepo.isSearchEnabled() } returns false
        every { localFlagRepo.isSearchEnabled() } returns false

        assertFalse(ReleaseFlagsService(localFlagRepo, remoteFlagRepo).isSearchEnabled())
    }

    @Test
    fun localIsFalseAndGlobalIsTrue() {
        every { remoteFlagRepo.isSearchEnabled() } returns true
        every { localFlagRepo.isSearchEnabled() } returns false

        assertTrue(ReleaseFlagsService(localFlagRepo, remoteFlagRepo).isSearchEnabled())
    }

    @Test
    fun localIsTrueAndGlobalIsFalse() {
        every { remoteFlagRepo.isSearchEnabled() } returns false
        every { localFlagRepo.isSearchEnabled() } returns true

        assertFalse(ReleaseFlagsService(localFlagRepo, remoteFlagRepo).isSearchEnabled())
    }

    @Test
    fun localIsTrueAndGlobalIsTrue() {
        every { remoteFlagRepo.isSearchEnabled() } returns true
        every { localFlagRepo.isSearchEnabled() } returns true

        assertTrue(ReleaseFlagsService(localFlagRepo, remoteFlagRepo).isSearchEnabled())
    }
}
