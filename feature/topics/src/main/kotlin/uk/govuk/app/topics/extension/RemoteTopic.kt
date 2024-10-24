package uk.govuk.app.topics.extension

import uk.govuk.app.topics.data.remote.model.RemoteTopic
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
            .map {
                TopicContent(
                    url = it.url,
                    title = it.title
                )
            },
        stepBySteps = stepBySteps
            .subList(0, min(stepBySteps.size, maxStepBySteps))
            .map {
                TopicContent(
                    url = it.url,
                    title = it.title
                )
            },
        displayStepByStepSeeAll = stepBySteps.size > maxStepBySteps
    )
}
