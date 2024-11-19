package uk.govuk.app.topics

import uk.govuk.app.topics.ui.model.TopicUi

internal sealed class TopicUiState(
    val topicUi: TopicUi? = null,
    val topicReference: String = ""
) {
    internal class Default(topicUi: TopicUi?) : TopicUiState(topicUi = topicUi)

    internal class Offline(topicReference: String) : TopicUiState(topicReference = topicReference)

    internal class ServiceError(topicReference: String) : TopicUiState(topicReference = topicReference)
}
