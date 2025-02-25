package uk.govuk.app.local.data.remote.model

import com.google.gson.annotations.SerializedName

data class LocalLinksManagerApiResponse(
    @SerializedName("name") val name: String,
    @SerializedName("homepage_url") val homepageUrl: String,
    @SerializedName("local_custodian_code") val localCustodianCode: Int
)
