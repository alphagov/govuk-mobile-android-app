package uk.gov.govuk.visited.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import uk.gov.govuk.design.ui.component.BodyRegularLabel
import uk.gov.govuk.design.ui.component.CardListItemLegacy
import uk.gov.govuk.design.ui.component.ChildPageHeader
import uk.gov.govuk.design.ui.component.LargeVerticalSpacer
import uk.gov.govuk.design.ui.component.ListHeadingLabel
import uk.gov.govuk.design.ui.component.MediumVerticalSpacer
import uk.gov.govuk.design.ui.component.SmallVerticalSpacer
import uk.gov.govuk.design.ui.component.SubheadlineRegularLabel
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.gov.govuk.visited.R
import uk.gov.govuk.visited.VisitedUiState
import uk.gov.govuk.visited.VisitedViewModel
import uk.gov.govuk.visited.ui.model.VisitedUi

@Composable
internal fun EditVisitedRoute(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: VisitedViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()

    EditVisitedScreen(
        uiState = uiState,
        actions = EditVisitedScreenActions(
            onEditPageView = { viewModel.onEditPageView() },
            onBack = {
                viewModel.onDoneClick()
                onBack()
            },
            onSelect = { title, url ->
                viewModel.onSelect(title, url)
            },
            onSelectAll = {
                viewModel.onSelectAllClick()
                viewModel.onSelectAll()
            },
            onDeselectAll = {
                viewModel.onDeselectAllClick()
                viewModel.onDeselectAll()
            },
            onRemove = {
                viewModel.onRemoveClick()
                viewModel.onRemove()
                onBack()
            }
        ),
        modifier = modifier
    )
}

private class EditVisitedScreenActions(
    val onEditPageView: () -> Unit,
    val onBack: () -> Unit,
    val onSelect: (String, String) -> Unit,
    val onSelectAll: () -> Unit,
    val onDeselectAll: () -> Unit,
    val onRemove: () -> Unit,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditVisitedScreen(
    uiState: VisitedUiState?,
    actions: EditVisitedScreenActions,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(Unit) {
        actions.onEditPageView()
    }

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

    val visitedItems = uiState?.visited
    val titleText = stringResource(R.string.visited_items_title)
    val editingCompleteButtonText = stringResource(R.string.visited_items_editing_complete_button)
    val editingCompleteAltText = stringResource(R.string.visited_items_editing_complete_alt)

    Scaffold(
        containerColor = GovUkTheme.colourScheme.surfaces.background,
        modifier = modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection)
            .fillMaxWidth(),

        topBar = {
            ChildPageHeader(
                text = titleText,
                onAction = actions.onBack,
                actionText = editingCompleteButtonText,
                actionAltText = editingCompleteAltText
            )
        },
        bottomBar = {
            BottomNavBar(
                onRemove = actions.onRemove,
                uiState = uiState,
                onSelectAll = actions.onSelectAll,
                onDeselectAll = actions.onDeselectAll,
                modifier = modifier
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .padding(innerPadding)
                .padding(horizontal = GovUkTheme.spacing.medium)
                .fillMaxWidth()
        ) {
            item {
                MediumVerticalSpacer()
            }

            item {
                visitedItems?.let { items ->
                    val lastVisitedText = stringResource(R.string.visited_items_last_visited)

                    items.forEach { group ->
                        val sectionTitle = group.key
                        val sectionItems = group.value
                        sectionItems.takeIf { it.isNotEmpty() }?.let { items ->
                            ListHeadingLabel(sectionTitle)
                            SmallVerticalSpacer()

                            items.forEachIndexed { index, item ->
                                CheckableExternalLinkListItem(
                                    item = item,
                                    subText = "$lastVisitedText ${item.lastVisited}",
                                    isFirst = index == 0,
                                    isLast = index == items.size - 1,
                                    onSelect = actions.onSelect,
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
    item: VisitedUi,
    subText: String,
    isFirst: Boolean,
    isLast: Boolean,
    onSelect: (String, String) -> Unit,
    modifier: Modifier = Modifier,
) {
    CardListItemLegacy(
        modifier = modifier,
        isFirst = isFirst,
        isLast = isLast
    ) {
        val interactionSource = remember { MutableInteractionSource() }
        Row(
            modifier = Modifier
                .clickable(
                    interactionSource = interactionSource,
                    indication = null
                ){
                    onSelect(item.title, item.url)
                },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.padding(0.dp),
            ) {
                Checkbox(
                    checked = item.isSelected,
                    onCheckedChange = { onSelect(item.title, item.url) },
                    colors = CheckboxDefaults.colors(
                        checkedColor = GovUkTheme.colourScheme.surfaces.switchOn,
                        uncheckedColor = GovUkTheme.colourScheme.strokes.switchOff,
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
                        text = item.title,
                        modifier = Modifier.weight(1f),
                        color = GovUkTheme.colourScheme.textAndIcons.link
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

@Composable
private fun BottomNavBar(
    onRemove: () -> Unit,
    uiState: VisitedUiState?,
    onSelectAll: () -> Unit,
    onDeselectAll: () -> Unit,
    modifier: Modifier = Modifier
) {
    val selectText = stringResource(R.string.visited_items_select_all_button)
    val deselectText = stringResource(R.string.visited_items_deselect_all_button)
    val removeText = stringResource(R.string.visited_items_remove_button)

    Column(modifier = modifier) {
        HorizontalDivider(
            thickness = 1.dp,
            color = GovUkTheme.colourScheme.strokes.fixedContainer
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = 64.dp)
                .padding(horizontal = GovUkTheme.spacing.large),
            verticalAlignment = Alignment.CenterVertically
        ) {
            var onClick = onSelectAll
            var buttonText = selectText

            if (uiState?.hasAllSelectedItems == true) {
                onClick = onDeselectAll
                buttonText = deselectText
            }

            Box(
                modifier = Modifier.weight(1f)
            ) {
                TextButton(
                    onClick = onClick
                ) {
                    BodyRegularLabel(
                        text = buttonText,
                        color = GovUkTheme.colourScheme.textAndIcons.link,
                        textAlign = TextAlign.Center
                    )
                }
            }

            TextButton(
                onClick = onRemove,
                enabled = uiState?.hasSelectedItems == true,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = GovUkTheme.colourScheme.textAndIcons.buttonDestructive,
                    disabledContentColor = GovUkTheme.colourScheme.textAndIcons.buttonRemoveDisabled
                )
            ) {
                Text(
                    text = removeText,
                    style = GovUkTheme.typography.bodyRegular
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
            uiState = VisitedUiState(
                visited = emptyMap(),
                hasSelectedItems = false,
                hasAllSelectedItems = false
            ),
            actions = EditVisitedScreenActions(
                onBack = {},
                onEditPageView = {},
                onSelect = { _, _ -> },
                onSelectAll = {},
                onDeselectAll = {},
                onRemove = {}
            ),
            modifier = Modifier
        )
    }
}
