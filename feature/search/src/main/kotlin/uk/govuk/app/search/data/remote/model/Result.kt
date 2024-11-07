package uk.govuk.app.search.data.remote.model

import com.google.gson.annotations.SerializedName
import uk.govuk.app.search.domain.SearchConfig.DESCRIPTION_RESPONSE_FIELD

data class Result(
    @SerializedName("title") val title: String,
    @SerializedName(DESCRIPTION_RESPONSE_FIELD) val description: String?,
    @SerializedName("link") val link: String
)
