package uk.govuk.app.topics.ui.model

import androidx.annotation.StringRes

internal data class TopicUi(
    val title: String,
    val description: String?,
    val popularPages: List<TopicContent>,
    val stepBySteps: List<TopicContent>,
    val displayStepByStepSeeAll: Boolean,
    val services: List<TopicContent>,
    val subtopics: List<Subtopic>,
    @StringRes val subtopicsTitle: Int,
) {
    data class Subtopic(
        val ref: String,
        val title: String
    )

    data class TopicContent(
        val url: String,
        val title: String
    )
}
