package uk.govuk.app.visited.data

import uk.govuk.app.visited.SectionTitles
import uk.govuk.app.visited.data.model.VisitedItem
import uk.govuk.app.visited.ui.model.VisitedUi
import java.time.LocalDate

class VisitedItemsTransformer(
    private val visitedItems: List<VisitedItem>,
    private val todaysDate: LocalDate
) {
    val todaysItems: List<VisitedItem>
    val thisMonthsItems: List<VisitedItem>
    val previousMonthsItems: List<VisitedItem>
    val groupedPreviousMonthsItems: Map<String, List<VisitedItem>>

    init {
        todaysItems = todaysItems()
        thisMonthsItems = thisMonthsItems()
        previousMonthsItems = previousMonthsItems()
        groupedPreviousMonthsItems = groupPreviousMonthsItems()
    }

    fun transform(): Map<String, List<VisitedUi>> {
        val groupedVisitedItems = mutableMapOf<String, List<VisitedUi>>()

        if (todaysItems.isNotEmpty()) {
            groupedVisitedItems += mapOf(SectionTitles().today to toVisitedUi(todaysItems))
        }

        if (thisMonthsItems.isNotEmpty()) {
            groupedVisitedItems += mapOf(SectionTitles().thisMonth to toVisitedUi(thisMonthsItems))
        }

        groupedPreviousMonthsItems.forEach { (key, value) ->
            groupedVisitedItems += mapOf(key to toVisitedUi(value))
        }

        return groupedVisitedItems
    }

    private fun todaysItems(): List<VisitedItem> {
        return visitedItems.filter {
            LocalDate.ofEpochDay(it.lastVisited).isEqual(todaysDate)
        }
    }

    private fun thisMonthsItems(): List<VisitedItem> {
        return visitedItems.filter {
            LocalDate.ofEpochDay(it.lastVisited).month == todaysDate.month
                && LocalDate.ofEpochDay(it.lastVisited).year == todaysDate.year
                && LocalDate.ofEpochDay(it.lastVisited).dayOfMonth != todaysDate.dayOfMonth
        }
    }

    private fun previousMonthsItems(): List<VisitedItem> {
        return visitedItems.filter {
            LocalDate.ofEpochDay(it.lastVisited).month != todaysDate.month
                || LocalDate.ofEpochDay(it.lastVisited).year != todaysDate.year
        }
    }

    private fun groupPreviousMonthsItems(): Map<String, List<VisitedItem>> {
        fun VisitedItem.key() =
            "${LocalDate.ofEpochDay(this.lastVisited).month.name} ${LocalDate.ofEpochDay(this.lastVisited).year}"

        return previousMonthsItems().groupBy { it.key() }
    }

    private fun toVisitedUi(visitedItems: List<VisitedItem>): List<VisitedUi> {
        return visitedItems.map { visitedItem ->
            VisitedUi(
                title = visitedItem.title,
                url = visitedItem.url,
                lastVisited = localDateFormatter(visitedItem.lastVisited)
            )
        }
    }
}
