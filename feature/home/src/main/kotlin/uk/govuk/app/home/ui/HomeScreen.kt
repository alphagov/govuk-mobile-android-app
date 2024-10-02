package uk.govuk.app.home.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import uk.govuk.app.design.ui.component.LargeVerticalSpacer
import uk.govuk.app.design.ui.theme.GovUkTheme
import uk.govuk.app.home.HomeViewModel
import uk.govuk.app.home.R
import kotlin.math.max
import kotlin.math.min

@Composable
internal fun HomeRoute(
    widgets: List<@Composable (Modifier) -> Unit>,
    modifier: Modifier = Modifier
) {
    val viewModel: HomeViewModel = hiltViewModel()

    HomeScreen(
        widgets = widgets,
        onPageView = { viewModel.onPageView() },
        modifier = modifier
    )
}

@Composable
private fun HomeScreen(
    widgets: List<@Composable (Modifier) -> Unit>,
    onPageView: () -> Unit,
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

    Column(modifier) {
        ScalingHeader(
            scaleFactor = scaleFactor,
            modifier = Modifier
                .fillMaxWidth()
        )

        LazyColumn (
            modifier = Modifier
                .padding(horizontal = GovUkTheme.spacing.medium),
            state = listState
        ) {
            items(widgets) { widget ->
                LargeVerticalSpacer()
                widget(Modifier.fillMaxWidth())
            }
            item{
                LargeVerticalSpacer()
            }
        }
    }
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

    var dividerAlpha by remember {
        mutableFloatStateOf(0f)
    }

    if (scaleFactor == -1) {
        logoHeight = minLogoHeight
        padding = minPadding
        dividerAlpha = 1f
    } else {
        logoHeight = max(
            minLogoHeight,
            (initialLogoHeight - (scaleFactor / 5f)).toInt()
        )
        padding = max(
            minPadding,
            (initialPadding - (scaleFactor / 5f)).toInt()
        )
        dividerAlpha = min(1f, scaleFactor / 100f)
    }

    Column(modifier = modifier) {
        Image(
            painter = painterResource(id = uk.govuk.app.design.R.drawable.logo),
            contentDescription = stringResource(id = R.string.logoAltText),
            modifier = Modifier
                .padding(vertical = padding.dp)
                .align(Alignment.CenterHorizontally)
                .height(logoHeight.dp)
        )
        HorizontalDivider(
            modifier = Modifier.alpha(dividerAlpha),
            thickness = 1.dp,
            color = GovUkTheme.colourScheme.strokes.container
        )
    }
}
