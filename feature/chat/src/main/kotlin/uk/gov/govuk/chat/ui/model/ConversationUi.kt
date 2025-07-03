package uk.gov.govuk.chat.ui.model

data class ConversationUi(
    val id: String,
    val answeredQuestions: List<AnsweredQuestionUi>,
    val createdAt: String
)
