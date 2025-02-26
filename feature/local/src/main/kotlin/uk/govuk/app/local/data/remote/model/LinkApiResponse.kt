package uk.govuk.app.local.data.remote.model

import com.google.gson.annotations.SerializedName

data class LinkApiResponse(
    @SerializedName("lgsl_code") val lgslCode: Int,
    @SerializedName("lgil_code") val lgilCode: Int,
    @SerializedName("url") val url: String
)
