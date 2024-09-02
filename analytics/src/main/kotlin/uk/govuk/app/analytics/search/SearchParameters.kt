package uk.govuk.app.analytics.search

import uk.gov.logging.api.analytics.logging.EVENT_NAME
import uk.gov.logging.api.analytics.logging.TEXT
import uk.gov.logging.api.analytics.parameters.Mapper

data class SearchParameters(
    private val searchTerm: String
) : Mapper {

    override fun asMap(): Map<out String, Any?> = mapOf(
        EVENT_NAME to "Search",
        TEXT to searchTerm
    )

}