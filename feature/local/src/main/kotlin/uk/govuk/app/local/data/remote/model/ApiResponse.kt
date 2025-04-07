package uk.govuk.app.local.data.remote.model

import com.google.gson.annotations.SerializedName

data class ApiResponse(
    @SerializedName("local_authority") val localAuthority: LocalAuthority?,
    @SerializedName("addresses") val addresses: List<Address>?,
    @SerializedName("message") val message: String?
)
