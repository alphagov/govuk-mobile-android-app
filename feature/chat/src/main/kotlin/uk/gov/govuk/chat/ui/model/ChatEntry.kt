package uk.gov.govuk.chat.ui.model

data class ChatEntry(
    val question: String,
    var answer: String,
    var sources: List<String>?
)
