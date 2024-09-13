package uk.govuk.app.analytics.navigation

import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import uk.govuk.app.analytics.ui.AnalyticsConsentRoute

const val ANALYTICS_GRAPH_ROUTE = "analytics_graph_route"
private const val ANALYTICS_CONSENT_ROUTE = "analytics_consent_route"

fun NavGraphBuilder.analyticsGraph(
    modifier: Modifier = Modifier
) {
    navigation(
        route = ANALYTICS_GRAPH_ROUTE,
        startDestination = ANALYTICS_CONSENT_ROUTE
    ) {
        composable(ANALYTICS_CONSENT_ROUTE) {
            AnalyticsConsentRoute(modifier)
        }
    }
}
