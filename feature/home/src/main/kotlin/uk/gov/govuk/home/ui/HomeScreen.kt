package uk.gov.govuk.home.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.LocalOverscrollConfiguration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import uk.gov.govuk.design.ui.component.LargeVerticalSpacer
import uk.gov.govuk.design.ui.component.MediumVerticalSpacer
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.gov.govuk.home.HomeViewModel
import uk.gov.govuk.home.R

@Composable
internal fun HomeRoute(
    widgets: List<@Composable (Modifier) -> Unit>,
    modifier: Modifier = Modifier,
    headerWidget: (@Composable (Modifier) -> Unit)? = null,
) {
    val viewModel: HomeViewModel = hiltViewModel()

    HomeScreen(
        widgets = widgets,
        onPageView = { viewModel.onPageView() },
        modifier = modifier,
        headerWidget = headerWidget
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun HomeScreen(
    widgets: List<@Composable (Modifier) -> Unit>,
    onPageView: () -> Unit,
    modifier: Modifier = Modifier,
    headerWidget: (@Composable (Modifier) -> Unit)? = null,
) {
    LaunchedEffect(Unit) {
        onPageView()
    }

    Column(modifier) {
        Column(
            Modifier
                .fillMaxWidth()
                .background(GovUkTheme.colourScheme.surfaces.homeHeader)
                .padding(horizontal = GovUkTheme.spacing.medium)
        ) {
            MediumVerticalSpacer()

            Image(
                painter = painterResource(id = uk.gov.govuk.design.R.drawable.logo),
                contentDescription = stringResource(id = R.string.logoAltText),
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
            )

            MediumVerticalSpacer()

            if (headerWidget != null) {
                headerWidget(Modifier.fillMaxWidth())
                MediumVerticalSpacer()
            }
        }

        val scaleViewOnScroll = rememberScaleViewOnScroll(maxViewSize = 64.dp)

        Box(Modifier.nestedScroll(scaleViewOnScroll.nestedScrollConnection)) {
            CompositionLocalProvider(
                LocalOverscrollConfiguration provides null
            ) {
                LazyColumn(
                    modifier = Modifier
                        .padding(horizontal = GovUkTheme.spacing.medium),
                    state = rememberLazyListState()
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
                                painter = painterResource(id = uk.gov.govuk.design.R.drawable.crown),
                                contentDescription = stringResource(id = uk.gov.govuk.design.R.string.crown_alt_text),
                                tint = GovUkTheme.colourScheme.textAndIcons.logoCrown,
                                modifier = Modifier
                                    .size(scaleViewOnScroll.maxViewSize)
                                    .graphicsLayer {
                                        scaleX = scaleViewOnScroll.scaleFactor
                                        scaleY = scaleViewOnScroll.scaleFactor
                                        translationY =
                                            -(scaleViewOnScroll.maxViewSize.toPx() - scaleViewOnScroll.currentViewSize.toPx()) / 2f
                                    }
                            )
                        }
                    }
                }
            }
        }
    }
}
