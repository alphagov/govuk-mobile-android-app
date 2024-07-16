package uk.govuk.app.home.ui

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import uk.govuk.app.design.ui.theme.GovUkTheme

@Composable
internal fun HomeRoute() {
    // Collect UI state from view model here and pass to screen (if necessary)
    HomeScreen()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeScreen() {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    val isCollapsed = remember { derivedStateOf { scrollBehavior.state.collapsedFraction > 0.5 } }

    val headerHeight by animateDpAsState(
        targetValue = if (isCollapsed.value) 80.dp else 135.dp
    )

    val fontSize = if (isCollapsed.value) 22.sp else 28.sp

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),

        topBar = {
            LargeTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = GovUkTheme.colourScheme.surfaces.background,
                    scrolledContainerColor = GovUkTheme.colourScheme.surfaces.background,
                ),
                title = {
                    Box(
                        modifier = Modifier.fillMaxSize()
                            .wrapContentSize()
                    ) {
                        Text(
                            text = govUkText(fontSize),
                            maxLines = 1,
                            textAlign = TextAlign.Center,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                },
                modifier = Modifier.height(headerHeight),
                scrollBehavior = scrollBehavior,
            )
        },
        content = { innerPadding ->
            LazyColumn(
                contentPadding = innerPadding,
                verticalArrangement = Arrangement.spacedBy(GovUkTheme.spacing.medium),
                modifier = Modifier.background(GovUkTheme.colourScheme.surfaces.background)
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
                            .background(GovUkTheme.colourScheme.strokes.listDivider, shape = RoundedCornerShape(10.dp))
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
    )
}

@Composable
fun govUkText(fontSize: androidx.compose.ui.unit.TextUnit): AnnotatedString {
    val textColor = GovUkTheme.colourScheme.surfaces.primary
    val dotColor = GovUkTheme.colourScheme.textAndIcons.dot
    val fontWeight = FontWeight.Bold

    return buildAnnotatedString {
        withStyle(
            style = SpanStyle(
                fontWeight = fontWeight,
                fontSize = fontSize,
                color = textColor,
                baselineShift = BaselineShift(multiplier = 1f)
            )
        ) {
            append("GOV")
        }
        withStyle(
            style = SpanStyle(
                fontWeight = fontWeight,
                fontSize = ((fontSize.value / 2) + fontSize.value).sp,
                color = dotColor,
                baselineShift = BaselineShift(multiplier = 1.3f)
            )
        ) {
            append(".")
        }
        withStyle(
            style = SpanStyle(
                fontWeight = fontWeight,
                fontSize = fontSize,
                color = textColor,
                baselineShift = BaselineShift(multiplier = 1f)
            )
        ) {
            append("UK")
        }
    }
}
