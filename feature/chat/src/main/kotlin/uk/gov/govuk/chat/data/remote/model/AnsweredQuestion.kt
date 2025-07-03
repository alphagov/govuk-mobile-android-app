package uk.gov.govuk.chat.data.remote.model

import com.google.gson.annotations.SerializedName
import uk.gov.govuk.chat.ui.model.AnsweredQuestionUi

data class AnsweredQuestion(
    @SerializedName("id") val id: String,
    @SerializedName("answer") val answer: Answer,
    @SerializedName("conversation_id") val conversationId: String,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("message") val message: String
) {
    fun toAnsweredQuestionUi(): AnsweredQuestionUi {
        return AnsweredQuestionUi(
            id = id,
            answer = answer.toAnswerUi(),
            conversationId = conversationId,
            createdAt = createdAt,
            message = message
        )
    }
}
