package uk.govuk.app.config.data.remote.model

data class ConfigResponse(
    val config: Config,
    val signature: String
)
