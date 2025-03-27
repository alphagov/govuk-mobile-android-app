package uk.gov.govuk.extension

import androidx.navigation.NavDeepLink
import androidx.navigation.navDeepLink

/**
 * Formats a deep link path into uri patterns for each scheme & host declared in the manifest.
 * Returns a collection of NavDeepLink objects for each formatted uri pattern.
 */
fun String.asDeepLinks(): List<NavDeepLink> {
    val path = this
    return listOf(
        navDeepLink {
            uriPattern = "govuk://app.gov.uk$path"
        }, navDeepLink {
            uriPattern = "govuk://gov.uk$path"
        }, navDeepLink {
            uriPattern = "https://app.gov.uk$path"
        }
    )
}
