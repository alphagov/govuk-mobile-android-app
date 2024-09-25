package uk.govuk.app.search.api_result

data class Results(
    val aggregates: Aggregates = Aggregates(),
    val es_cluster: String = "",
    val results: List<Result> = emptyList(),
    val start: Int = 0,
    val suggested_autocomplete: List<Any> = emptyList(),
    val suggested_queries: List<Any> = emptyList(),
    val total: Int = 0
)
