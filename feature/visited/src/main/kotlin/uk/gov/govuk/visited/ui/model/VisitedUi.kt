package uk.gov.govuk.visited.ui.model

data class VisitedUi(
    val title: String,
    val url: String,
    val lastVisited: String,
    var isSelected: Boolean = false
)
