package uk.govuk.app.home.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import uk.govuk.app.design.ui.theme.GovUkTheme
import uk.govuk.app.home.HomeViewModel
import kotlin.math.max
import kotlin.math.min

@Composable
internal fun HomeRoute(
    widget: @Composable () -> Unit
) {
    val viewModel: HomeViewModel = hiltViewModel()

    HomeScreen(
        onPageView = { viewModel.onPageView() },
        widget
    )
}

@Composable
private fun HomeScreen(
    onPageView: () -> Unit,
    widget: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(Unit) {
        onPageView()
    }

    var scaleFactor by remember {
        mutableIntStateOf(0)
    }

    Column(modifier) {
        ScalingHeader(
            scaleFactor = scaleFactor,
            modifier = Modifier
                .fillMaxWidth()
        )

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

        widget()

        LazyColumn(
            state = listState
        ) {
            val list = (1..12).map { it.toString() }
            items(count = list.size) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = GovUkTheme.colourScheme.surfaces.card,
                    ),
                    border = BorderStroke(1.dp, GovUkTheme.colourScheme.strokes.listDivider),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(GovUkTheme.spacing.medium)
                ) {
                    Text(
                        text = "Scrollable content",
                        style = GovUkTheme.typography.bodyRegular,
                        color = GovUkTheme.colourScheme.textAndIcons.primary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(GovUkTheme.spacing.medium)
                    )
                }
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
            contentDescription = null,
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