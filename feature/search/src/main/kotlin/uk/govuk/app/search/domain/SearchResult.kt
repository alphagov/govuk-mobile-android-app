package uk.govuk.app.search.domain

import uk.govuk.app.search.api_result.SearchResponse

data class SearchResult(
    val status: ResultStatus,
    val response: SearchResponse
)
