package uk.govuk.app.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import uk.govuk.app.R
import uk.govuk.app.design.R.drawable.ic_home
import uk.govuk.app.design.R.drawable.ic_settings
import uk.govuk.app.home.navigation.HOME_GRAPH_ROUTE
import uk.govuk.app.settings.navigation.SETTINGS_GRAPH_ROUTE
import uk.govuk.app.topics.navigation.TOPICS_ALL_ROUTE
import uk.govuk.app.topics.navigation.TOPICS_ALL_STEP_BY_STEPS_ROUTE
import uk.govuk.app.topics.navigation.TOPIC_ROUTE


internal sealed class TopLevelDestination(
    val route: String,
    @StringRes val stringResId: Int,
    @DrawableRes val icon: Int,
    val associatedRoutes: List<String> = emptyList()
) {
    internal data object Home : TopLevelDestination(
        route =  HOME_GRAPH_ROUTE,
        stringResId = R.string.home,
        icon = ic_home,
        associatedRoutes = listOf(
            TOPICS_ALL_ROUTE,
            TOPIC_ROUTE,
            TOPICS_ALL_STEP_BY_STEPS_ROUTE
        )
    )

    internal data object Settings : TopLevelDestination(
        route = SETTINGS_GRAPH_ROUTE,
        stringResId = R.string.settings,
        icon = ic_settings
    )
}