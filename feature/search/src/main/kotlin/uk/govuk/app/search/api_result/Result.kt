package uk.govuk.app.search.api_result

import com.google.gson.annotations.SerializedName

data class Result(
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("link") val link: String
)
