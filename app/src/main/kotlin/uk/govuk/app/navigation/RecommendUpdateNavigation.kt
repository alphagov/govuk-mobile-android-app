package uk.govuk.app.navigation

import android.content.Intent
import android.net.Uri
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import uk.govuk.app.ui.RecommendUpdateRoute

const val RECOMMEND_UPDATE_GRAPH_ROUTE = "recommend_update_graph_route"
private const val RECOMMEND_UPDATE_ROUTE = "recommend_update_route"

fun NavGraphBuilder.recommendUpdateGraph(
    appStoreUrl: String,
    recommendUpdateSkipped: () -> Unit,
    modifier: Modifier = Modifier
) {
    navigation(
        route = RECOMMEND_UPDATE_GRAPH_ROUTE,
        startDestination = RECOMMEND_UPDATE_ROUTE
    ) {
        composable(RECOMMEND_UPDATE_ROUTE) {
            val context = LocalContext.current
            RecommendUpdateRoute(
                onUpdateClick = {
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse(appStoreUrl)
                    context.startActivity(intent)
                },
                recommendUpdateSkipped = recommendUpdateSkipped,
                modifier = modifier
            )
        }
    }
}
