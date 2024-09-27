package uk.govuk.app.search.domain

import uk.govuk.app.search.data.remote.model.SearchResponse

data class SearchResult(
    val status: ResultStatus,
    val response: SearchResponse
)
