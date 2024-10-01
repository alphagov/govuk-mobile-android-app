package uk.govuk.app.ui

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import uk.govuk.app.AppViewModel
import uk.govuk.app.BuildConfig
import uk.govuk.app.PRIVACY_POLICY_URL
import uk.govuk.app.analytics.navigation.ANALYTICS_GRAPH_ROUTE
import uk.govuk.app.analytics.navigation.analyticsGraph
import uk.govuk.app.design.ui.theme.GovUkTheme
import uk.govuk.app.home.navigation.HOME_GRAPH_ROUTE
import uk.govuk.app.home.navigation.HOME_GRAPH_START_DESTINATION
import uk.govuk.app.home.navigation.homeGraph
import uk.govuk.app.navigation.TopLevelDestination
import uk.govuk.app.onboarding.navigation.ONBOARDING_GRAPH_ROUTE
import uk.govuk.app.onboarding.navigation.onboardingGraph
import uk.govuk.app.search.navigation.SEARCH_GRAPH_ROUTE
import uk.govuk.app.search.navigation.searchGraph
import uk.govuk.app.search.ui.widget.SearchWidget
import uk.govuk.app.settings.navigation.settingsGraph
import uk.govuk.app.topics.ui.widget.TopicsWidget

@Composable
internal fun GovUkApp() {
    val viewModel: AppViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()
    var isSplashDone by rememberSaveable { mutableStateOf(false) }

    if (isSplashDone && uiState != null) {
        SetStatusBarColour(
            colour = GovUkTheme.colourScheme.surfaces.background,
            isLight = !isSystemInDarkTheme()
        )
        uiState?.let {
            BottomNavScaffold(
                shouldDisplayAnalyticsConsent = it.shouldDisplayAnalyticsConsent,
                shouldDisplayOnboarding = it.shouldDisplayOnboarding,
                isSearchEnabled = it.isSearchEnabled,
                onboardingCompleted = { viewModel.onboardingCompleted() },
                onTabClick = { tabText -> viewModel.onTabClick(tabText) },
                onWidgetClick = { text -> viewModel.onWidgetClick(text) }
            )
        }
    } else {
        SetStatusBarColour(
            colour = GovUkTheme.colourScheme.surfaces.primary,
            isLight = false
        )
        SplashScreen { isSplashDone = true }
    }
}

@Composable
private fun BottomNavScaffold(
    shouldDisplayAnalyticsConsent: Boolean,
    shouldDisplayOnboarding: Boolean,
    isSearchEnabled: Boolean,
    onboardingCompleted: () -> Unit,
    onTabClick: (String) -> Unit,
    onWidgetClick: (String) -> Unit
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
                            val tabText = stringResource(destination.resourceId)

                            NavigationBarItem(
                                selected = index == selectedIndex,
                                onClick = {
                                    selectedIndex = index
                                    onTabClick(tabText)
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
                                        text = tabText,
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
            val startDestination =
                if (shouldDisplayAnalyticsConsent) {
                    ANALYTICS_GRAPH_ROUTE
                } else if (shouldDisplayOnboarding) {
                    ONBOARDING_GRAPH_ROUTE
                } else {
                    HOME_GRAPH_ROUTE
                }

            NavHost(
                navController = navController,
                startDestination = startDestination
            ) {
                analyticsGraph(
                    privacyPolicyUrl = PRIVACY_POLICY_URL,
                    analyticsConsentCompleted = {
                        if (shouldDisplayOnboarding) {
                            navController.popBackStack()
                            navController.navigate(ONBOARDING_GRAPH_ROUTE)
                        } else {
                            navController.popBackStack()
                            navController.navigate(HOME_GRAPH_ROUTE)
                        }
                    }
                )
                onboardingGraph(
                    onboardingCompleted = {
                        onboardingCompleted()
                        navController.popBackStack()
                        navController.navigate(HOME_GRAPH_ROUTE)
                    }
                )
                homeGraph(
                    widgets = homeScreenWidgets(
                        navController,
                        isSearchEnabled,
                        onWidgetClick
                    ),
                    modifier = Modifier.padding(paddingValues)
                )
                settingsGraph(
                    appVersion = BuildConfig.VERSION_NAME,
                    privacyPolicyUrl = PRIVACY_POLICY_URL,
                    navController = navController,
                    modifier = Modifier.padding(paddingValues)
                )
                searchGraph(navController)
            }
        }
    }
}

private fun homeScreenWidgets(
    navController: NavHostController,
    isSearchEnabled: Boolean,
    onClick: (String) -> Unit
): List<@Composable (Modifier) -> Unit> {
    return listOf(
        { modifier ->
            if (isSearchEnabled) {
                SearchWidget(
                    onClick = { text ->
                        onClick(text)
                        navController.navigate(SEARCH_GRAPH_ROUTE)
                    },
                    modifier = modifier
                )
            }
        },
        { modifier ->
            TopicsWidget(
                onClick = onClick, // Todo - launch topics page
                modifier = modifier
            )
        }
    )
}

@Composable
private fun SetStatusBarColour(
    colour: Color,
    isLight: Boolean
) {
    val view = LocalView.current
    val window = (view.context as Activity).window
    window.statusBarColor = colour.toArgb()
    WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = isLight
}