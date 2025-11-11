package uk.gov.govuk.config.data.remote.model

import com.google.gson.annotations.SerializedName

data class EmergencyBanner(
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String?,
    @SerializedName("body") val body: String?,
    @SerializedName("link") val link: Link?,
    @SerializedName("type") val type: BannerType,
    @SerializedName("image") val image: String,
    @SerializedName("allowsDismissal") val allowsDismissal: Boolean = true
)

enum class BannerType {
    @SerializedName("notable-death")
    NOTABLE_DEATH,

    @SerializedName("national-emergency")
    NATIONAL_EMERGENCY,

    @SerializedName("local-emergency")
    LOCAL_EMERGENCY,

    @SerializedName("information")
    INFORMATION
}
