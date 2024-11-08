package uk.govuk.app.availability.navigation

import android.content.Intent
import android.net.Uri
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import androidx.navigation.navigation
import uk.govuk.app.availability.ui.UnavailableRoute

const val UNAVAILABLE_GRAPH_ROUTE = "unavailable_graph_route"
private const val UNAVAILABLE_ROUTE = "unavailable_route"
const val UNAVAILABLE_GRAPH_START_DESTINATION = UNAVAILABLE_ROUTE

fun NavGraphBuilder.unavailableGraph(
    govUkUrl: String,
    modifier: Modifier = Modifier
) {
    navigation(
        route = UNAVAILABLE_GRAPH_ROUTE,
        startDestination = UNAVAILABLE_GRAPH_START_DESTINATION
    ) {
        composable(
            UNAVAILABLE_ROUTE,
            deepLinks = listOf(
                navDeepLink {
                    uriPattern = "/unavailable"
                    action = Intent.ACTION_VIEW
                }
            )
        ) {
            val context = LocalContext.current
            UnavailableRoute(
                onGoToGovUkClick = {
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse(govUkUrl)
                    context.startActivity(intent)
                },
                modifier = modifier
            )
        }
    }
}
