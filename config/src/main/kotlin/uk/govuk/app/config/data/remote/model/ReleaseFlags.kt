package uk.govuk.app.config.data.remote.model

import com.google.gson.annotations.SerializedName

data class ReleaseFlags(
    @SerializedName("onboarding") val onboarding: Boolean,
    @SerializedName("search") val search: Boolean
)