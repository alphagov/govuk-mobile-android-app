package uk.govuk.app.topics.ui.model

internal data class TopicUi(
    val title: String,
    val description: String?,
    val subtopics: List<Subtopic>,
    val popularPages: List<TopicContent>,
    val stepBySteps: List<TopicContent>,
    val displayStepByStepSeeAll: Boolean
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
