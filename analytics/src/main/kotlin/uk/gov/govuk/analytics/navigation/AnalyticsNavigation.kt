package uk.gov.govuk.analytics.navigation

import android.content.Intent
import android.net.Uri
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import uk.gov.govuk.analytics.ui.AnalyticsConsentRoute

const val ANALYTICS_GRAPH_ROUTE = "analytics_graph_route"
private const val ANALYTICS_CONSENT_ROUTE = "analytics_consent_route"

fun NavGraphBuilder.analyticsGraph(
    privacyPolicyUrl: String,
    analyticsConsentCompleted: () -> Unit,
    modifier: Modifier = Modifier
) {
    navigation(
        route = ANALYTICS_GRAPH_ROUTE,
        startDestination = ANALYTICS_CONSENT_ROUTE
    ) {
        composable(ANALYTICS_CONSENT_ROUTE) {
            val context = LocalContext.current

            AnalyticsConsentRoute(
                onPrivacyPolicyClick = {
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse(privacyPolicyUrl)
                    context.startActivity(intent)
                },
                analyticsConsentCompleted = analyticsConsentCompleted,
                modifier = modifier
            )
        }
    }
}
