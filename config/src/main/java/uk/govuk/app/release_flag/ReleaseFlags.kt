package uk.govuk.app.release_flag

data class ReleaseFlags(val flags: Map<String, Boolean>) {
    fun isEmpty() = flags.isEmpty()
    fun flagEnabled(flagName: String): Boolean? = flags[flagName]
}
