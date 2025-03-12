package uk.gov.govuk.topics.extension

import uk.gov.govuk.topics.R
import uk.gov.govuk.topics.data.remote.model.RemoteTopic
import uk.gov.govuk.topics.data.remote.model.RemoteTopic.RemoteTopicContent
import uk.gov.govuk.topics.ui.model.TopicUi
import uk.gov.govuk.topics.ui.model.TopicUi.Subtopic
import uk.gov.govuk.topics.ui.model.TopicUi.TopicContent
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
            TopicUi.Section(
                title = R.string.relatedTitle,
                icon = R.drawable.ic_topic_related
            )
        } else {
            TopicUi.Section(
                title = R.string.browseTitle,
                icon = R.drawable.ic_topic_browse
            )
        }
    )
}

internal fun RemoteTopicContent.toTopicContent(): TopicContent {
    return TopicContent(
        url = this.url,
        title = this.title
    )
}
