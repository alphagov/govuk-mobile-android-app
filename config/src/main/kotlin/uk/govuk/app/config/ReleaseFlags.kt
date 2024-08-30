package uk.govuk.app.config

data class ReleaseFlags(val flags: Map<String, Boolean>) {
    fun isEmpty() = flags.isEmpty()
    fun flagEnabled(flagName: String): Boolean? = flags[flagName]
}
