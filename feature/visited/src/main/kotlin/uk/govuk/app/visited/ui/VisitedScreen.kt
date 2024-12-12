package uk.govuk.app.visited.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import uk.govuk.app.design.ui.component.BodyBoldLabel
import uk.govuk.app.design.ui.component.BodyRegularLabel
import uk.govuk.app.design.ui.component.ChildPageHeader
import uk.govuk.app.design.ui.component.ExternalLinkListItem
import uk.govuk.app.design.ui.component.ExtraLargeVerticalSpacer
import uk.govuk.app.design.ui.component.LargeTitleBoldLabel
import uk.govuk.app.design.ui.component.LargeVerticalSpacer
import uk.govuk.app.design.ui.component.ListHeadingLabel
import uk.govuk.app.design.ui.component.SmallVerticalSpacer
import uk.govuk.app.design.ui.theme.GovUkTheme
import uk.govuk.app.visited.R
import uk.govuk.app.visited.VisitedUiState
import uk.govuk.app.visited.VisitedViewModel
import uk.govuk.app.visited.navigation.navigateToEditVisited
import uk.govuk.app.visited.ui.model.VisitedUi

@Composable
internal fun VisitedRoute(
    navController: NavController,
    onBack: () -> Unit,
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
    modifier: Modifier = Modifier
) {
    LaunchedEffect(Unit) {
        onPageView()
    }

    val title = stringResource(R.string.visited_items_title)
    val editText = stringResource(R.string.visited_items_edit_button)
    val visitedItems = uiState?.visited
    val lifecycleOwner = LocalLifecycleOwner.current
    val lifecycleState by lifecycleOwner.lifecycle.currentStateFlow.collectAsState()
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }
    var isViewingItem by remember { mutableStateOf(false) }

    Column(modifier) {
        val onAction = if (!visitedItems.isNullOrEmpty()) onEditClick else null

        ChildPageHeader(
            onBack = onBack,
            onAction = onAction,
            actionText = editText,
        )
        LargeTitleBoldLabel(
            text = title,
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester)
                .focusable()
                .padding(horizontal = GovUkTheme.spacing.medium)
        )
        LazyColumn(
            Modifier
                .padding(horizontal = GovUkTheme.spacing.medium)
                .padding(top = GovUkTheme.spacing.small)
        ) {
            item {
                if (visitedItems.isNullOrEmpty()) {
                    NoVisitedItems(modifier)
                } else {
                    ShowVisitedItems(visitedItems) { title, url ->
                        isViewingItem = true
                        onClick(title, url)
                    }
                }
            }
        }
    }

    LaunchedEffect(lifecycleState) {
        when (lifecycleState) {
            Lifecycle.State.RESUMED -> {
                if (isViewingItem) {
                    focusManager.clearFocus(true)
                    delay(500)
                    focusRequester.requestFocus()
                    isViewingItem = false
                }
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
) {
    val lastVisitedText = stringResource(R.string.visited_items_last_visited)

    items.forEach { (sectionTitle, visitedItems) ->
        if (visitedItems.isNotEmpty()) {
            ListHeadingLabel(sectionTitle)
            SmallVerticalSpacer()

            visitedItems.forEachIndexed { index, item ->
                val title = item.title
                val lastVisited = item.lastVisited
                val url = item.url
                val context = LocalContext.current
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(url)

                ExternalLinkListItem(
                    title = title,
                    onClick = {
                        onClick(title, url)
                        context.startActivity(intent)
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
