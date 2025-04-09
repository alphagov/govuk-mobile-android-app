package uk.gov.govuk.config.data.remote.model

import com.google.gson.annotations.SerializedName

data class ReleaseFlags(
    @SerializedName("onboarding") val onboarding: Boolean,
    @SerializedName("search") val search: Boolean,
    @SerializedName("recentActivity") val recentActivity: Boolean,
    @SerializedName("topics") val topics: Boolean,
    @SerializedName("notifications") val notifications: Boolean,
    @SerializedName("localServices") val localServices: Boolean
)