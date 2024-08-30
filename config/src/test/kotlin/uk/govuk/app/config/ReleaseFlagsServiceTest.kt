package uk.govuk.app.config

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

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
    //    | unset | unset  | false  |
    //    | false | unset  | false  |
    //    | true  | unset  | true   |
    //    | unset | false  | false  |
    //    | unset | true   | true   |
    //    | false | false  | false  |
    //    | false | true   | true   |
    //    | true  | false  | false  |
    //    | true  | true   | true   |

    private val flagName = "search"
    private val unset = mapOf("unset" to true)
    private val flagIsTrue = mapOf(flagName to true)
    private val flagIsFalse = mapOf(flagName to false)

    @Test
    fun globalIsUnsetAndLocalIsUnset() {
        val globalFlags = ReleaseFlags(unset)
        val localFlags = ReleaseFlags(unset)

        assertFalse(ReleaseFlagsService(globalFlags, localFlags).isSearchEnabled())
    }

    @Test
    fun globalIsUnsetAndLocalIsFalse() {
        val globalFlags = ReleaseFlags(unset)
        val localFlags = ReleaseFlags(flagIsFalse)

        assertFalse(ReleaseFlagsService(globalFlags, localFlags).isSearchEnabled())
    }

    @Test
    fun globalIsUnsetAndLocalIsTrue() {
        val globalFlags = ReleaseFlags(unset)
        val localFlags = ReleaseFlags(flagIsTrue)

        assertTrue(ReleaseFlagsService(globalFlags, localFlags).isSearchEnabled())
    }

    @Test
    fun localIsUnsetAndGlobalIsFalse() {
        val globalFlags = ReleaseFlags(flagIsFalse)
        val localFlags = ReleaseFlags(unset)

        assertFalse(ReleaseFlagsService(globalFlags, localFlags).isSearchEnabled())
    }

    @Test
    fun localIsUnsetAndGlobalIsTrue() {
        val globalFlags = ReleaseFlags(flagIsTrue)
        val localFlags = ReleaseFlags(unset)

        assertTrue(ReleaseFlagsService(globalFlags, localFlags).isSearchEnabled())
    }

    @Test
    fun localIsFalseAndGlobalIsFalse() {
        val globalFlags = ReleaseFlags(flagIsFalse)
        val localFlags = ReleaseFlags(flagIsFalse)

        assertFalse(ReleaseFlagsService(globalFlags, localFlags).isSearchEnabled())
    }

    @Test
    fun localIsFalseAndGlobalIsTrue() {
        val globalFlags = ReleaseFlags(flagIsTrue)
        val localFlags = ReleaseFlags(flagIsFalse)

        assertTrue(ReleaseFlagsService(globalFlags, localFlags).isSearchEnabled())
    }

    @Test
    fun localIsTrueAndGlobalIsFalse() {
        val globalFlags = ReleaseFlags(flagIsFalse)
        val localFlags = ReleaseFlags(flagIsTrue)

        assertFalse(ReleaseFlagsService(globalFlags, localFlags).isSearchEnabled())
    }

    @Test
    fun localIsTrueAndGlobalIsTrue() {
        val globalFlags = ReleaseFlags(flagIsTrue)
        val localFlags = ReleaseFlags(flagIsTrue)

        assertTrue(ReleaseFlagsService(globalFlags, localFlags).isSearchEnabled())
    }
}
