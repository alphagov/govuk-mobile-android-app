package uk.gov.govuk.chat.ui.model

data class AnsweredQuestionUi(
    val id: String,
    val answer: AnswerUi,
    val conversationId: String,
    val createdAt: String,
    val message: String
)
