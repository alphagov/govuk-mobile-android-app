package uk.govuk.app.search.domain

import uk.govuk.app.search.api_result.Results

data class SearchResult(
    val status: ResultStatus, val
    results: Results
)
