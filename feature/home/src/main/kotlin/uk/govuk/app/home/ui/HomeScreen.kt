package uk.govuk.app.home.ui

import android.content.res.Configuration
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import uk.govuk.app.design.ui.component.LargeVerticalSpacer
import uk.govuk.app.design.ui.theme.GovUkTheme
import uk.govuk.app.home.HomeViewModel
import uk.govuk.app.home.R
import kotlin.math.max

@Composable
internal fun HomeRoute(
    widgets: List<@Composable (Modifier) -> Unit>,
    modifier: Modifier = Modifier
) {
    val viewModel: HomeViewModel = hiltViewModel()

    HomeScreen(
        widgets = widgets,
        onPageView = { viewModel.onPageView() },
        isSearchEnabled = viewModel.isSearchEnabled(),
        modifier = modifier
    )
}

@Composable
private fun HomeScreen(
    widgets: List<@Composable (Modifier) -> Unit>,
    onPageView: () -> Unit,
    isSearchEnabled: Boolean,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(Unit) {
        onPageView()
    }

    var scaleFactor by remember {
        mutableIntStateOf(0)
    }

    val listState = rememberLazyListState()

    LaunchedEffect(listState) {
        snapshotFlow {
            if (listState.firstVisibleItemIndex > 0) {
                -1
            } else {
                listState.firstVisibleItemScrollOffset
            }
        }.collect { offset ->
            scaleFactor = offset
        }
    }

    var isLogoVisible by remember { mutableStateOf(true) }
    val currentOrientation by rememberUpdatedState(newValue = LocalConfiguration.current.orientation)

    LaunchedEffect(currentOrientation) {
        isLogoVisible = currentOrientation == Configuration.ORIENTATION_PORTRAIT
    }

    Column(modifier) {
        AnimatedContent(
            targetState = isLogoVisible,
            label = "LogoTransition",
            transitionSpec = {
                if (targetState) {
                    slideInHorizontally(initialOffsetX = { it }
                        ) + fadeIn() togetherWith
                        slideOutHorizontally(targetOffsetX = { -it }
                        ) + fadeOut()
                } else {
                    slideInHorizontally(initialOffsetX = { -it }
                        ) + fadeIn() togetherWith
                        slideOutHorizontally(targetOffsetX = { it }
                        ) + fadeOut()
                }
            }
        ) { isVisible ->
            if (isVisible) {
                ScalingHeader(
                    scaleFactor = scaleFactor,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(GovUkTheme.colourScheme.surfaces.homeHeader)
                )
            } else {
                if (isSearchEnabled) {
                    StaticHeader(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(GovUkTheme.colourScheme.surfaces.homeHeader)
                    )
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .padding(horizontal = GovUkTheme.spacing.medium),
            state = listState
        ) {
            item {
                LargeVerticalSpacer()
            }
            items(widgets) { widget ->
                widget(Modifier.fillMaxWidth())
            }
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            top = GovUkTheme.spacing.extraLarge,
                            bottom = GovUkTheme.spacing.medium
                        )
                ) {
                    Icon(
                        painter = painterResource(id = uk.govuk.app.design.R.drawable.crown),
                        contentDescription = stringResource(id = uk.govuk.app.design.R.string.crown_alt_text),
                        tint = GovUkTheme.colourScheme.textAndIcons.logoCrown,
                        modifier = Modifier.height(64.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun StaticHeader(
    modifier: Modifier = Modifier
) {
    DisplayLogo(
        padding = 16,
        logoHeight = 28,
        modifier = modifier
    )
}

@Composable
private fun ScalingHeader(
    scaleFactor: Int,
    modifier: Modifier = Modifier
) {
    val initialLogoHeight = 28
    val minLogoHeight = 22

    val initialPadding = 16
    val minPadding = 8

    var logoHeight by remember {
        mutableIntStateOf(initialLogoHeight)
    }

    var padding by remember {
        mutableIntStateOf(initialPadding)
    }

    if (scaleFactor == -1) {
        logoHeight = minLogoHeight
        padding = minPadding
    } else {
        logoHeight = max(
            minLogoHeight,
            (initialLogoHeight - (scaleFactor / 5f)).toInt()
        )
        padding = max(
            minPadding,
            (initialPadding - (scaleFactor / 5f)).toInt()
        )
    }

    DisplayLogo(
        padding = padding,
        logoHeight = logoHeight,
        modifier = modifier
    )
}

@Composable
private fun DisplayLogo(
    padding: Int,
    logoHeight: Int,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Image(
            painter = painterResource(id = uk.govuk.app.design.R.drawable.logo),
            contentDescription = stringResource(id = R.string.logoAltText),
            modifier = Modifier
                .padding(vertical = padding.dp)
                .align(Alignment.CenterHorizontally)
                .height(logoHeight.dp)
                .semantics { heading() }
        )
    }
}
