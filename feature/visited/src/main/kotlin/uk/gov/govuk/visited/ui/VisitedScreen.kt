package uk.gov.govuk.visited.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import uk.gov.govuk.design.ui.component.ScreenBackground
import uk.gov.govuk.design.ui.component.BodyBoldLabel
import uk.gov.govuk.design.ui.component.BodyRegularLabel
import uk.gov.govuk.design.ui.component.ChildPageHeader
import uk.gov.govuk.design.ui.component.ExternalLinkListItem
import uk.gov.govuk.design.ui.component.ExtraLargeVerticalSpacer
import uk.gov.govuk.design.ui.component.LargeVerticalSpacer
import uk.gov.govuk.design.ui.component.ListHeadingLabel
import uk.gov.govuk.design.ui.component.MediumVerticalSpacer
import uk.gov.govuk.design.ui.component.SmallVerticalSpacer
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.gov.govuk.visited.R
import uk.gov.govuk.visited.VisitedUiState
import uk.gov.govuk.visited.VisitedViewModel
import uk.gov.govuk.visited.navigation.navigateToEditVisited
import uk.gov.govuk.visited.ui.model.VisitedUi

@Composable
internal fun VisitedRoute(
    navController: NavController,
    onBack: () -> Unit,
    launchBrowser: (url: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: VisitedViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()

    VisitedScreen(
        uiState = uiState,
        onPageView = { viewModel.onPageView() },
        onBack = onBack,
        onClick = { title, url ->
            viewModel.onVisitedItemClicked(title, url)
        },
        onEditClick = {
            viewModel.onEditClick()
            navController.navigateToEditVisited()
        },
        launchBrowser = launchBrowser,
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
    onEditClick: () -> Unit,
    launchBrowser: (url: String) -> Unit,
    modifier: Modifier = Modifier
) {
    ScreenBackground()

    LaunchedEffect(Unit) {
        onPageView()
    }

    val title = stringResource(R.string.visited_items_title)
    val editText = stringResource(R.string.visited_items_edit_button)
    val editAltText = stringResource(R.string.visited_items_edit_button_alt_text)
    val visitedItems = uiState?.visited
    val lifecycleOwner = LocalLifecycleOwner.current
    val lifecycleState by lifecycleOwner.lifecycle.currentStateFlow.collectAsState()
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }

    Column(modifier) {
        val onAction = if (!visitedItems.isNullOrEmpty()) onEditClick else null

        ChildPageHeader(
            text = title,
            onBack = onBack,
            onAction = onAction,
            actionText = editText,
            actionAltText = editAltText,
            modifier = Modifier.focusRequester(focusRequester)
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
                    ShowVisitedItems(visitedItems, onClick, launchBrowser)
                }
            }
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
    Column(
        modifier
            .padding(GovUkTheme.spacing.medium)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ExtraLargeVerticalSpacer()

        BodyBoldLabel(
            stringResource(R.string.visited_items_no_pages_title)
        )

        BodyRegularLabel(
            stringResource(R.string.visited_items_no_pages_description),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun ShowVisitedItems(
    items: Map<String, List<VisitedUi>>,
    onClick: (title: String, url: String) -> Unit,
    launchBrowser: (url: String) -> Unit
) {
    val lastVisitedText = stringResource(R.string.visited_items_last_visited)
    val coroutineScope = rememberCoroutineScope()

    items.forEach { (sectionTitle, visitedItems) ->
        if (visitedItems.isNotEmpty()) {
            ListHeadingLabel(sectionTitle)
            SmallVerticalSpacer()

            visitedItems.forEachIndexed { index, item ->
                val title = item.title
                val lastVisited = item.lastVisited
                val url = item.url

                ExternalLinkListItem(
                    title = title,
                    onClick = {
                        coroutineScope.launch {
                            delay(500)
                            onClick(title, url)
                        }
                        launchBrowser(url)
                    },
                    isFirst = index == 0,
                    isLast = index == visitedItems.size - 1,
                    subText = "$lastVisitedText $lastVisited"
                )
            }

            LargeVerticalSpacer()
        }
    }
}
