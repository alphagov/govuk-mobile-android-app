package uk.gov.govuk.data.flex.model

import com.google.gson.annotations.SerializedName

data class FlexPreferencesResponse(
    @SerializedName("userId") val userId: String
)
