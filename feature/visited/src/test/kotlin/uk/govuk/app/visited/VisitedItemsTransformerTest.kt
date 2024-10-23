package uk.govuk.app.visited

import org.junit.Assert.assertEquals
import org.junit.Test
import uk.govuk.app.visited.data.VisitedItemsTransformer
import uk.govuk.app.visited.data.model.VisitedItem
import java.time.LocalDate

class VisitedItemsTransformerTest {
    @Test
    fun `Given one item for today, and one item for yesterday, then you only get one item when asking for today's items`() {
        val today = LocalDate.of(2023, 10, 2)
        val yesterday = today.minusDays(1)

        val visitedItems = listOf(
            VisitedItem().apply {
                title = "GOV.UK"
                url = "https://www.gov.uk"
                lastVisited = today.toEpochDay()
            },
            VisitedItem().apply {
                title = "Google"
                url = "https://www.google.com"
                lastVisited = yesterday.toEpochDay()
            }
        )

        val expected = listOf(
            VisitedItem().apply {
                title = "GOV.UK"
                url = "https://www.gov.uk"
                lastVisited = today.toEpochDay()
            }
        )

        val actual = VisitedItemsTransformer(visitedItems, today).todaysItems

        assertEquals(expected.size, actual.size)
        assertEquals(expected.first().url, actual.first().url)
        assertEquals(expected.first().lastVisited, actual.first().lastVisited)
    }

    @Test
    fun `Given one item for today, and one item for the rest of the month, then you only get one item when asking for this month's items`() {
        val today = LocalDate.of(2023, 10, 2)
        val yesterday = today.minusDays(1)

        val visitedItems = listOf(
            VisitedItem().apply {
                title = "GOV.UK"
                url = "https://www.gov.uk"
                lastVisited = today.toEpochDay()
            },
            VisitedItem().apply {
                title = "Google"
                url = "https://www.google.com"
                lastVisited = yesterday.toEpochDay()
            }
        )

        val expected = listOf(
            VisitedItem().apply {
                title = "Google"
                url = "https://www.google.com"
                lastVisited = yesterday.toEpochDay()
            }
        )

        val actual = VisitedItemsTransformer(visitedItems, today).thisMonthsItems

        assertEquals(expected.size, actual.size)
        assertEquals(expected.first().url, actual.first().url)
        assertEquals(expected.first().lastVisited, actual.first().lastVisited)
    }

    @Test
    fun `Given one item for today, and no items for the rest of the month, then you get no items when asking for this month's items`() {
        val today = LocalDate.of(2023, 10, 2)
        val lastMonth = today.minusDays(2)

        val visitedItems = listOf(
            VisitedItem().apply {
                title = "GOV.UK"
                url = "https://www.gov.uk"
                lastVisited = today.toEpochDay()
            },
            VisitedItem().apply {
                title = "Google"
                url = "https://www.google.com"
                lastVisited = lastMonth.toEpochDay()
            }
        )

        val actual = VisitedItemsTransformer(visitedItems, today).thisMonthsItems

        assertEquals(0, actual.size)
    }

    @Test
    fun `Given one item for today, and no items for the rest of the month, then you get one item when asking for previous month's items`() {
        val today = LocalDate.of(2023, 10, 2)
        val lastMonth = today.minusDays(2)

        val visitedItems = listOf(
            VisitedItem().apply {
                title = "GOV.UK"
                url = "https://www.gov.uk"
                lastVisited = today.toEpochDay()
            },
            VisitedItem().apply {
                title = "Google"
                url = "https://www.google.com"
                lastVisited = lastMonth.toEpochDay()
            }
        )

        val expected = listOf(
            VisitedItem().apply {
                title = "Google"
                url = "https://www.google.com"
                lastVisited = lastMonth.toEpochDay()
            }
        )

        val actual = VisitedItemsTransformer(visitedItems, today).previousMonthsItems

        assertEquals(expected.size, actual.size)
        assertEquals(expected.first().url, actual.first().url)
        assertEquals(expected.first().lastVisited, actual.first().lastVisited)
    }

    @Test
    fun `Given one item for today, and no items for the rest of the month, then you get one item keyed by month and year when asking for grouped previous month's items`() {
        val today = LocalDate.of(2023, 10, 2)
        val lastMonth = today.minusDays(2)

        val visitedItems = listOf(
            VisitedItem().apply {
                title = "GOV.UK"
                url = "https://www.gov.uk"
                lastVisited = today.toEpochDay()
            },
            VisitedItem().apply {
                title = "Google"
                url = "https://www.google.com"
                lastVisited = lastMonth.toEpochDay()
            }
        )

        val expected = mapOf(
            "${today.minusMonths(1).month.name} ${today.year}" to listOf(VisitedItem().apply {
                    title = "Google"
                    url = "https://www.google.com"
                    lastVisited = lastMonth.toEpochDay()
                }
            )
        )

        val actual = VisitedItemsTransformer(visitedItems, today).groupedPreviousMonthsItems

        assertEquals(expected.values.size, actual.values.size)
        assertEquals(expected.keys.first(), actual.keys.first())
    }
}
