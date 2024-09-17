package uk.govuk.app.search.api_result

data class Results(
    val aggregates: Aggregates,
    val es_cluster: String,
    val results: List<Result> = emptyList(),
    val start: Int,
    val suggested_autocomplete: List<Any> = emptyList(),
    val suggested_queries: List<Any> = emptyList(),
    val total: Int
)
