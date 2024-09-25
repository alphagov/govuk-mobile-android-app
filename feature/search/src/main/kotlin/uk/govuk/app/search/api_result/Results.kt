package uk.govuk.app.search.api_result

data class Results(
    val total: Int = 0,
    val results: List<Result> = emptyList()
)
