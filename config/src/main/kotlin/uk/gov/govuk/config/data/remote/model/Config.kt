package uk.gov.govuk.config.data.remote.model

import com.google.gson.annotations.SerializedName

data class Config(
    @SerializedName("available") val available: Boolean,
    @SerializedName("minimumVersion") val minimumVersion: String,
    @SerializedName("recommendedVersion") val recommendedVersion: String,
    @SerializedName("releaseFlags") val releaseFlags: ReleaseFlags,
    @SerializedName("version") val version: String,
    @SerializedName("chatPollIntervalSeconds") val chatPollIntervalSeconds: Double?,
    @SerializedName("alertBanner") val alertBanner: AlertBanner?,
    @SerializedName("userFeedbackBanner") val userFeedbackBanner: UserFeedbackBanner?,
    @SerializedName("chatUrls") val chatUrls: ChatUrls,
    @SerializedName("refreshTokenExpirySeconds") val refreshTokenExpirySeconds: Long
)
