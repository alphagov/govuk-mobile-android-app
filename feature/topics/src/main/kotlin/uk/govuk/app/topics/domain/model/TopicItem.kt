package uk.govuk.app.topics.domain.model

internal data class TopicItem(
    val ref: String,
    val title: String,
    val description: String,
    val isSelected: Boolean
)