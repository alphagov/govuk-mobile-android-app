package uk.govuk.app.config.data.remote.model

import com.google.gson.annotations.SerializedName

data class Config(
    @SerializedName("available") val available: Boolean,
    @SerializedName("minimumVersion") val minimumVersion: String,
    @SerializedName("recommendedVersion") val recommendedVersion: String,
    @SerializedName("releaseFlags") val releaseFlags: ReleaseFlags,
    @SerializedName("version") val version: String,
    @SerializedName("lastUpdated") val lastUpdated: String, // Todo - handle date format
)