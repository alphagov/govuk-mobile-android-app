package uk.govuk.app.home.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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

    val scrollState = rememberScrollState()

    LaunchedEffect(scrollState) {
        snapshotFlow {
            scrollState.value
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

        // Todo - ideally this would be a lazy column to gain from performance optimizations, however
        //  nested lazy columns are not allowed without a non-trivial workaround (some widgets will
        //  themselves contain a lazy column/grid). The performance impact should be negligible with
        //  the amount of items currently displayed on the home screen but we may have to re-visit
        //  this in the future.
        Column (
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = GovUkTheme.spacing.medium)
                .padding(
                    bottom = GovUkTheme.spacing.large
                )
                .verticalScroll(scrollState)
        ) {
            for (widget in widgets) {
                LargeVerticalSpacer()
                widget(Modifier.fillMaxWidth())
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
