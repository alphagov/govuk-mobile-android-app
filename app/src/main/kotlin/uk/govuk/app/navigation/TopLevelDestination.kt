package uk.govuk.app.navigation

import androidx.annotation.StringRes
import uk.govuk.app.R
import uk.govuk.app.home.ui.navigation.HOME_GRAPH_ROUTE
import uk.govuk.app.settings.ui.navigation.SETTINGS_GRAPH_ROUTE

sealed class TopLevelDestination(
    val route: String,
    @StringRes val resourceId: Int
) {
    data object Home : TopLevelDestination(HOME_GRAPH_ROUTE, R.string.home)
    data object Settings : TopLevelDestination(SETTINGS_GRAPH_ROUTE, R.string.settings)
}