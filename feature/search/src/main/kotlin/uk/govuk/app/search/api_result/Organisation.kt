package uk.govuk.app.search.api_result

data class Organisation(
    val acronym: String = "",
    val analytics_identifier: String = "",
    val child_organisations: List<String> = emptyList(),
    val content_id: String = "",
    val link: String = "",
    val logo_formatted_title: String = "",
    val organisation_brand: String = "",
    val organisation_crest: String = "",
    val organisation_state: String = "",
    val organisation_type: String = "",
    val parent_organisations: List<String> = emptyList(),
    val public_timestamp: String = "",
    val slug: String = "",
    val superseded_organisations: List<String> = emptyList(),
    val title: String = ""
)
