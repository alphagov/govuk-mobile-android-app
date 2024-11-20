package uk.govuk.app.topics

import uk.govuk.app.topics.ui.model.TopicUi

internal sealed class TopicUiState {
    internal class Default(val topicUi: TopicUi) : TopicUiState()

    internal class Offline(val topicReference: String) : TopicUiState()

    internal data object ServiceError : TopicUiState()
}
