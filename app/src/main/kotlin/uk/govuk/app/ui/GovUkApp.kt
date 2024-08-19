package uk.govuk.app.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import uk.govuk.app.design.ui.theme.GovUkTheme
import uk.govuk.app.home.navigation.HOME_GRAPH_ROUTE
import uk.govuk.app.home.navigation.HOME_GRAPH_START_DESTINATION
import uk.govuk.app.home.navigation.homeGraph
import uk.govuk.app.launch.AppLaunchState
import uk.govuk.app.launch.AppLaunchViewModel
import uk.govuk.app.navigation.TopLevelDestination
import uk.govuk.app.onboarding.navigation.ONBOARDING_GRAPH_ROUTE
import uk.govuk.app.onboarding.navigation.onboardingGraph
import uk.govuk.app.search.navigation.SEARCH_GRAPH_ROUTE
import uk.govuk.app.search.navigation.searchGraph
import uk.govuk.app.search.ui.widget.SearchWidget
import uk.govuk.app.settings.navigation.settingsGraph

@Composable
fun GovUkApp() {
    val viewModel: AppLaunchViewModel = hiltViewModel()
    val appLaunchState by viewModel.appLaunchState.collectAsState()

    appLaunchState?.let { launchState ->
        BottomNavScaffold(
            onboardingRequired = launchState == AppLaunchState.ONBOARDING_REQUIRED
        ) {
            viewModel.onboardingCompleted()
        }
    }
}

@Composable
fun BottomNavScaffold(
    onboardingRequired: Boolean,
    onboardingCompleted: () -> Unit
) {
    val topLevelDestinations = listOf(TopLevelDestination.Home, TopLevelDestination.Settings)

    var selectedIndex by rememberSaveable {
        mutableIntStateOf(0)
    }

    val navController = rememberNavController()
    navController.addOnDestinationChangedListener { _, destination, _ ->
        selectedIndex = topLevelDestinations.indexOfFirst { it.route == destination.parent?.route }
    }

    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentNavRoute = currentBackStackEntry?.destination?.parent?.route

    Scaffold(
        bottomBar = {
            // Display bottom nav is current destination is a tab destination
            if (topLevelDestinations.any { it.route == currentNavRoute }) {
                Column {
                    HorizontalDivider(
                        thickness = 1.dp,
                        color = GovUkTheme.colourScheme.strokes.container
                    )
                    NavigationBar(
                        containerColor = GovUkTheme.colourScheme.surfaces.background
                    ) {
                        topLevelDestinations.forEachIndexed { index, destination ->
                            NavigationBarItem(
                                selected = index == selectedIndex,
                                onClick = {
                                    selectedIndex = index
                                    navController.navigate(destination.route) {
                                        // Pop up to the start destination of the graph to
                                        // avoid building up a large stack of destinations
                                        // on the back stack as users select items
                                        popUpTo(HOME_GRAPH_START_DESTINATION) {
                                            saveState = true
                                        }
                                        // Avoid multiple copies of the same destination when
                                        // re-selecting the same item
                                        launchSingleTop = true
                                        // Restore state when re-selecting a previously selected item
                                        restoreState = true
                                    }
                                },
                                icon = {
                                    Icon(painterResource(destination.icon), contentDescription = null)
                                },
                                label = {
                                    Text(
                                        text = stringResource(destination.resourceId),
                                        style = GovUkTheme.typography.captionBold,
                                    )
                                },
                                colors = NavigationBarItemDefaults
                                    .colors(
                                        selectedIconColor = GovUkTheme.colourScheme.textAndIcons.buttonPrimary,
                                        selectedTextColor = GovUkTheme.colourScheme.textAndIcons.link,
                                        indicatorColor = GovUkTheme.colourScheme.surfaces.buttonPrimary,
                                        unselectedIconColor = GovUkTheme.colourScheme.textAndIcons.secondary,
                                        unselectedTextColor = GovUkTheme.colourScheme.textAndIcons.secondary,
                                    )
                            )
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = GovUkTheme.colourScheme.surfaces.background
        ) {
            NavHost(
                navController = navController,
                startDestination = if (onboardingRequired) ONBOARDING_GRAPH_ROUTE else HOME_GRAPH_ROUTE
            ) {
                onboardingGraph(
                    onboardingCompleted = {
                        onboardingCompleted()
                        navController.popBackStack()
                        navController.navigate(HOME_GRAPH_ROUTE)
                    }
                )
                homeGraph(
                    widgets = homeScreenWidgets(navController),
                    modifier = Modifier.padding(paddingValues)
                )
                settingsGraph(
                    navController = navController,
                    modifier = Modifier.padding(paddingValues)
                )
                searchGraph(navController)
            }
        }
    }
}

private fun homeScreenWidgets(navController: NavHostController): List<@Composable (Modifier) -> Unit> {
    return listOf { modifier ->
        SearchWidget(
            onClick = {
                navController.navigate(SEARCH_GRAPH_ROUTE)
            },
            modifier = modifier
        )
    }
}