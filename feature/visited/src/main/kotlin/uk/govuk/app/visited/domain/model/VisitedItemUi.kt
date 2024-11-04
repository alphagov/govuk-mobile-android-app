package uk.govuk.app.visited.domain.model

data class VisitedItemUi(
    val title: String,
    val url: String,
    val lastVisited: Long
)
