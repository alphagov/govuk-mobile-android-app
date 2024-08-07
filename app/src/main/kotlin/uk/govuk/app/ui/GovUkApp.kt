package uk.govuk.app.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import uk.govuk.app.R
import uk.govuk.app.design.ui.theme.GovUkTheme
import uk.govuk.app.home.ui.navigation.homeGraph
import uk.govuk.app.launch.AppLaunchState
import uk.govuk.app.launch.AppLaunchViewModel
import uk.govuk.app.navigation.TopLevelDestination
import uk.govuk.app.onboarding.ui.navigation.ONBOARDING_GRAPH_ROUTE
import uk.govuk.app.onboarding.ui.navigation.onboardingGraph
import uk.govuk.app.settings.ui.navigation.settingsGraph

private const val SPLASH_ROUTE = "splash"
private const val ONBOARDING_COMPLETED_ROUTE = "onboarding_completed"

@Composable
fun GovUkApp() {
    val viewModel: AppLaunchViewModel = hiltViewModel()
    val appLaunchState by viewModel.appLaunchState.collectAsState()
    var isSplashDone by rememberSaveable { mutableStateOf(false) }

    SetStatusBarColour(
        if (isSplashDone)
            GovUkTheme.colourScheme.surfaces.background
        else
            GovUkTheme.colourScheme.surfaces.primary,
        isSplashDone && !isSystemInDarkTheme()
    )

    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = SPLASH_ROUTE
    ) {
        composable(SPLASH_ROUTE) {
            SplashScreen {
                isSplashDone = true
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
        }
        onboardingGraph {
            viewModel.onboardingCompleted()
            navController.popBackStack()
            navController.navigate(ONBOARDING_COMPLETED_ROUTE)
        }
        composable(ONBOARDING_COMPLETED_ROUTE) { BottomNavScaffold() }
    }
}

@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(DelicateCoroutinesApi::class)
@Composable
private fun SplashScreen(
    onSplashDone: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth()
            .background(GovUkTheme.colourScheme.surfaces.primary),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val composition by rememberLottieComposition(
            LottieCompositionSpec.RawRes(R.raw.app_splash)
        )

        var state = animateLottieCompositionAsState(composition = composition)

        // Handle cases where animation is disabled...
        if (areAnimationsDisabled(LocalContext.current)) {
            state = animateLottieCompositionAsState(composition = composition, isPlaying = false)
            GlobalScope.launch {
                delay(6000) // wait for 6 seconds
                onSplashDone()
            }
        // Animations are enabled...
        } else {
            LaunchedEffect(state.progress) {
                if (state.progress == 1f) {
                    onSplashDone()
                }
            }
        }

        LottieAnimation(
            composition = composition,
            progress = { state.progress }
        )
    }
}

@Composable
private fun BottomNavScaffold() {
    val topLevelDestinations = listOf(TopLevelDestination.Home, TopLevelDestination.Settings)

    var selectedIndex by remember {
        mutableIntStateOf(0)
    }

    val navController = rememberNavController()
    Scaffold(
        bottomBar = {
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
                                    popUpTo(navController.graph.findStartDestination().id) {
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
    ) { innerPadding ->
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = GovUkTheme.colourScheme.surfaces.background
        ) {
            NavHost(
                navController = navController,
                startDestination = TopLevelDestination.Home.route,
                modifier = Modifier
                    .padding(innerPadding)
            ) {
                homeGraph()
                settingsGraph(navController)
            }
        }
    }
}

fun areAnimationsDisabled(context: Context): Boolean {
    val animatorDurationScale = Settings.Global.getFloat(
        context.contentResolver,
        Settings.Global.ANIMATOR_DURATION_SCALE,
        1f
    )
    return animatorDurationScale == 0f
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
