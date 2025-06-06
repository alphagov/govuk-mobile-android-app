package uk.gov.govuk.chat.data.remote.model

import com.google.gson.annotations.SerializedName
import uk.gov.govuk.chat.ui.model.AnswerUi

data class Answer(
    @SerializedName("id") val id: String,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("message") val message: String
) {
    fun toAnswerUi(): AnswerUi {
        return AnswerUi(
            id = id,
            createdAt = createdAt,
            message = message
        )
    }
}
