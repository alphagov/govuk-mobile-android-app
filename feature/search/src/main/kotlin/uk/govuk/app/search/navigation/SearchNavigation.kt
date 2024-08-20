package uk.govuk.app.search.navigation

import android.content.Intent
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import androidx.navigation.navigation
import uk.govuk.app.search.ui.SearchRoute

const val SEARCH_GRAPH_ROUTE = "search_graph_route"
private const val SEARCH_ROUTE = "search_route"

private const val SEARCH_URL = "https://www.gov.uk/search/all?keywords=%s&order=relevance"
private const val BLANK_SEARCH_URL = "https://www.gov.uk/search?q="

fun NavGraphBuilder.searchGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    navigation(
        route = SEARCH_GRAPH_ROUTE,
        startDestination = SEARCH_ROUTE
    ) {
        composable(
            SEARCH_ROUTE,
            deepLinks = listOf(
                navDeepLink {
                    uriPattern = "/search"
                    action = Intent.ACTION_VIEW
                }
            )
        ) {
            val context = LocalContext.current

            SearchRoute(
                onBack = { navController.popBackStack() },
                onSearch = { searchQuery ->
                    val intent = CustomTabsIntent.Builder().build()
                    val searchUrl = if (searchQuery.isBlank()) {
                        BLANK_SEARCH_URL
                    } else {
                        String.format(SEARCH_URL, searchQuery)
                    }
                    intent.launchUrl(context, Uri.parse(searchUrl))
                },
                modifier = modifier
            )
        }
    }
}