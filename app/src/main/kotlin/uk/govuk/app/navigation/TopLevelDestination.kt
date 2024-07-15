package uk.govuk.app.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import uk.govuk.app.R
import uk.govuk.app.home.ui.navigation.HOME_GRAPH_ROUTE
import uk.govuk.app.settings.ui.navigation.SETTINGS_GRAPH_ROUTE

sealed class TopLevelDestination(
    val route: String,
    @StringRes val resourceId: Int,
    val icon: ImageVector
) {
    data object Home : TopLevelDestination(HOME_GRAPH_ROUTE, R.string.home, Icons.Default.Home)
    data object Settings : TopLevelDestination(SETTINGS_GRAPH_ROUTE, R.string.settings, Icons.Default.Settings)
}