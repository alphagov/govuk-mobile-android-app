package uk.govuk.app.local.ui

data class LocalAuthorityUi(
    val name: String,
    val url: String,
    val slug: String,
    val parent: LocalAuthorityUi? = null
)
