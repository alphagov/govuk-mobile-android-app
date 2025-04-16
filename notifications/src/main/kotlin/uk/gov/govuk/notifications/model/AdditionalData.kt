package uk.gov.govuk.notifications.model

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

internal data class AdditionalData(
    @SerializedName("deeplink") val deepLink: String
)

internal fun String.asAdditionalData(): AdditionalData? {
    return try {
        val gson = Gson()
        gson.fromJson(this, AdditionalData::class.java)
    } catch (_: Exception) {
        null
    }
}
