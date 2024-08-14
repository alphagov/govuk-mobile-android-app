package uk.govuk.app.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import uk.govuk.app.R
import uk.govuk.app.design.R.drawable.ic_home
import uk.govuk.app.design.R.drawable.ic_settings
import uk.govuk.app.home.navigation.HOME_GRAPH_ROUTE
import uk.govuk.app.settings.navigation.SETTINGS_GRAPH_ROUTE


sealed class TopLevelDestination(
    val route: String,
    @StringRes val resourceId: Int,
    @DrawableRes val icon: Int
) {
    data object Home : TopLevelDestination(HOME_GRAPH_ROUTE, R.string.home, ic_home)
    data object Settings : TopLevelDestination(SETTINGS_GRAPH_ROUTE, R.string.settings, ic_settings)
}