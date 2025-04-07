package uk.govuk.app.local.data.remote.model

import com.google.gson.annotations.SerializedName

data class Address(
    @SerializedName("address") val address: String,
    @SerializedName("slug") val slug: String,
    @SerializedName("name") val name: String
)
