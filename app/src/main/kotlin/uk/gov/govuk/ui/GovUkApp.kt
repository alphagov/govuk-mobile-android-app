package uk.gov.govuk.ui

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import uk.gov.govuk.AppUiState
import uk.gov.govuk.AppViewModel
import uk.gov.govuk.BuildConfig
import uk.gov.govuk.R
import uk.gov.govuk.analytics.navigation.analyticsGraph
import uk.gov.govuk.design.ui.component.LargeVerticalSpacer
import uk.gov.govuk.design.ui.component.error.AppUnavailableScreen
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.gov.govuk.extension.asDeepLinks
import uk.gov.govuk.home.HomeWidget
import uk.gov.govuk.home.navigation.homeGraph
import uk.gov.govuk.navigation.AppLaunchNavigation
import uk.gov.govuk.navigation.TopLevelDestination
import uk.gov.govuk.notifications.navigation.notificationsGraph
import uk.gov.govuk.notifications.ui.NotificationsPromptWidget
import uk.gov.govuk.notifications.ui.notificationsPermissionShouldShowRationale
import uk.gov.govuk.onboarding.navigation.onboardingGraph
import uk.gov.govuk.search.navigation.SEARCH_GRAPH_ROUTE
import uk.gov.govuk.search.navigation.searchGraph
import uk.gov.govuk.search.ui.widget.SearchWidget
import uk.gov.govuk.settings.BuildConfig.PRIVACY_POLICY_URL
import uk.gov.govuk.settings.navigation.navigateToHelpAndFeedback
import uk.gov.govuk.settings.navigation.settingsGraph
import uk.gov.govuk.settings.ui.FeedbackPromptWidget
import uk.gov.govuk.topics.navigation.navigateToTopic
import uk.gov.govuk.topics.navigation.navigateToTopicsAll
import uk.gov.govuk.topics.navigation.navigateToTopicsEdit
import uk.gov.govuk.topics.navigation.topicsGraph
import uk.gov.govuk.topics.ui.widget.TopicsWidget
import uk.gov.govuk.visited.navigation.VISITED_GRAPH_ROUTE
import uk.gov.govuk.visited.navigation.visitedGraph
import uk.gov.govuk.visited.ui.widget.VisitedWidget

@Composable
internal fun GovUkApp(intentFlow: Flow<Intent>) {
    val viewModel: AppViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()
    val homeWidgets by viewModel.homeWidgets.collectAsState()
    var isSplashDone by rememberSaveable { mutableStateOf(false) }
    var isRecommendUpdateSkipped by rememberSaveable { mutableStateOf(false) }

    if (isSplashDone && uiState != null) {
        SetStatusBarColour(
            colour = GovUkTheme.colourScheme.surfaces.homeHeader,
            isLight = false
        )
        uiState?.let {
            when (it) {
                is AppUiState.Loading -> LoadingScreen()
                is AppUiState.AppUnavailable -> AppUnavailableScreen()
                is AppUiState.DeviceOffline -> DeviceOfflineScreen(
                    onTryAgain = { viewModel.onTryAgain() }
                )
                is AppUiState.ForcedUpdate -> ForcedUpdateScreen()
                is AppUiState.Default -> {
                    if (it.shouldDisplayRecommendUpdate && !isRecommendUpdateSkipped) {
                        RecommendUpdateScreen(
                            recommendUpdateSkipped = { isRecommendUpdateSkipped = true }
                        )
                    } else {
                        val section = stringResource(R.string.homepage)
                        BottomNavScaffold(
                            uiState = it,
                            intentFlow = intentFlow,
                            onboardingCompleted = { viewModel.onboardingCompleted() },
                            topicSelectionCompleted = { viewModel.topicSelectionCompleted() },
                            onTabClick = { tabText -> viewModel.onTabClick(tabText) },
                            homeWidgets = homeWidgets,
                            onWidgetClick = { text, external ->
                                viewModel.onWidgetClick(text, external, section)
                            },
                            onSuppressWidgetClick = { text, widget ->
                                viewModel.onSuppressWidgetClick(text, section, widget)
                            },
                            onDeepLinkReceived = { hasDeepLink, url ->
                                viewModel.onDeepLinkReceived(hasDeepLink, url)
                            }
                        )
                    }
                }
            }
        }
    } else {
        SetStatusBarColour(
            colour = GovUkTheme.colourScheme.surfaces.splash,
            isLight = false
        )
        SplashScreen { isSplashDone = true }
    }
}

@Composable
private fun LoadingScreen(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(Modifier.width(64.dp))
    }
}

@Composable
private fun BottomNavScaffold(
    uiState: AppUiState.Default,
    intentFlow: Flow<Intent>,
    onboardingCompleted: () -> Unit,
    topicSelectionCompleted: () -> Unit,
    onTabClick: (String) -> Unit,
    homeWidgets: List<HomeWidget>?,
    onWidgetClick: (String, Boolean) -> Unit,
    onSuppressWidgetClick: (String, HomeWidget) -> Unit,
    onDeepLinkReceived: (Boolean, String) -> Unit
) {
    val navController = rememberNavController()

    Scaffold(
        contentWindowInsets = WindowInsets(0.dp),
        bottomBar = {
            BottomNav(navController, onTabClick)
        }
    ) { paddingValues ->
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = GovUkTheme.colourScheme.surfaces.background
        ) {
            GovUkNavHost(
                navController = navController,
                uiState = uiState,
                onboardingCompleted = onboardingCompleted,
                topicSelectionCompleted = topicSelectionCompleted,
                homeWidgets = homeWidgets,
                onWidgetClick = onWidgetClick,
                onSuppressWidgetClick = onSuppressWidgetClick,
                paddingValues = paddingValues
            )
        }
    }
    val context = LocalContext.current

    // Collect and handle intent data sent with deep links
    LaunchedEffect(intentFlow) {
        intentFlow.collectLatest { intent ->
            intent.data?.let { uri ->
                if (navController.graph.hasDeepLink(uri)) {
                    onDeepLinkReceived(true, uri.toString())
                    val request = NavDeepLinkRequest.Builder
                        .fromUri(uri)
                        .build()
                    navController.navigate(
                        request,
                        navOptions = NavOptions.Builder().setLaunchSingleTop(true).build()
                    )
                } else {
                    onDeepLinkReceived(false, uri.toString())
                    showDeepLinkNotFoundAlert(context = context)
                }
            }
        }
    }
}

private fun showDeepLinkNotFoundAlert(context: Context) {
    AlertDialog.Builder(context).apply {
        setTitle(context.getString(R.string.deep_link_not_found_alert_title))
        setMessage(context.getString(R.string.deep_link_not_found_alert_message))
        setPositiveButton(context.getString(R.string.deep_link_not_found_alert_button)) { dialog, _ ->
            dialog.dismiss()
        }
    }.also { deepLinkNotFoundAlert ->
        deepLinkNotFoundAlert.show()
    }
}

@Composable
private fun BottomNav(
    navController: NavHostController,
    onTabClick: (String) -> Unit
) {
    val topLevelDestinations = listOf(TopLevelDestination.Home, TopLevelDestination.Settings)

    var selectedIndex by rememberSaveable {
        mutableIntStateOf(0)
    }

    navController.addOnDestinationChangedListener { _, destination, _ ->
        selectedIndex =
            topLevelDestinations.indexOfFirst { topLevelDestination ->
                topLevelDestination.route == destination.parent?.route ||
                        topLevelDestination.associatedRoutes.any {
                            destination.route?.startsWith(it) == true
                        }
            }
    }

    // Display the nav bar if the current destination has a tab index (is a top level destination
    // or associated route)
    val displayBottomNavBar = selectedIndex != -1

    if (displayBottomNavBar) {
        Column {
            HorizontalDivider(
                thickness = 1.dp,
                color = GovUkTheme.colourScheme.strokes.fixedContainer
            )
            NavigationBar(
                containerColor = GovUkTheme.colourScheme.surfaces.background
            ) {
                topLevelDestinations.forEachIndexed { index, destination ->
                    val tabText = stringResource(destination.stringResId)

                    NavigationBarItem(
                        selected = index == selectedIndex,
                        onClick = {
                            selectedIndex = index
                            onTabClick(tabText)

                            navController.navigate(destination.route) {
                                // Pop up to the start destination of the graph to
                                // avoid building up a large stack of destinations
                                // on the back stack as users select items
                                popUpTo(destination.route)
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
                                indicatorColor = GovUkTheme.colourScheme.surfaces.primary,
                                unselectedIconColor = GovUkTheme.colourScheme.textAndIcons.secondary,
                                unselectedTextColor = GovUkTheme.colourScheme.textAndIcons.secondary,
                            )
                    )
                }
            }
        }
    }
}

@Composable
private fun GovUkNavHost(
    navController: NavHostController,
    uiState: AppUiState.Default,
    onboardingCompleted: () -> Unit,
    topicSelectionCompleted: () -> Unit,
    homeWidgets: List<HomeWidget>?,
    onWidgetClick: (String, Boolean) -> Unit,
    onSuppressWidgetClick: (String, HomeWidget) -> Unit,
    paddingValues: PaddingValues
) {
    val launchRoutes = rememberSaveable { AppLaunchNavigation(uiState).launchRoutes }
    val startDestination = rememberSaveable { launchRoutes.pop() }

    val context = LocalContext.current

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        analyticsGraph(
            privacyPolicyUrl = PRIVACY_POLICY_URL,
            analyticsConsentCompleted = {
                navController.popBackStack()
                navController.navigate(launchRoutes.pop())
            }
        )
        onboardingGraph(
            onboardingCompleted = {
                onboardingCompleted()
                navController.popBackStack()
                navController.navigate(launchRoutes.pop())
            }
        )
        if (homeWidgets.contains(HomeWidget.TOPICS)) {
            topicsGraph(
                navController = navController,
                topicSelectionCompleted = {
                    topicSelectionCompleted()
                    navController.popBackStack()
                    navController.navigate(launchRoutes.pop())
                },
                deepLinks = { it.asDeepLinks() },
                modifier = Modifier.padding(paddingValues)
            )
        }
        notificationsGraph(
            notificationsOnboardingCompleted = {
                navController.popBackStack()
                if (launchRoutes.isEmpty()) return@notificationsGraph
                navController.navigate(launchRoutes.pop())
            }
        )
        homeGraph(
            widgets = homeScreenWidgets(
                context = context,
                navController = navController,
                homeWidgets = homeWidgets,
                onClick = onWidgetClick,
                onSuppressClick = onSuppressWidgetClick
            ),
            deepLinks = { it.asDeepLinks() },
            modifier = Modifier.padding(paddingValues),
            headerWidget = if (homeWidgets.contains(HomeWidget.SEARCH)) {
                { modifier ->
                    SearchWidget(
                        onClick = { text ->
                            onWidgetClick(text, false)
                            navController.navigate(SEARCH_GRAPH_ROUTE)
                        },
                        modifier = modifier
                    )
                }
            } else null,
            transitionOverrideRoutes = listOf(SEARCH_GRAPH_ROUTE)
        )
        settingsGraph(
            navigateTo = { route -> navController.navigate(route) },
            appVersion = BuildConfig.VERSION_NAME,
            deepLinks = { it.asDeepLinks() },
            modifier = Modifier.padding(paddingValues)
        )
        if (homeWidgets.contains(HomeWidget.SEARCH)) {
            searchGraph(navController, deepLinks = { it.asDeepLinks() })
        }
        if (homeWidgets.contains(HomeWidget.RECENT_ACTIVITY)) {
            visitedGraph(
                navController = navController,
                deepLinks = { it.asDeepLinks() },
                modifier = Modifier.padding(paddingValues)
            )
        }
    }
}

private fun List<HomeWidget>?.contains(widget: HomeWidget) = this?.contains(widget) == true

private fun homeScreenWidgets(
    context: Context,
    navController: NavHostController,
    homeWidgets: List<HomeWidget>?,
    onClick: (String, Boolean) -> Unit,
    onSuppressClick: (String, HomeWidget) -> Unit
): List<@Composable (Modifier) -> Unit> {
    val widgets = mutableListOf<@Composable (Modifier) -> Unit>()
    homeWidgets?.forEach {
        when (it) {
            HomeWidget.NOTIFICATIONS -> {
                widgets.add { modifier ->
                    if (notificationsPermissionShouldShowRationale()) {
                        NotificationsPromptWidget(
                            onClick = { text ->
                                onClick(text, true)
                            },
                            onSuppressClick = { text ->
                                onSuppressClick(text, HomeWidget.NOTIFICATIONS)
                            },
                            modifier = modifier
                        )
                        LargeVerticalSpacer()
                    }
                }
            }

            HomeWidget.FEEDBACK_PROMPT -> {
                widgets.add { modifier ->
                    FeedbackPromptWidget(
                        onClick = { text ->
                            onClick(text, true)
                            navigateToHelpAndFeedback(context, BuildConfig.VERSION_NAME)
                        },
                        modifier = modifier
                    )
                    LargeVerticalSpacer()
                }
            }

            HomeWidget.RECENT_ACTIVITY -> {
                widgets.add { modifier ->
                    VisitedWidget(
                        onClick = { text ->
                            onClick(text, false)
                            navController.navigate(VISITED_GRAPH_ROUTE)
                        },
                        modifier = modifier
                    )
                    LargeVerticalSpacer()
                }
            }

            HomeWidget.TOPICS -> {
                widgets.add { modifier ->
                    TopicsWidget(
                        onTopicClick = { ref, title ->
                            onClick(title, false)
                            navController.navigateToTopic(ref)
                        },
                        onEditClick = { text ->
                            onClick(text, false)
                            navController.navigateToTopicsEdit()
                        },
                        onAllClick = { text ->
                            onClick(text, false)
                            navController.navigateToTopicsAll()
                        },
                        modifier = modifier
                    )
                    LargeVerticalSpacer()
                }
            }

            else -> { /* Do nothing */ }
        }
    }
    return widgets
}

@Composable
private fun SetStatusBarColour(
    colour: Color,
    isLight: Boolean
) {
    val localView = LocalView.current
    val window = (localView.context as Activity).window

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
        window.decorView.setOnApplyWindowInsetsListener { view, insets ->
            val statusBarInsets = insets.getInsets(android.view.WindowInsets.Type.statusBars())
            view.setBackgroundColor(colour.toArgb())
            view.setPadding(0, statusBarInsets.top, 0, 0)
            insets
        }
    } else {
        window.statusBarColor = colour.toArgb()
    }

    WindowCompat.getInsetsController(window, localView).isAppearanceLightStatusBars = isLight
}
