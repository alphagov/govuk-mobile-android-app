package uk.gov.govuk.config.data.serialisation

import com.google.gson.TypeAdapter
import com.google.gson.annotations.SerializedName
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import uk.gov.govuk.config.data.remote.model.EmergencyBannerType

/**
 * Type adapter for EmergencyBannerType to safeguard against unknown types.
 * Defaults to INFORMATION if there's no match (for example misspelling)
 */
class EmergencyBannerTypeAdapter : TypeAdapter<EmergencyBannerType>() {

    private val bannerTypeMap: Map<String, EmergencyBannerType> =
        EmergencyBannerType.entries.associateBy {
            it.javaClass.getField(it.name).getAnnotation(SerializedName::class.java)?.value
                ?: throw IllegalStateException("Missing @SerializedName annotation for enum constant ${it.name}")
        }

    override fun read(reader: JsonReader): EmergencyBannerType {
        val typeString = reader.nextString()
        return bannerTypeMap[typeString] ?: EmergencyBannerType.INFORMATION // default to INFORMATION
    }

    override fun write(out: JsonWriter, value: EmergencyBannerType) {
        val serializedName = bannerTypeMap.entries.find { it.value == value }?.key
            ?: throw IllegalStateException("Missing @SerializedName annotation for enum constant ${value.name}")

        out.value(serializedName)
    }
}