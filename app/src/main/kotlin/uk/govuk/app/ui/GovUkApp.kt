package uk.govuk.app.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import uk.govuk.app.AppLaunchState
import uk.govuk.app.AppLaunchViewModel
import uk.govuk.app.home.ui.navigation.homeGraph
import uk.govuk.app.onboarding.ui.navigation.ONBOARDING_GRAPH_ROUTE
import uk.govuk.app.onboarding.ui.navigation.onboardingGraph
import uk.govuk.app.settings.ui.navigation.settingsGraph
import uk.govuk.app.ui.navigation.TopLevelDestination

private const val SPLASH_ROUTE = "splash"
private const val ONBOARDING_COMPLETED_ROUTE = "onboarding_completed"

@Composable
fun GovUkApp() {
    val viewModel: AppLaunchViewModel = hiltViewModel()
    val appLaunchState by viewModel.appLaunchState.collectAsState()

    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = SPLASH_ROUTE
    ) {
        composable(SPLASH_ROUTE) { } // Todo - splash screen to go here
        onboardingGraph {
            viewModel.onboardingCompleted()
            navController.popBackStack()
            navController.navigate(ONBOARDING_COMPLETED_ROUTE)
        }
        composable(ONBOARDING_COMPLETED_ROUTE) { BottomNavScaffold() }
    }

    appLaunchState?.let {
        when (it) {
            AppLaunchState.ONBOARDING_REQUIRED -> {
                navController.popBackStack()
                navController.navigate(ONBOARDING_GRAPH_ROUTE)
            }
            AppLaunchState.ONBOARDING_COMPLETED -> {
                navController.popBackStack()
                navController.navigate(ONBOARDING_COMPLETED_ROUTE)
            }
        }
    }
}

@Composable
private fun BottomNavScaffold() {
    val topLevelDestinations = listOf(TopLevelDestination.Home, TopLevelDestination.Settings)
    val navController = rememberNavController()
    Scaffold(
        bottomBar = {
            BottomNavigation {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                topLevelDestinations.forEach { destination ->
                    BottomNavigationItem(
                        icon = { Icon(Icons.Filled.Favorite, contentDescription = null) },
                        label = { Text(stringResource(destination.resourceId)) },
                        selected = currentDestination?.hierarchy?.any { it.route == destination.route } == true,
                        onClick = {
                            navController.navigate(destination.route) {
                                // Pop up to the start destination of the graph to
                                // avoid building up a large stack of destinations
                                // on the back stack as users select items
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                // Avoid multiple copies of the same destination when
                                // re-selecting the same item
                                launchSingleTop = true
                                // Restore state when re-selecting a previously selected item
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = TopLevelDestination.Home.route, Modifier.padding(innerPadding)
        ) {
            homeGraph()
            settingsGraph(navController)
        }
    }
}