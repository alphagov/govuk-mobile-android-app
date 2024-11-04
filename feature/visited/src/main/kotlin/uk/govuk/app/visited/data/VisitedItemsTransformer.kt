package uk.govuk.app.visited.data

import uk.govuk.app.visited.SectionTitles
import uk.govuk.app.visited.data.model.VisitedItem
import uk.govuk.app.visited.domain.model.VisitedItemUi
import uk.govuk.app.visited.ui.model.VisitedUi
import java.time.LocalDate

class VisitedItemsTransformer(
    private val visitedItems: List<VisitedItemUi>,
    private val todaysDate: LocalDate
) {
    val todaysItems: List<VisitedItemUi>
    val thisMonthsItems: List<VisitedItemUi>
    val previousMonthsItems: List<VisitedItemUi>
    val groupedPreviousMonthsItems: Map<String, List<VisitedItemUi>>

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

    private fun todaysItems(): List<VisitedItemUi> {
        return visitedItems.filter {
            LocalDate.ofEpochDay(it.lastVisited).isEqual(todaysDate)
        }
    }

    private fun thisMonthsItems(): List<VisitedItemUi> {
        return visitedItems.filter {
            LocalDate.ofEpochDay(it.lastVisited).month == todaysDate.month
                && LocalDate.ofEpochDay(it.lastVisited).year == todaysDate.year
                && LocalDate.ofEpochDay(it.lastVisited).dayOfMonth != todaysDate.dayOfMonth
        }
    }

    private fun previousMonthsItems(): List<VisitedItemUi> {
        return visitedItems.filter {
            LocalDate.ofEpochDay(it.lastVisited).month != todaysDate.month
                || LocalDate.ofEpochDay(it.lastVisited).year != todaysDate.year
        }
    }

    private fun groupPreviousMonthsItems(): Map<String, List<VisitedItemUi>> {
        fun VisitedItemUi.key() =
            "${LocalDate.ofEpochDay(this.lastVisited).month.name} ${LocalDate.ofEpochDay(this.lastVisited).year}"

        return previousMonthsItems().groupBy { it.key() }
    }

    private fun toVisitedUi(visitedItems: List<VisitedItemUi>): List<VisitedUi> {
        return visitedItems.map { visitedItem ->
            VisitedUi(
                title = visitedItem.title,
                url = visitedItem.url,
                lastVisited = localDateFormatter(visitedItem.lastVisited)
            )
        }
    }
}
