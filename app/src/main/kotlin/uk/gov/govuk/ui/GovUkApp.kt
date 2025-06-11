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
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
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
import uk.gov.govuk.design.ui.component.error.AppUnavailableScreen
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.gov.govuk.extension.asDeepLinks
import uk.gov.govuk.extension.getUrlParam
import uk.gov.govuk.home.navigation.HOME_GRAPH_START_DESTINATION
import uk.gov.govuk.home.navigation.homeGraph
import uk.gov.govuk.login.navigation.LOGIN_ROUTE
import uk.gov.govuk.login.navigation.loginGraph
import uk.gov.govuk.navigation.DeepLink
import uk.gov.govuk.navigation.TopLevelDestination
import uk.gov.govuk.notifications.navigation.NOTIFICATIONS_CONSENT_GRAPH_ROUTE
import uk.gov.govuk.notifications.navigation.NOTIFICATIONS_ONBOARDING_ROUTE
import uk.gov.govuk.notifications.navigation.NOTIFICATIONS_PERMISSION_ROUTE
import uk.gov.govuk.notifications.navigation.notificationsConsentGraph
import uk.gov.govuk.notifications.navigation.notificationsOnboardingGraph
import uk.gov.govuk.notifications.navigation.notificationsPermissionGraph
import uk.gov.govuk.search.navigation.SEARCH_GRAPH_ROUTE
import uk.gov.govuk.search.navigation.searchGraph
import uk.gov.govuk.search.ui.widget.SearchWidget
import uk.gov.govuk.settings.navigation.settingsGraph
import uk.gov.govuk.settings.navigation.signOutGraph
import uk.gov.govuk.topics.navigation.topicSelectionGraph
import uk.gov.govuk.topics.navigation.topicsGraph
import uk.gov.govuk.ui.model.HomeWidget
import uk.gov.govuk.visited.navigation.visitedGraph
import uk.govuk.app.local.navigation.localGraph

@Composable
internal fun GovUkApp(intentFlow: Flow<Intent>) {
    val viewModel: AppViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()
    val homeWidgets by viewModel.homeWidgets.collectAsState()
    var isSplashDone by rememberSaveable { mutableStateOf(false) }
    var isRecommendUpdateSkipped by rememberSaveable { mutableStateOf(false) }

    if (isSplashDone && uiState != null) {
        SetStatusBarColour(GovUkTheme.colourScheme.surfaces.homeHeader)
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
                        BottomNavScaffold(
                            intentFlow = intentFlow,
                            viewModel = viewModel,
                            shouldDisplayNotificationsOnboarding = it.shouldDisplayNotificationsOnboarding,
                            shouldShowExternalBrowser = it.shouldShowExternalBrowser,
                            homeWidgets = homeWidgets
                        )
                    }
                }
            }
        }
    } else {
        SetStatusBarColour(GovUkTheme.colourScheme.surfaces.splash)
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
    intentFlow: Flow<Intent>,
    viewModel: AppViewModel,
    shouldDisplayNotificationsOnboarding: Boolean,
    shouldShowExternalBrowser: Boolean,
    homeWidgets: List<HomeWidget>?,
) {
    val navController = rememberNavController()
    val navBarPadding = WindowInsets.navigationBars.asPaddingValues()
    val section = stringResource(R.string.homepage)

    Scaffold(
        contentWindowInsets = WindowInsets(0.dp),
        bottomBar = {
            BottomNav(viewModel, navController) { tabText ->
                viewModel.onTabClick(tabText)
            }
        },
        modifier = Modifier.padding(bottom = navBarPadding.calculateBottomPadding())
    ) { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    awaitPointerEventScope {
                        while (true) {
                            awaitPointerEvent()
                            viewModel.onUserInteraction(navController)
                        }
                    }
                },
            color = GovUkTheme.colourScheme.surfaces.background
        ) {
            GovUkNavHost(
                viewModel = viewModel,
                navController = navController,
                homeWidgets = homeWidgets,
                onInternalWidgetClick = { text ->
                    viewModel.onWidgetClick(
                        text = text,
                        external = false,
                        section = section
                    )
                },
                onExternalWidgetClick = { text, url ->
                    viewModel.onWidgetClick(
                        text = text,
                        url = url,
                        external = true,
                        section = section
                    )
                },
                onSuppressWidgetClick = { text, widget ->
                    viewModel.onSuppressWidgetClick(text, section, widget)
                },
                shouldShowExternalBrowser = shouldShowExternalBrowser,
                paddingValues = paddingValues
            )
        }
    }
    HandleReceivedIntents(
        intentFlow = intentFlow,
        navController = navController,
        shouldShowExternalBrowser = shouldShowExternalBrowser,
    ) { hasDeepLink, url ->
        viewModel.onDeepLinkReceived(hasDeepLink, url)
    }
    if (shouldDisplayNotificationsOnboarding) {
        HandleNotificationsPermissionStatus(navController = navController)
    }
}

@Composable
private fun HandleReceivedIntents(
    intentFlow: Flow<Intent>,
    navController: NavHostController,
    shouldShowExternalBrowser: Boolean,
    onDeepLinkReceived: (hasDeepLink: Boolean, url: String) -> Unit
) {
    val context = LocalContext.current
    val browserLauncher = rememberBrowserLauncher(shouldShowExternalBrowser)
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
                    uri.getUrlParam(DeepLink.allowedAppUrls, DeepLink.allowedGovUkUrls)?.let {
                        onDeepLinkReceived(true, uri.toString())
                        browserLauncher.launch(it.toString())
                    } ?: run {
                        onDeepLinkReceived(false, uri.toString())
                        showDeepLinkNotFoundAlert(context = context)
                    }
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
private fun HandleNotificationsPermissionStatus(
    navController: NavHostController
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val lifecycleState by lifecycleOwner.lifecycle.currentStateFlow.collectAsState()

    LaunchedEffect(lifecycleState) {
        when (lifecycleState) {
            Lifecycle.State.RESUMED -> {
                val route = when (navController.currentDestination?.route) {
                    NOTIFICATIONS_ONBOARDING_ROUTE -> NOTIFICATIONS_ONBOARDING_ROUTE
                    NOTIFICATIONS_PERMISSION_ROUTE -> return@LaunchedEffect
                    else -> NOTIFICATIONS_CONSENT_GRAPH_ROUTE
                }
                navController.navigate(route) {
                    launchSingleTop = true
                }
            }
            else -> { /* Do nothing */ }
        }
    }
}

@Composable
private fun BottomNav(
    viewModel: AppViewModel,
    navController: NavHostController,
    onTabClick: (String) -> Unit
) {
    val topLevelDestinations = listOf(TopLevelDestination.Home, TopLevelDestination.Settings)

    var selectedIndex by rememberSaveable {
        mutableIntStateOf(-1)
    }

    LaunchedEffect(Unit) {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            viewModel.onUserInteraction(navController)
            selectedIndex =
                topLevelDestinations.indexOfFirst { topLevelDestination ->
                    topLevelDestination.route == destination.parent?.route ||
                            topLevelDestination.associatedRoutes.any {
                                destination.route?.startsWith(it) == true
                            }
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
                containerColor = GovUkTheme.colourScheme.surfaces.background,
                windowInsets = WindowInsets(0.dp)
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
    viewModel: AppViewModel,
    navController: NavHostController,
    homeWidgets: List<HomeWidget>?,
    onInternalWidgetClick: (String) -> Unit,
    onExternalWidgetClick: (String, String?) -> Unit,
    onSuppressWidgetClick: (String, HomeWidget) -> Unit,
    shouldShowExternalBrowser: Boolean,
    paddingValues: PaddingValues
) {
    val appLaunchNavigation = viewModel.appLaunchNavigation
    val startDestination = appLaunchNavigation.startDestination
    val browserLauncher = rememberBrowserLauncher(shouldShowExternalBrowser)
    val context = LocalContext.current

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        analyticsGraph(
            analyticsConsentCompleted = {
                appLaunchNavigation.onNext(navController)
            },
            launchBrowser = { url -> browserLauncher.launchPartial(context = context, url = url) }
        )
        if (homeWidgets.contains(HomeWidget.TOPICS)) {
            topicSelectionGraph(
                topicSelectionCompleted = {
                    viewModel.topicSelectionCompleted()
                    appLaunchNavigation.onNext(navController)
                }
            )
            topicsGraph(
                navController = navController,
                deepLinks = { it.asDeepLinks(DeepLink.allowedAppUrls) },
                launchBrowser = { url -> browserLauncher.launch(url) },
                modifier = Modifier.padding(paddingValues)
            )
        }
        notificationsOnboardingGraph(
            notificationsOnboardingCompleted = {
                navController.popBackStack()
                appLaunchNavigation.onNext(navController)
                navController.navigate(NOTIFICATIONS_CONSENT_GRAPH_ROUTE)
            },
            launchBrowser = { url -> browserLauncher.launchPartial(context = context, url = url) }
        )
        notificationsPermissionGraph(
            notificationsPermissionCompleted = {
                navController.popBackStack()
            },
            launchBrowser = { url -> browserLauncher.launchPartial(context = context, url = url) }
        )
        notificationsConsentGraph(
            notificationsConsentCompleted = {
                navController.navigateUp()
            },
            launchBrowser = { url -> browserLauncher.launchPartial(context = context, url = url) }
        )
        loginGraph(
            navController = navController,
            onLoginCompleted = {
                viewModel.onLogin(navController)
            },
            onBiometricSetupCompleted = {
                appLaunchNavigation.onNext(navController)
            }
        )
        homeGraph(
            widgets = homeWidgets(
                navController = navController,
                homeWidgets = homeWidgets,
                onInternalClick = onInternalWidgetClick,
                onExternalClick = onExternalWidgetClick,
                onSuppressClick = onSuppressWidgetClick,
                launchBrowser = { url -> browserLauncher.launch(url) }
            ),
            deepLinks = { it.asDeepLinks(DeepLink.allowedAppUrls) },
            modifier = Modifier.padding(paddingValues),
            headerWidget = if (homeWidgets.contains(HomeWidget.SEARCH)) {
                { modifier ->
                    SearchWidget(
                        onClick = { text ->
                            onInternalWidgetClick(text)
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
            appVersion = BuildConfig.VERSION_NAME_USER_FACING,
            deepLinks = { it.asDeepLinks(DeepLink.allowedAppUrls) },
            launchBrowser = { url -> browserLauncher.launchPartial(context = context, url = url) },
            modifier = Modifier.padding(paddingValues)
        )
        signOutGraph(
            navController = navController,
            onSignOut = {
                viewModel.onSignOut()
                navController.navigate(LOGIN_ROUTE) {
                    popUpTo(0) { inclusive = true }
                }
            }
        )
        if (homeWidgets.contains(HomeWidget.SEARCH)) {
            searchGraph(
                navController,
                deepLinks = { it.asDeepLinks(DeepLink.allowedAppUrls) },
                launchBrowser = { url -> browserLauncher.launch(url) })
        }
        if (homeWidgets.contains(HomeWidget.RECENT_ACTIVITY)) {
            visitedGraph(
                navController = navController,
                deepLinks = { it.asDeepLinks(DeepLink.allowedAppUrls) },
                launchBrowser = { url -> browserLauncher.launch(url) },
                modifier = Modifier.padding(paddingValues)
            )
        }
        if (homeWidgets.contains(HomeWidget.LOCAL)) {
            val exitLocalAuth: () -> Unit =
                { navController.popBackStack(HOME_GRAPH_START_DESTINATION, false) }

            localGraph(
                navController = navController,
                onLocalAuthoritySelected = exitLocalAuth,
                onCancel = exitLocalAuth,
                modifier = Modifier.padding(paddingValues)
            )
        }
    }
}

@Suppress("DEPRECATION")
@Composable
private fun SetStatusBarColour(
    colour: Color
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

    WindowCompat.getInsetsController(window, localView).isAppearanceLightStatusBars = false
}
