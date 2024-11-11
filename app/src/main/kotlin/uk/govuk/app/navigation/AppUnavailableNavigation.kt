package uk.govuk.app.navigation

import android.content.Intent
import android.net.Uri
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import uk.govuk.app.ui.AppUnavailableRoute

const val APP_UNAVAILABLE_GRAPH_ROUTE = "app_unavailable_graph_route"
private const val APP_UNAVAILABLE_ROUTE = "app_unavailable_route"

fun NavGraphBuilder.appUnavailableGraph(
    govUkUrl: String,
    modifier: Modifier = Modifier
) {
    navigation(
        route = APP_UNAVAILABLE_GRAPH_ROUTE,
        startDestination = APP_UNAVAILABLE_ROUTE
    ) {
        composable(APP_UNAVAILABLE_ROUTE) {
            val context = LocalContext.current

            AppUnavailableRoute(
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
