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
        subtopicsSection =
        if (isSubtopic && content.isNotEmpty()) {
            TopicUi.SubTopicsSection(
                title = R.string.relatedTitle,
                icon = R.drawable.ic_topic_related
            )
        } else {
            TopicUi.SubTopicsSection(
                title = R.string.browseTitle,
                icon = R.drawable.ic_topic_browse
            )
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
