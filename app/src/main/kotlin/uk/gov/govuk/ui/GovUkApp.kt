package uk.gov.govuk.ui

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsTopHeight
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import uk.gov.govuk.AppUiState
import uk.gov.govuk.AppViewModel
import uk.gov.govuk.BuildConfig
import uk.gov.govuk.R
import uk.gov.govuk.analytics.navigation.analyticsGraph
import uk.gov.govuk.chat.navigation.CHAT_GRAPH_ROUTE
import uk.gov.govuk.chat.navigation.chatGraph
import uk.gov.govuk.chat.navigation.chatOptInGraph
import uk.gov.govuk.design.ui.component.error.AppUnavailableScreen
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.gov.govuk.home.navigation.HOME_GRAPH_START_DESTINATION
import uk.gov.govuk.home.navigation.homeGraph
import uk.gov.govuk.login.navigation.BIOMETRIC_SETTINGS_ROUTE
import uk.gov.govuk.login.navigation.LOGIN_GRAPH_ROUTE
import uk.gov.govuk.login.navigation.loginGraph
import uk.gov.govuk.navigation.AppNavigation
import uk.gov.govuk.navigation.TopLevelDestination
import uk.gov.govuk.notifications.navigation.notificationsGraph
import uk.gov.govuk.search.navigation.SEARCH_GRAPH_ROUTE
import uk.gov.govuk.search.navigation.searchGraph
import uk.gov.govuk.search.ui.widget.SearchWidget
import uk.gov.govuk.settings.navigation.settingsGraph
import uk.gov.govuk.settings.navigation.signOutGraph
import uk.gov.govuk.topics.navigation.topicSelectionGraph
import uk.gov.govuk.topics.navigation.topicsGraph
import uk.gov.govuk.visited.navigation.visitedGraph
import uk.gov.govuk.widgets.model.HomeWidget
import uk.gov.govuk.widgets.ui.contains
import uk.gov.govuk.widgets.ui.homeWidgets
import uk.govuk.app.local.navigation.localGraph

@Composable
internal fun GovUkApp(intentFlow: Flow<Intent>) {
    val viewModel: AppViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()
    val homeWidgets by viewModel.homeWidgets.collectAsState()
    var isSplashDone by rememberSaveable { mutableStateOf(false) }
    var isRecommendUpdateSkipped by rememberSaveable { mutableStateOf(false) }

    if (isSplashDone && uiState != null) {
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
                            uiState = it,
                            homeWidgets = homeWidgets
                        )
                    }
                }
            }
        }
    } else {
        Column {
            StatusBar(false)
            SplashScreen { isSplashDone = true }
        }
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
    uiState: AppUiState.Default,
    homeWidgets: List<HomeWidget>?
) {
    val navController = rememberNavController()
    val layoutDirection = LocalLayoutDirection.current
    val navBarPadding = WindowInsets.navigationBars.asPaddingValues()
    val section = stringResource(R.string.homepage)

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentNavParentRoute = navBackStackEntry?.destination?.parent?.route

    Scaffold(
        contentWindowInsets = WindowInsets(0.dp),
        bottomBar = {
            BottomNav(uiState.isChatEnabled, navController) { tabText ->
                viewModel.onTabClick(tabText)
            }
        },
        modifier = Modifier
            .padding(
                start = navBarPadding.calculateStartPadding(layoutDirection),
                bottom = navBarPadding.calculateBottomPadding(),
                end = navBarPadding.calculateEndPadding(layoutDirection)
            )
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
            Column {
                StatusBar(currentNavParentRoute == CHAT_GRAPH_ROUTE)
                GovUkNavHost(
                    intentFlow = intentFlow,
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
                    onSuppressWidgetClick = { id ->
                        viewModel.onSuppressWidgetClick(id, section)
                    },
                    shouldShowExternalBrowser = uiState.shouldShowExternalBrowser,
                    paddingValues = paddingValues
                )
                HandleOnResumeNavigation(
                    navController = { navController },
                    appNavigation = viewModel.appNavigation
                )
            }
        }
    }
}

@Composable
private fun HandleOnResumeNavigation(
    navController: () -> NavHostController,
    appNavigation: AppNavigation
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val lifecycleState by lifecycleOwner.lifecycle.currentStateFlow.collectAsState()

    LaunchedEffect(lifecycleState) {
        when (lifecycleState) {
            Lifecycle.State.RESUMED -> {
                val controller = navController()
                try {
                    controller.graph
                } catch (e: IllegalStateException) {
                    // Nav graph has not been set
                    return@LaunchedEffect
                }
                appNavigation.navigateOnResume(controller)
            }
            else -> { /* Do nothing */ }
        }
    }
}

@Composable
private fun BottomNav(
    isChatEnabled: Boolean,
    navController: NavHostController,
    onTabClick: (String) -> Unit
) {
    val topLevelDestinations = remember(isChatEnabled) {
        TopLevelDestination.values(isChatEnabled)
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val currentParentRoute = navBackStackEntry?.destination?.parent?.route

    val selectedIndex = remember(topLevelDestinations, currentRoute, currentParentRoute) {
        topLevelDestinations.indexOfFirst { topLevelDestination ->
            topLevelDestination.route == currentParentRoute ||
                    topLevelDestination.associatedRoutes.any {
                        currentRoute?.startsWith(it) == true
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
    intentFlow: Flow<Intent>,
    viewModel: AppViewModel,
    navController: NavHostController,
    homeWidgets: List<HomeWidget>?,
    onInternalWidgetClick: (String) -> Unit,
    onExternalWidgetClick: (String, String?) -> Unit,
    onSuppressWidgetClick: (id: String) -> Unit,
    shouldShowExternalBrowser: Boolean,
    paddingValues: PaddingValues
) {
    val appNavigation = viewModel.appNavigation
    val browserLauncher = rememberBrowserLauncher(shouldShowExternalBrowser)
    val context = LocalContext.current

    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        appNavigation.setOnLaunchBrowser { url ->
            browserLauncher.launch(url) { showBrowserNotFoundAlert(context) }
        }

        appNavigation.setOnDeeplinkNotFound {
            showDeepLinkNotFoundAlert(context)
        }

        intentFlow.collectLatest { intent ->
            appNavigation.setDeeplink(navController, intent.data)
        }
    }

    NavHost(
        navController = navController,
        startDestination = LOGIN_GRAPH_ROUTE
    ) {
        loginGraph(
            navController = navController,
            onLoginCompleted = {
                viewModel.onLogin(navController)
            }
        )
        analyticsGraph(
            analyticsConsentCompleted = {
                coroutineScope.launch {
                    appNavigation.onNext(navController)
                }
            },
            launchBrowser = { url ->
                browserLauncher.launchPartial(
                    context = context,
                    url = url
                ) { showBrowserNotFoundAlert(context) }
            }
        )
        topicSelectionGraph(
            topicSelectionCompleted = {
                coroutineScope.launch {
                    viewModel.topicSelectionCompleted()
                    appNavigation.onNext(navController)
                }
            }
        )
        topicsGraph(
            navController = navController,
            launchBrowser = { url -> browserLauncher.launch(url) { showBrowserNotFoundAlert(context) } },
            modifier = Modifier.padding(paddingValues)
        )
        notificationsGraph(
            notificationsOnboardingCompleted = {
                coroutineScope.launch {
                    appNavigation.onNotificationsOnboardingCompleted(navController)
                }
            },
            notificationsConsentOnNextCompleted = {
                coroutineScope.launch {
                    appNavigation.onNext(navController)
                }
            },
            notificationsConsentCompleted = {
                navController.popBackStack()
            },
            notificationsPermissionCompleted = {
                navController.popBackStack()
            },
            launchBrowser = { url ->
                browserLauncher.launchPartial(
                    context = context,
                    url = url
                ) { showBrowserNotFoundAlert(context) }
            }
        )
        homeGraph(
            widgets = homeWidgets(
                navController = navController,
                homeWidgets = homeWidgets,
                onInternalClick = onInternalWidgetClick,
                onExternalClick = onExternalWidgetClick,
                onSuppressClick = onSuppressWidgetClick,
                launchBrowser = { url ->
                    browserLauncher.launch(url) {
                        showBrowserNotFoundAlert(
                            context
                        )
                    }
                }
            ),
            modifier = Modifier.padding(paddingValues),
            headerWidget = if (homeWidgets.contains(HomeWidget.Search)) {
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
            onBiometricsClick = { navController.navigate(BIOMETRIC_SETTINGS_ROUTE) },
            appVersion = BuildConfig.VERSION_NAME_USER_FACING,
            launchBrowser = { url ->
                browserLauncher.launchPartial(
                    context = context,
                    url = url
                ) { showBrowserNotFoundAlert(context) }
            },
            modifier = Modifier.padding(paddingValues)
        )
        signOutGraph(
            navController = navController,
            onSignOut = {
                appNavigation.onSignOut(navController)
            }
        )
        searchGraph(
            navController,
            launchBrowser = { url -> browserLauncher.launch(url) { showBrowserNotFoundAlert(context) } })
        visitedGraph(
            navController = navController,
            launchBrowser = { url -> browserLauncher.launch(url) { showBrowserNotFoundAlert(context) } },
            modifier = Modifier.padding(paddingValues)
        )

        val exitLocal: () -> Unit =
            { navController.popBackStack(HOME_GRAPH_START_DESTINATION, false) }

        localGraph(
            navController = navController,
            onLocalAuthoritySelected = exitLocal,
            onCancel = exitLocal,
            modifier = Modifier.padding(paddingValues)
        )

        chatGraph(
            navController = navController,
            launchBrowser = { url -> browserLauncher.launch(url) { showBrowserNotFoundAlert(context) } },
            onAuthError = { appNavigation.onSignOut(navController) },
            modifier = Modifier.padding(paddingValues)
        )

        chatOptInGraph(
            navController = navController,
            launchBrowser = { url -> browserLauncher.launch(url) { showBrowserNotFoundAlert(context) } }
        )
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

private fun showBrowserNotFoundAlert(context: Context) {
    AlertDialog.Builder(context).apply {
        setTitle(context.getString(R.string.browser_not_found_alert_title))
        setMessage(context.getString(R.string.browser_not_found_alert_message))
        setPositiveButton(context.getString(R.string.browser_not_found_alert_button)) { dialog, _ ->
            dialog.dismiss()
        }
    }.also { deepLinkNotFoundAlert ->
        deepLinkNotFoundAlert.show()
    }
}

@Composable
private fun StatusBar(
    isChat: Boolean,
    modifier: Modifier = Modifier
) {
    val localView = LocalView.current
    val window = (localView.context as Activity).window

    if (!isChat) {
        Box(
            modifier
                .fillMaxWidth()
                .windowInsetsTopHeight(WindowInsets.statusBars)
                .background(GovUkTheme.colourScheme.surfaces.homeHeader)
        )
    }

    WindowCompat.getInsetsController(window, localView).isAppearanceLightStatusBars = isChat
}
