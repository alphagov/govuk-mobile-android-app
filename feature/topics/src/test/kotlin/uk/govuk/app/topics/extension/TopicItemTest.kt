package uk.govuk.app.topics.extension

import org.junit.Assert.assertEquals
import org.junit.Test
import uk.govuk.app.design.R
import uk.govuk.app.topics.TopicUi
import uk.govuk.app.topics.data.remote.model.TopicItem

class TopicItemTest{

    @Test
    fun `Given a benefits topic item, When mapping to topic ui, then return a topic ui with correct icon`() {
        val expected = TopicUi(
            ref = "benefits",
            title = "Title",
            icon = R.drawable.ic_topic_benefits
        )

        val actual =
            TopicItem(
                ref = "benefits",
                title = "Title"
            ).toTopicUi()

        assertEquals(actual, expected)
    }

    @Test
    fun `Given a business topic item, When mapping to topic ui, then return a a topic ui with correct icon`() {
        val expected = TopicUi(
            ref = "business",
            title = "Title",
            icon = R.drawable.ic_topic_business
        )

        val actual =
            TopicItem(
                ref = "business",
                title = "Title"
            ).toTopicUi()

        assertEquals(actual, expected)
    }

    @Test
    fun `Given a care topic item, When mapping to topic ui, then return a a topic ui with correct icon`() {
        val expected = TopicUi(
            ref = "care",
            title = "Title",
            icon = R.drawable.ic_topic_care
        )

        val actual =
            TopicItem(
                ref = "care",
                title = "Title"
            ).toTopicUi()

        assertEquals(actual, expected)
    }

    @Test
    fun `Given a transport topic item, When mapping to topic ui, then return a a topic ui with correct icon`() {
        val expected = TopicUi(
            ref = "driving-transport",
            title = "Title",
            icon = R.drawable.ic_topic_transport
        )

        val actual =
            TopicItem(
                ref = "driving-transport",
                title = "Title"
            ).toTopicUi()

        assertEquals(actual, expected)
    }

    @Test
    fun `Given an employment topic item, When mapping to topic ui, then return a a topic ui with correct icon`() {
        val expected = TopicUi(
            ref = "employment",
            title = "Title",
            icon = R.drawable.ic_topic_employment
        )

        val actual =
            TopicItem(
                ref = "employment",
                title = "Title"
            ).toTopicUi()

        assertEquals(actual, expected)
    }

    @Test
    fun `Given a health topic item, When mapping to topic ui, then return a a topic ui with correct icon`() {
        val expected = TopicUi(
            ref = "health-disability",
            title = "Title",
            icon = R.drawable.ic_topic_health
        )

        val actual =
            TopicItem(
                ref = "health-disability",
                title = "Title"
            ).toTopicUi()

        assertEquals(actual, expected)
    }

    @Test
    fun `Given a money topic item, When mapping to topic ui, then return a a topic ui with correct icon`() {
        val expected = TopicUi(
            ref = "money-tax",
            title = "Title",
            icon = R.drawable.ic_topic_money
        )

        val actual =
            TopicItem(
                ref = "money-tax",
                title = "Title"
            ).toTopicUi()

        assertEquals(actual, expected)
    }

    @Test
    fun `Given a parenting topic item, When mapping to topic ui, then return a a topic ui with correct icon`() {
        val expected = TopicUi(
            ref = "parenting-guardianship",
            title = "Title",
            icon = R.drawable.ic_topic_parenting
        )

        val actual =
            TopicItem(
                ref = "parenting-guardianship",
                title = "Title"
            ).toTopicUi()

        assertEquals(actual, expected)
    }

    @Test
    fun `Given a retirement topic item, When mapping to topic ui, then return a a topic ui with correct icon`() {
        val expected = TopicUi(
            ref = "retirement",
            title = "Title",
            icon = R.drawable.ic_topic_retirement
        )

        val actual =
            TopicItem(
                ref = "retirement",
                title = "Title"
            ).toTopicUi()

        assertEquals(actual, expected)
    }

    @Test
    fun `Given a studying topic item, When mapping to topic ui, then return a a topic ui with correct icon`() {
        val expected = TopicUi(
            ref = "studying-training",
            title = "Title",
            icon = R.drawable.ic_topic_studying
        )

        val actual =
            TopicItem(
                ref = "studying-training",
                title = "Title"
            ).toTopicUi()

        assertEquals(actual, expected)
    }

    @Test
    fun `Given a travel topic item, When mapping to topic ui, then return a a topic ui with correct icon`() {
        val expected = TopicUi(
            ref = "travel",
            title = "Title",
            icon = R.drawable.ic_topic_travel
        )

        val actual =
            TopicItem(
                ref = "travel",
                title = "Title"
            ).toTopicUi()

        assertEquals(actual, expected)
    }

    @Test
    fun `Given an unknown topic item, When mapping to topic ui, then return a a topic ui with correct icon`() {
        val expected = TopicUi(
            ref = "unknown",
            title = "Title",
            icon = R.drawable.ic_topic_default
        )

        val actual =
            TopicItem(
                ref = "unknown",
                title = "Title"
            ).toTopicUi()

        assertEquals(actual, expected)
    }
}