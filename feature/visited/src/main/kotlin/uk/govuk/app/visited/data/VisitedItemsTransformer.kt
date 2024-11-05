package uk.govuk.app.visited.data

import uk.govuk.app.visited.SectionTitles
import uk.govuk.app.visited.domain.model.VisitedItemUi
import uk.govuk.app.visited.ui.model.VisitedUi
import java.time.LocalDate

fun transformVisitedItems(visitedItems: List<VisitedItemUi>, todaysDate: LocalDate): Map<String, List<VisitedUi>> {
    fun VisitedItemUi.toLocalDate() = LocalDate.ofEpochDay(this.lastVisited)

    val todaysItems = visitedItems.filter { it.toLocalDate().isEqual(todaysDate) }
    val thisMonthsItems = visitedItems.filter {
        it.toLocalDate().month == todaysDate.month &&
            it.toLocalDate().year == todaysDate.year &&
            it.toLocalDate().dayOfMonth != todaysDate.dayOfMonth
    }
    val previousMonthsItems = visitedItems.filter {
        it.toLocalDate().month != todaysDate.month ||
            it.toLocalDate().year != todaysDate.year
    }

    fun VisitedItemUi.previousMonthKey() = "${toLocalDate().month.name} ${toLocalDate().year}"
    val groupedPreviousMonthsItems = previousMonthsItems.groupBy { it.previousMonthKey() }

    fun toVisitedUi(items: List<VisitedItemUi>): List<VisitedUi> = items.map {
        VisitedUi(
            title = it.title,
            url = it.url,
            lastVisited = localDateFormatter(it.lastVisited)
        )
    }

    val groupedVisitedItems = mutableMapOf<String, List<VisitedUi>>()

    if (todaysItems.isNotEmpty()) {
        groupedVisitedItems[SectionTitles().today] = toVisitedUi(todaysItems)
    }

    if (thisMonthsItems.isNotEmpty()) {
        groupedVisitedItems[SectionTitles().thisMonth] = toVisitedUi(thisMonthsItems)
    }

    groupedPreviousMonthsItems.forEach { (key, value) ->
        groupedVisitedItems[key] = toVisitedUi(value)
    }

    return groupedVisitedItems
}
