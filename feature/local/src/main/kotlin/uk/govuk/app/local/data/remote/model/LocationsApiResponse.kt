package uk.govuk.app.local.data.remote.model

import com.google.gson.annotations.SerializedName

data class LocationsApiResponse(
    @SerializedName("postcode") val postcode: String,
    @SerializedName("address") val address: String,
    @SerializedName("local_custodian_code") val localCustodianCode: Int
)
