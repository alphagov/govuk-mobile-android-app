package uk.govuk.app.visited

interface Visited {
    suspend fun visitableItemClick(title: String, url: String)
}
