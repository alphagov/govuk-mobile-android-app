package uk.govuk.app.topics.extension

import uk.govuk.app.topics.R
import uk.govuk.app.topics.data.remote.model.RemoteTopic
import uk.govuk.app.topics.data.remote.model.RemoteTopic.RemoteTopicContent
import uk.govuk.app.topics.ui.model.TopicUi
import uk.govuk.app.topics.ui.model.TopicUi.Subtopic
import uk.govuk.app.topics.ui.model.TopicUi.TopicContent
import kotlin.math.min

internal fun RemoteTopic.toTopicUi(maxStepBySteps: Int, isSubtopic: Boolean): TopicUi {
    val stepBySteps = content.filter { it.isStepByStep }

    return TopicUi(
        title = title,
        description = description,
        popularPages = content
            .filter { it.isPopular }
            .map { it.toTopicContent() },
        stepBySteps = stepBySteps
            .subList(0, min(stepBySteps.size, maxStepBySteps))
            .map { it.toTopicContent()},
        displayStepByStepSeeAll = stepBySteps.size > maxStepBySteps,
        services = content
            .filter { !it.isPopular && !it.isStepByStep }
            .map { it.toTopicContent() },
        subtopics = subtopics.map {
            Subtopic(
                ref = it.ref,
                title = it.title
            )
        },
        subtopicsTitle =
            if (isSubtopic && content.isNotEmpty()) {
                R.string.relatedTitle
            } else {
                R.string.browseTitle
            }
    )
}

internal fun RemoteTopic.toAllStepBySteps(): List<TopicContent> {
    return content.filter { it.isStepByStep }.map { it.toTopicContent() }
}

private fun RemoteTopicContent.toTopicContent(): TopicContent {
    return TopicContent(
        url = this.url,
        title = this.title
    )
}
