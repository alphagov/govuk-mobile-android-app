package uk.govuk.app.topics.extension

import org.junit.Assert.assertEquals
import org.junit.Test
import uk.govuk.app.design.R
import uk.govuk.app.topics.TopicUi
import uk.govuk.app.topics.data.remote.model.TopicItem

class TopicItemTest{

    @Test
    fun `Given topic items, When mapping to topic ui, then return a topic ui with correct icon`() {
        val topicItems = listOf(
            Pair(R.drawable.ic_topic_benefits, TopicItem("benefits", "Title")),
            Pair(R.drawable.ic_topic_business, TopicItem("business", "Title")),
            Pair(R.drawable.ic_topic_care, TopicItem("care", "Title")),
            Pair(R.drawable.ic_topic_transport, TopicItem("driving-transport", "Title")),
            Pair(R.drawable.ic_topic_employment, TopicItem("employment", "Title")),
            Pair(R.drawable.ic_topic_health, TopicItem("health-disability", "Title")),
            Pair(R.drawable.ic_topic_money, TopicItem("money-tax", "Title")),
            Pair(R.drawable.ic_topic_parenting, TopicItem("parenting-guardianship", "Title")),
            Pair(R.drawable.ic_topic_retirement, TopicItem("retirement", "Title")),
            Pair(R.drawable.ic_topic_studying, TopicItem("studying-training", "Title")),
            Pair(R.drawable.ic_topic_travel, TopicItem("travel", "Title")),
            Pair(R.drawable.ic_topic_default, TopicItem("unknown", "Title")),
        )

        for (topicItem in topicItems) {
            val expected = TopicUi(
                ref = topicItem.second.ref,
                icon = topicItem.first,
                title = topicItem.second.title
            )

            assertEquals(topicItem.second.toTopicUi(), expected)
        }
    }
}