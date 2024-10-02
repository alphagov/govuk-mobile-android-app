package uk.govuk.app.topics.data.remote.model

import com.google.gson.annotations.SerializedName

data class TopicItem(
    @SerializedName("ref") val ref: String,
    @SerializedName("title") val title: String
)