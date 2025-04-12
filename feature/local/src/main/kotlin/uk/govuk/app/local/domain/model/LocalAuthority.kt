package uk.govuk.app.local.domain.model

data class LocalAuthority(
    val name: String,
    val url: String,
    val slug: String,
    var parentName: String?,
    var parentUrl: String?,
    var parentSlug: String?
)
