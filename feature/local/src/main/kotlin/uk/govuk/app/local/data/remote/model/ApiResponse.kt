package uk.govuk.app.local.data.remote.model

import com.google.gson.annotations.SerializedName

sealed class ApiResponse {
    data class LocalAuthorityResponse(
        @SerializedName("local_authority") val localAuthority: LocalAuthority
    ) : ApiResponse()

    data class AddressListResponse(
        @SerializedName("addresses") val addresses: List<Address>
    ) : ApiResponse()

    data class MessageResponse(
        @SerializedName("message") val message: String
    ) : ApiResponse()
}
