package uk.gov.govuk.notifications.model

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import org.json.JSONObject

internal data class AdditionalData(
    @SerializedName("deeplink") val deepLink: String
)

/**
 * Tries to parse a JSON object to an Additional Data object.
 *
 * If successful returns an Additional Data object.
 * If unsuccessful returns null.
 */
internal fun JSONObject?.asAdditionalData(): AdditionalData? {
    return try {
        val gson = Gson()
        gson.fromJson(this.toString(), AdditionalData::class.java)
    } catch (_: Exception) {
        null
    }
}
