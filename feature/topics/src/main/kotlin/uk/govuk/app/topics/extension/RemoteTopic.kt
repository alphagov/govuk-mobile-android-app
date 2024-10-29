package uk.govuk.app.topics.extension

import uk.govuk.app.topics.data.remote.model.RemoteTopic
import uk.govuk.app.topics.data.remote.model.RemoteTopic.RemoteTopicContent
import uk.govuk.app.topics.ui.model.TopicUi
import uk.govuk.app.topics.ui.model.TopicUi.Subtopic
import uk.govuk.app.topics.ui.model.TopicUi.TopicContent
import kotlin.math.min

internal fun RemoteTopic.toTopicUi(maxStepBySteps: Int): TopicUi {
    val stepBySteps = content.filter { it.isStepByStep }

    return TopicUi(
        title = title,
        description = description,
        subtopics = subtopics.map {
            Subtopic(
                ref = it.ref,
                title = it.title
            )
        },
        popularPages = content
            .filter { it.isPopular }
            .map { it.toTopicContent() },
        stepBySteps = stepBySteps
            .subList(0, min(stepBySteps.size, maxStepBySteps))
            .map { it.toTopicContent()},
        services = content
            .filter { !it.isPopular && !it.isStepByStep }
            .map { it.toTopicContent() },
        displayStepByStepSeeAll = stepBySteps.size > maxStepBySteps
    )
}

private fun RemoteTopicContent.toTopicContent(): TopicContent {
    return TopicContent(
        url = this.url,
        title = this.title
    )
}
