package uk.govuk.app.visited.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import uk.govuk.app.design.ui.component.BodyRegularLabel
import uk.govuk.app.design.ui.component.CardListItem
import uk.govuk.app.design.ui.component.LargeVerticalSpacer
import uk.govuk.app.design.ui.component.ListHeadingLabel
import uk.govuk.app.design.ui.component.MediumHorizontalSpacer
import uk.govuk.app.design.ui.component.SmallVerticalSpacer
import uk.govuk.app.design.ui.component.SubheadlineRegularLabel
import uk.govuk.app.design.ui.component.Title2BoldLabel
import uk.govuk.app.design.ui.theme.GovUkTheme
import uk.govuk.app.visited.R
import uk.govuk.app.visited.VisitedUiState
import uk.govuk.app.visited.VisitedViewModel
import kotlin.random.Random

@Composable
internal fun EditVisitedRoute(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: VisitedViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()

    EditVisitedScreen(
        uiState = uiState,
        onEditPageView = { viewModel.onEditPageView() },
        onBack = onBack,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditVisitedScreen(
    uiState: VisitedUiState?,
    onEditPageView: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(Unit) {
        onEditPageView()
    }

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

    val visitedItems = uiState?.visited
    val titleText = stringResource(R.string.visited_items_title)
    val doneText = stringResource(R.string.visited_items_done_button)
    val selectText = stringResource(R.string.visited_items_select_all_button)
    val removeText = stringResource(R.string.visited_items_remove_button)

    Scaffold(
        containerColor = GovUkTheme.colourScheme.surfaces.background,
        modifier = modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection)
            .fillMaxWidth(),

        topBar = {
            TopNavBar(
                title = titleText,
                doneText = doneText,
                onBack = onBack,
                scrollBehavior = scrollBehavior,
                modifier = modifier
            )
        },
        bottomBar = {
            BottomNavBar(
                selectText = selectText,
                removeText = removeText,
                modifier = modifier
            )
        }
    ) { innerPadding ->
        LazyColumn (
            modifier = modifier
                .padding(innerPadding)
                .padding(horizontal = GovUkTheme.spacing.medium)
                .fillMaxWidth()
                .background(GovUkTheme.colourScheme.surfaces.background)
        ) {
            item {
                if (visitedItems != null) {
                    val lastVisitedText = stringResource(R.string.visited_items_last_visited)

                    visitedItems.forEach { (sectionTitle, visitedItems) ->
                        if (visitedItems.isNotEmpty()) {
                            ListHeadingLabel(sectionTitle)
                            SmallVerticalSpacer()

                            visitedItems.forEachIndexed { index, item ->
                                val title = item.title
                                val lastVisited = item.lastVisited
                                val url = item.url

                                // TODO:
                                //     - Add Select all and Remove handlers
                                //     - Handle select/deselect
                                //     - Add tests
                                CheckableExternalLinkListItem(
                                    title = title,
                                    subText = "$lastVisitedText $lastVisited",
                                    isFirst = index == 0,
                                    isLast = index == visitedItems.size - 1,
                                    modifier = modifier
                                )
                            }

                            LargeVerticalSpacer()
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CheckableExternalLinkListItem(
    title: String,
    subText: String,
    isFirst: Boolean,
    isLast: Boolean,
    modifier: Modifier = Modifier,
) {
    CardListItem(
        modifier = modifier,
        onClick = { /* Do nothing */ },
        isFirst = isFirst,
        isLast = isLast
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.padding(0.dp),
            ) {
                Checkbox(
                    checked = Random.nextBoolean(),
                    onCheckedChange = { /* checked = it */ },
                    colors = CheckboxDefaults.colors(
                        checkedColor = GovUkTheme.colourScheme.surfaces.primary,
                        uncheckedColor = GovUkTheme.colourScheme.strokes.buttonCompactBorder,
                        checkmarkColor = GovUkTheme.colourScheme.textAndIcons.selectedTick,
                    )
                )
            }

            Column(
                modifier = Modifier.padding(0.dp),
            ) {
                Row(
                    modifier = Modifier.padding(
                        top = GovUkTheme.spacing.medium,
                        start = GovUkTheme.spacing.medium,
                        end = GovUkTheme.spacing.medium,
                        bottom = 0.dp
                    )
                ) {
                    BodyRegularLabel(
                        text = title,
                        modifier = Modifier.weight(1f),
                        color = GovUkTheme.colourScheme.textAndIcons.link
                    )

                    MediumHorizontalSpacer()

                    Icon(
                        painter = painterResource(uk.govuk.app.design.R.drawable.ic_external_link),
                        contentDescription = "",
                        tint = GovUkTheme.colourScheme.textAndIcons.link
                    )
                }

                Row(
                    modifier = Modifier.padding(
                        start = GovUkTheme.spacing.medium,
                        top = 0.dp,
                        bottom = GovUkTheme.spacing.medium
                    ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SubheadlineRegularLabel(
                        text = subText,
                        color = GovUkTheme.colourScheme.textAndIcons.primary
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopNavBar(
    title: String,
    doneText: String,
    onBack: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior,
    modifier : Modifier = Modifier,
) {
    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = GovUkTheme.colourScheme.surfaces.background,
            scrolledContainerColor = GovUkTheme.colourScheme.surfaces.background,
            titleContentColor = GovUkTheme.colourScheme.textAndIcons.primary,
        ),
        title = {
            Title2BoldLabel(
                title,
            )
        },
        actions = {
            TextButton(
                onClick = onBack,
                modifier = modifier.wrapContentSize()
            ) {
                BodyRegularLabel(
                    text = doneText,
                    color = GovUkTheme.colourScheme.textAndIcons.link,
                    textAlign = TextAlign.End
                )
            }
        },
        scrollBehavior = scrollBehavior,
    )
}

@Composable
private fun BottomNavBar(
    selectText: String,
    removeText: String,
    modifier : Modifier = Modifier
) {
    val borderColor = GovUkTheme.colourScheme.strokes.listDivider

    NavigationBar(
        containerColor = GovUkTheme.colourScheme.surfaces.background,
        contentColor = GovUkTheme.colourScheme.textAndIcons.primary,
        modifier = modifier
            .fillMaxWidth()
            .height(81.dp)
            .drawBehind {
                val borderSize = 1.dp
                val topEdge = 0f
                drawLine(
                    color = borderColor,
                    start = Offset(0f, topEdge),
                    end = Offset(size.width, topEdge),
                    strokeWidth = borderSize.toPx()
                )
            }
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(horizontal = GovUkTheme.spacing.large),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextButton(
                onClick = { /* TODO */ }
            ) {
                BodyRegularLabel(
                    text = selectText,
                    color = GovUkTheme.colourScheme.textAndIcons.link,
                    textAlign = TextAlign.Start
                )
            }
            TextButton(
                onClick = { /* TODO */ },
            ) {
                BodyRegularLabel(
                    text = removeText,
                    color = GovUkTheme.colourScheme.textAndIcons.buttonRemove,
                    textAlign = TextAlign.End
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun EditVisitedScreenPreview() {
    GovUkTheme {
        EditVisitedScreen(
            uiState = VisitedUiState(visited = emptyMap()),
            onEditPageView = {},
            onBack = {},
            modifier = Modifier
        )
    }
}
