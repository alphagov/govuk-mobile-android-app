package uk.govuk.app.topics.extension

import org.junit.Assert.assertEquals
import org.junit.Test
import uk.govuk.app.design.R
import uk.govuk.app.topics.TopicUi
import uk.govuk.app.topics.domain.model.TopicItem

class TopicItemTest{

    @Test
    fun `Given topic items, When mapping to topic ui, then return a topic ui with correct icon`() {
        val topicItems = listOf(
            Pair(R.drawable.ic_topic_benefits, TopicItem("benefits", "Title", true)),
            Pair(R.drawable.ic_topic_business, TopicItem("business", "Title", false)),
            Pair(R.drawable.ic_topic_care, TopicItem("care", "Title", true)),
            Pair(R.drawable.ic_topic_transport, TopicItem("driving-transport", "Title", false)),
            Pair(R.drawable.ic_topic_employment, TopicItem("employment", "Title", true)),
            Pair(R.drawable.ic_topic_health, TopicItem("health-disability", "Title", false)),
            Pair(R.drawable.ic_topic_money, TopicItem("money-tax", "Title", true)),
            Pair(R.drawable.ic_topic_parenting, TopicItem("parenting-guardianship", "Title", false)),
            Pair(R.drawable.ic_topic_retirement, TopicItem("retirement", "Title", true)),
            Pair(R.drawable.ic_topic_studying, TopicItem("studying-training", "Title", false)),
            Pair(R.drawable.ic_topic_travel, TopicItem("travel", "Title", true)),
            Pair(R.drawable.ic_topic_default, TopicItem("unknown", "Title", false)),
        )

        for (topicItem in topicItems) {
            val expected = TopicUi(
                ref = topicItem.second.ref,
                icon = topicItem.first,
                title = topicItem.second.title,
                isSelected = topicItem.second.isSelected
            )

            assertEquals(topicItem.second.toTopicUi(), expected)
        }
    }
}