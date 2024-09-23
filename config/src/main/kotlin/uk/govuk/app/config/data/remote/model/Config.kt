package uk.govuk.app.config.data.remote.model

data class Config(
    val available: Boolean,
    val minimumVersion: String,
    val recommendedVersion: String,
    val releaseFlags: ReleaseFlags,
    val version: String,
    val lastUpdated: String, // Todo - handle date format
)