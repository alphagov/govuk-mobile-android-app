package uk.govuk.app.release_flag

class ReleaseFlagsService(
    private var globalFlags: ReleaseFlags = ReleaseFlags(mapOf()),
    private var localFlags: ReleaseFlags = ReleaseFlags(mapOf("search" to true))
) {
    fun isSearchEnabled(): Boolean {
        if (globalFlags.isEmpty()) {
            globalFlags = getGlobalReleaseFlags()
        }
        return isEnabled("search", globalFlags, localFlags)
    }

    private fun isEnabled(
        flagName: String,
        globalFlags: ReleaseFlags,
        localFlags: ReleaseFlags
    ): Boolean {
        return globalFlags.flagEnabled(flagName) ?: localFlags.flagEnabled(flagName) ?: false
    }

    private fun getGlobalReleaseFlags(): ReleaseFlags {
//        TODO: we need to get this from the API call
        return ReleaseFlags(mapOf("search" to true))
    }
}
