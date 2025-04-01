package uk.govuk.app.local.data.remote.model

import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter

class ApiResponseAdapter(
    private val gson: Gson
) : TypeAdapter<ApiResponse>() {
    override fun write(out: JsonWriter, value: ApiResponse?) {
        throw UnsupportedOperationException("Serialization of ApiResponse is not supported.")
    }

    override fun read(reader: JsonReader): ApiResponse? {
        var result: Any? = null
        var type: String? = null

        reader.beginObject()
        while (reader.hasNext()) {
            when (reader.nextName()) {
                "local_authority" -> {
                    type = "localAuthority"
                    val jsonObject = gson.toJson(readJsonObject(reader))
                    result = gson.fromJson(
                        "{\"local_authority\":$jsonObject}",
                        ApiResponse.LocalAuthorityResponse::class.java
                    )
                }

                "addresses" -> {
                    type = "addresses"
                    val jsonArray = gson.toJson(readJsonArray(reader))
                    result = gson.fromJson(
                        "{\"addresses\":$jsonArray}",
                        ApiResponse.AddressListResponse::class.java
                    )
                }

                "message" -> {
                    type = "message"
                    val jsonString = gson.toJson(reader.nextString())
                    result = gson.fromJson(
                        "{\"message\":$jsonString}",
                        ApiResponse.MessageResponse::class.java
                    )
                }
            }
        }
        reader.endObject()

        return when (type) {
            "localAuthority" -> result as ApiResponse.LocalAuthorityResponse
            "addresses" -> result as ApiResponse.AddressListResponse
            "message" -> result as ApiResponse.MessageResponse
            else -> null
        }
    }

    private fun readJsonObject(reader: JsonReader): Map<String, Any?> {
        val jsonObject = mutableMapOf<String, Any?>()
        reader.beginObject()
        while (reader.hasNext()) {
            jsonObject[reader.nextName()] = readValue(reader)
        }
        reader.endObject()
        return jsonObject
    }

    private fun readJsonArray(reader: JsonReader): List<Any?> {
        val jsonArray = mutableListOf<Any?>()
        reader.beginArray()
        while (reader.hasNext()) {
            jsonArray.add(readValue(reader))
        }
        reader.endArray()
        return jsonArray
    }

    private fun readValue(reader: JsonReader): Any? {
        return when (reader.peek()) {
            JsonToken.BEGIN_OBJECT -> readJsonObject(reader)
            JsonToken.STRING -> reader.nextString()
            JsonToken.NULL -> {
                reader.nextNull()
                null
            }

            else -> throw IllegalStateException("Unexpected token: ${reader.peek()}")
        }
    }
}
