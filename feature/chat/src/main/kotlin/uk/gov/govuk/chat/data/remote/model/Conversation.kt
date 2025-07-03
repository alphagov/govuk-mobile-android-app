package uk.gov.govuk.chat.data.remote.model

import com.google.gson.annotations.SerializedName
import uk.gov.govuk.chat.ui.model.ConversationUi

data class Conversation(
    @SerializedName("id") val id: String,
    @SerializedName("answered_questions") val answeredQuestions: List<AnsweredQuestion>,
    @SerializedName("created_at") val createdAt: String
) {
    fun toConversationUi(): ConversationUi {
        return ConversationUi(
            id = id,
            answeredQuestions = answeredQuestions.map { it.toAnsweredQuestionUi() },
            createdAt = createdAt
        )
    }
}
