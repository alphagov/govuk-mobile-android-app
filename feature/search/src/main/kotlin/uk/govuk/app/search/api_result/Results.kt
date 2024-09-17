package uk.govuk.app.search.api_result

data class Results(
    val aggregates: Aggregates,
    val es_cluster: String,
    val results: List<Result>,
    val start: Int,
    val suggested_autocomplete: List<Any>,
    val suggested_queries: List<Any>,
    val total: Int
)
