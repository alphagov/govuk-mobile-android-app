package uk.gov.govuk.visited.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import uk.gov.govuk.design.ui.component.BodyBoldLabel
import uk.gov.govuk.design.ui.component.BodyRegularLabel
import uk.gov.govuk.design.ui.component.ChildPageHeader
import uk.gov.govuk.design.ui.component.ExternalLinkListItem
import uk.gov.govuk.design.ui.component.LargeVerticalSpacer
import uk.gov.govuk.design.ui.component.MediumVerticalSpacer
import uk.gov.govuk.design.ui.component.NonTappableCard
import uk.gov.govuk.design.ui.component.SectionHeadingLabel
import uk.gov.govuk.design.ui.component.SmallVerticalSpacer
import uk.gov.govuk.design.ui.model.ExternalLinkListItemStyle
import uk.gov.govuk.design.ui.model.HeaderStyle
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.gov.govuk.visited.R
import uk.gov.govuk.visited.VisitedUiState
import uk.gov.govuk.visited.VisitedViewModel
import uk.gov.govuk.visited.ui.model.VisitedUi

@Composable
internal fun VisitedRoute(
    onBack: () -> Unit,
    launchBrowser: (url: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: VisitedViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    VisitedScreen(
        uiState = uiState,
        onPageView = { viewModel.onPageView() },
        onBack = onBack,
        onClick = { title, url ->
            coroutineScope.launch {
                delay(500)
                viewModel.onVisitedItemClicked(title, url)
            }
            launchBrowser(url)
        },
        onRemoveAllClick = {
            viewModel.onRemoveAllVisitedItemsClicked()
        },
        onRemoveClick = { title, url ->
            viewModel.onVisitedItemRemoveClicked(title, url)
        },
        modifier = modifier
    )
}

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
private fun VisitedScreen(
    uiState: VisitedUiState?,
    onPageView: () -> Unit,
    onBack: () -> Unit,
    onClick: (title: String, url: String) -> Unit,
    onRemoveAllClick: () -> Unit,
    onRemoveClick: (title: String, url: String) -> Unit,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(Unit) {
        onPageView()
    }

    val title = stringResource(R.string.visited_items_title)
    val removeAllText = stringResource(R.string.visited_items_remove_all_button)
    val removeAllAltText = stringResource(R.string.visited_items_remove_all)
    val visitedItems = uiState?.visited
    val lifecycleOwner = LocalLifecycleOwner.current
    val lifecycleState by lifecycleOwner.lifecycle.currentStateFlow.collectAsState()
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }
    var showRemoveAllDialog by remember { mutableStateOf(false) }

    Column(
        modifier
            .fillMaxSize()
            .background(GovUkTheme.colourScheme.surfaces.screenBackground)
    ) {

        ChildPageHeader(
            text = title,
            onBack = onBack,
            modifier = Modifier.focusRequester(focusRequester),
            style = if (visitedItems.isNullOrEmpty()) {
                HeaderStyle.Default
            } else {
                HeaderStyle.ActionButton(
                    onClick = { showRemoveAllDialog = true },
                    title = removeAllText,
                    altText = removeAllAltText
                )
            }
        )

        LazyColumn(
            Modifier
                .padding(horizontal = GovUkTheme.spacing.medium)
        ) {
            item {
                MediumVerticalSpacer()
            }
            item {
                if (visitedItems.isNullOrEmpty()) {
                    NoVisitedItems(modifier)
                } else {
                    ShowVisitedItems(visitedItems, onClick, onRemoveClick)
                }
            }
        }

        if (showRemoveAllDialog) {
            RemoveAllConfirmationDialog(
                onConfirm = onRemoveAllClick,
                onDismiss = { showRemoveAllDialog = false }
            )
        }
    }

    LaunchedEffect(lifecycleState) {
        when (lifecycleState) {
            Lifecycle.State.RESUMED -> {
                focusManager.clearFocus(true)
                delay(500)
                focusRequester.requestFocus()
            }
            else -> { /* Do nothing */ }
        }
    }
}

@Composable
private fun NoVisitedItems(
    modifier: Modifier = Modifier
) {
    NonTappableCard(
        body = stringResource(R.string.visited_items_no_pages_description),
        modifier = modifier
    )
}

@Composable
private fun ShowVisitedItems(
    items: Map<String, List<VisitedUi>>,
    onClick: (title: String, url: String) -> Unit,
    onRemoveClick: (title: String, url: String) -> Unit
) {
    val lastVisitedText = stringResource(R.string.visited_items_last_visited)

    items.forEach { (sectionTitle, visitedItems) ->
        if (visitedItems.isNotEmpty()) {
            SectionHeadingLabel(title3 = sectionTitle)

            SmallVerticalSpacer()

            visitedItems.forEachIndexed { index, item ->
                val title = item.title
                val lastVisited = item.lastVisited
                val url = item.url

                ExternalLinkListItem(
                    title = title,
                    description = "$lastVisitedText $lastVisited",
                    onClick = {
                        onClick(title, url)
                    },
                    isFirst = index == 0,
                    isLast = index == visitedItems.size - 1,
                    style = ExternalLinkListItemStyle.Button(
                        icon = uk.gov.govuk.design.R.drawable.ic_cancel_round,
                        altText = "${stringResource(uk.gov.govuk.design.R.string.content_desc_remove)} $title",
                        onClick = {
                            onRemoveClick(title, url)
                        }
                    )
                )
            }

            LargeVerticalSpacer()
        }
    }
}

@Composable
private fun RemoveAllConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm()
                    onDismiss()
                }
            ) {
                BodyRegularLabel(
                    text = stringResource(R.string.visited_items_remove_all_button),
                    color = GovUkTheme.colourScheme.textAndIcons.buttonDestructive
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                BodyRegularLabel(
                    text = stringResource(R.string.visited_items_cancel_button),
                    color = GovUkTheme.colourScheme.textAndIcons.link
                )
            }
        },
        modifier = modifier,
        title = {
            BodyBoldLabel(stringResource(R.string.visited_items_remove_all))
        },
        containerColor = GovUkTheme.colourScheme.surfaces.alert
    )
}
