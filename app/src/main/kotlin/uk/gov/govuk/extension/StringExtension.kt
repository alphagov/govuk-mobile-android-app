package uk.gov.govuk.extension

import androidx.navigation.NavDeepLink
import androidx.navigation.navDeepLink
import uk.gov.govuk.navigation.DeepLink

/**
 * Formats a deep link path into uri patterns for each allowed url
 *
 * Returns a collection of NavDeepLink objects for each formatted uri pattern.
 */
internal fun String.asDeepLinks(allowedUrls: List<String>): List<NavDeepLink> {
    val path = this
    val navDeepLinks = mutableListOf<NavDeepLink>()
    allowedUrls.forEach {
        navDeepLinks.add(navDeepLink {
            uriPattern = "$it$path"
        })
    }
    return navDeepLinks
}
