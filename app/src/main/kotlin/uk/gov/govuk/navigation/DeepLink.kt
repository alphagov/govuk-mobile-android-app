package uk.gov.govuk.navigation

internal object DeepLink {
    /**
     * Allowed app urls that are declared in the manifest
     */
    val allowedAppUrls = listOf<String>("govuk://app.gov.uk", "govuk://gov.uk", "https://app.gov.uk")

    /**
     * Allowed gov.uk urls
     */
    val allowedGovUkUrls = listOf<String>("https://gov.uk", "https://www.gov.uk")
}
