package uk.govuk.app.topics.extension

import uk.govuk.app.design.R
import uk.govuk.app.topics.domain.model.TopicItem
import uk.govuk.app.topics.ui.model.TopicUi

internal fun TopicItem.toTopicUi(): TopicUi {
    val icon = when (ref) {
        "benefits" -> R.drawable.ic_topic_benefits
        "business" -> R.drawable.ic_topic_business
        "care" -> R.drawable.ic_topic_care
        "driving-transport" -> R.drawable.ic_topic_transport
        "employment" -> R.drawable.ic_topic_employment
        "health-disability" -> R.drawable.ic_topic_health
        "money-tax" -> R.drawable.ic_topic_money
        "parenting-guardianship" -> R.drawable.ic_topic_parenting
        "retirement" -> R.drawable.ic_topic_retirement
        "studying-training" -> R.drawable.ic_topic_studying
        "travel" -> R.drawable.ic_topic_travel
        else -> R.drawable.ic_topic_default
    }

    return TopicUi(
        ref = ref,
        icon = icon,
        title = title,
        isSelected = isSelected
    )
}
