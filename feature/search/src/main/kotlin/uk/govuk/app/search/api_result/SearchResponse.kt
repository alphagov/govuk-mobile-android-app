package uk.govuk.app.search.api_result

import com.google.gson.annotations.SerializedName

data class SearchResponse(
    @SerializedName("total") val total: Int,
    @SerializedName("results") val results: List<Result>
)
