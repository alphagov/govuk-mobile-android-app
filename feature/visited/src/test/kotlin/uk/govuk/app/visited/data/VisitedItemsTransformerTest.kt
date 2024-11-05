package uk.govuk.app.visited.data

import org.junit.Assert.assertEquals
import org.junit.Test
import uk.govuk.app.visited.SectionTitles
import uk.govuk.app.visited.domain.model.VisitedItemUi
import uk.govuk.app.visited.ui.model.VisitedUi
import java.time.LocalDate

class VisitedItemsTransformerTest {
    @Test
    fun `Given one item for today, and one item for yesterday, then you only get one item when asking for today's items`() {
        val today = LocalDate.of(2023, 10, 2)
        val yesterday = today.minusDays(1)

        val visitedItems = listOf(
            VisitedItemUi(
                title = "GOV.UK",
                url = "https://www.gov.uk",
                lastVisited = today.toEpochDay()
            ),
            VisitedItemUi(
                title = "Google",
                url = "https://www.google.com",
                lastVisited = yesterday.toEpochDay()
            )
        )

        val items = transformVisitedItems(visitedItems, today)[SectionTitles().today]
        val actual = items?.first() ?: throw Exception("Failed to transform visited items")

        val expected = listOf(
            VisitedUi(
                title = "GOV.UK",
                url = "https://www.gov.uk",
                lastVisited = "02 October 2023"
            )
        )

        assertEquals(expected.first().title, actual.title)
        assertEquals(expected.first().url, actual.url)
        assertEquals(expected.first().lastVisited, actual.lastVisited)
    }

    @Test
    fun `Given one item for today, and one item for the rest of the month, then you only get one item when asking for this month's items`() {
        val today = LocalDate.of(2023, 10, 2)
        val yesterday = today.minusDays(1)

        val visitedItems = listOf(
            VisitedItemUi(
                title = "GOV.UK",
                url = "https://www.gov.uk",
                lastVisited = today.toEpochDay()
            ),
            VisitedItemUi(
                title = "Google",
                url = "https://www.google.com",
                lastVisited = yesterday.toEpochDay()
            )
        )

        val items = transformVisitedItems(visitedItems, today)[SectionTitles().thisMonth]
        val actual = items?.first() ?: throw Exception("Failed to transform visited items")

        val expected = listOf(
            VisitedUi(
                title = "Google",
                url = "https://www.google.com",
                lastVisited = "01 October 2023"
            )
        )

        assertEquals(expected.first().title, actual.title)
        assertEquals(expected.first().url, actual.url)
        assertEquals(expected.first().lastVisited, actual.lastVisited)
    }

    @Test
    fun `Given one item for today, and no items for the rest of the month, then you get no items when asking for this month's items`() {
        val today = LocalDate.of(2023, 10, 2)
        val lastMonth = today.minusDays(2)

        val visitedItems = listOf(
            VisitedItemUi(
                title = "GOV.UK",
                url = "https://www.gov.uk",
                lastVisited = today.toEpochDay()
            ),
            VisitedItemUi(
                title = "Google",
                url = "https://www.google.com",
                lastVisited = lastMonth.toEpochDay()
            )
        )

        val items = transformVisitedItems(visitedItems, today)[SectionTitles().thisMonth]

        assertEquals(null, items?.size)
    }

    @Test
    fun `Given one item for today, and no items for the rest of the month, then you get one item when asking for previous month's items`() {
        val today = LocalDate.of(2023, 10, 2)
        val lastMonth = today.minusDays(2)

        val visitedItems = listOf(
            VisitedItemUi(
                title = "GOV.UK",
                url = "https://www.gov.uk",
                lastVisited = today.toEpochDay()
            ),
            VisitedItemUi(
                title = "Google",
                url = "https://www.google.com",
                lastVisited = lastMonth.toEpochDay()
            )
        )

        val items = transformVisitedItems(visitedItems, today)["${lastMonth.month.name} ${lastMonth.year}"]
        val actual = items?.first() ?: throw Exception("Failed to transform visited items")

        val expected = listOf(
            VisitedUi(
                title = "Google",
                url = "https://www.google.com",
                lastVisited = "30 September 2023"
            )
        )

        assertEquals(expected.first().title, actual.title)
        assertEquals(expected.first().url, actual.url)
        assertEquals(expected.first().lastVisited, actual.lastVisited)
    }

    @Test
    fun `Given one item for today, and no items for the rest of the month, then you get one item keyed by month and year when asking for grouped previous month's items`() {
        val today = LocalDate.of(2023, 10, 2)
        val lastMonth = today.minusMonths(1)

        val visitedItems = listOf(
            VisitedItemUi(
                title = "GOV.UK",
                url = "https://www.gov.uk",
                lastVisited = today.toEpochDay()
            ),
            VisitedItemUi(
                title = "Google",
                url = "https://www.google.com",
                lastVisited = lastMonth.toEpochDay()
            )
        )

        val items = transformVisitedItems(visitedItems, today)["${lastMonth.month.name} ${lastMonth.year}"]
        val actual = items?.first() ?: throw Exception("Failed to transform visited items")

        val expected = listOf(
            VisitedUi(
                title = "Google",
                url = "https://www.google.com",
                lastVisited = "02 September 2023"
            )
        )

        assertEquals(expected.first().title, actual.title)
        assertEquals(expected.first().url, actual.url)
        assertEquals(expected.first().lastVisited, actual.lastVisited)
    }
}
