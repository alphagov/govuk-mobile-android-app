package uk.govuk.app.search.data.remote.model

import com.google.gson.annotations.SerializedName

data class Result(
    @SerializedName("title") val title: String,
    @SerializedName("description_with_highlighting") val description: String?,
    @SerializedName("link") val link: String
)
