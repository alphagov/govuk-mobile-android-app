package uk.gov.govuk.chat.ui.model

data class AnswerUi(
    val id: String,
    val createdAt: String,
    val message: String,
    val sources: List<SourceUi>
)
