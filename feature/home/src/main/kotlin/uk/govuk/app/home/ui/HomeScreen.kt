package uk.govuk.app.home.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import uk.govuk.app.design.ui.theme.GovUkTheme
import kotlin.math.max
import kotlin.math.min

@Composable
internal fun HomeRoute() {
    // Collect UI state from view model here and pass to screen (if necessary)
    HomeScreen()
}

@Composable
private fun HomeScreen() {
    val initialLogoHeight = 28
    val minLogoHeight = 22

    val initialPadding = 16
    val minPadding = 8

    var logoHeight by remember {
        mutableStateOf(initialLogoHeight)
    }

    var padding by remember {
        mutableStateOf(initialPadding)
    }

    var dividerAlpha by remember {
        mutableStateOf(0f)
    }

    Column {
        Header(
            logoHeight = logoHeight.dp,
            verticalPadding = padding.dp,
            dividerAlpha = dividerAlpha,
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
                if (offset == -1) {
                    logoHeight = minLogoHeight
                    padding = minPadding
                    dividerAlpha = 1f
                } else {
                    logoHeight = max(
                        minLogoHeight,
                        (initialLogoHeight - (offset / 5f)).toInt()
                    )
                    padding = max(
                        minPadding,
                        (initialPadding - (offset / 5f)).toInt()
                    )
                    dividerAlpha = min(1f, offset / 100f)
                }
            }
        }

        LazyColumn(
            modifier = Modifier.background(GovUkTheme.colourScheme.surfaces.background),
            state = listState,
            verticalArrangement = Arrangement.spacedBy(GovUkTheme.spacing.medium)
        ) {
            val list = (1..12).map { it.toString() }
            items(count = list.size) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = GovUkTheme.colourScheme.surfaces.container,
                    ),
                    border = BorderStroke(1.dp, GovUkTheme.colourScheme.strokes.listDivider),
                    modifier = Modifier
                        .padding(GovUkTheme.spacing.medium)
                        .fillMaxSize()
                        .height(200.dp)
                        .background(
                            GovUkTheme.colourScheme.strokes.listDivider,
                            shape = RoundedCornerShape(10.dp)
                        )
                ) {
                    Text(
                        text = "Scrollable content",
                        style = GovUkTheme.typography.bodyRegular,
                        color = GovUkTheme.colourScheme.textAndIcons.primary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(GovUkTheme.spacing.medium)
                    )
                }
            }
        }
    }
}

@Composable
private fun Header(
    logoHeight: Dp,
    verticalPadding: Dp,
    dividerAlpha: Float,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Image(
            painter = painterResource(id = uk.govuk.app.design.R.drawable.logo),
            contentDescription = null,
            modifier = Modifier
                .padding(vertical = verticalPadding)
                .align(Alignment.CenterHorizontally)
                .height(logoHeight)
        )
        Divider(
            modifier = Modifier
                .alpha(dividerAlpha),
            thickness = 1.dp,
            // Todo - should be container stroke colour
            color = GovUkTheme.colourScheme.strokes.listDivider
        )
    }
}