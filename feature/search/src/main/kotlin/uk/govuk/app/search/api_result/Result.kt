package uk.govuk.app.search.api_result

data class Result(
    val _id: String,
    val description: String,
    val display_type: String,
    val document_type: String,
    val elasticsearch_type: String,
    val es_score: Double,
    val expanded_organisations: List<Organisation>,
    val format: String,
    val index: String,
    val link: String,
    val organisation_content_ids: List<String>,
    val organisations: List<Organisation>,
    val policy_areas: List<PolicyArea>,
    val public_timestamp: String,
    val title: String
)
