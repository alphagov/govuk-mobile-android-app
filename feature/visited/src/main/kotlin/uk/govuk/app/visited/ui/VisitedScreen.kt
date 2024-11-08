package uk.govuk.app.visited.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import uk.govuk.app.design.ui.component.BodyBoldLabel
import uk.govuk.app.design.ui.component.BodyRegularLabel
import uk.govuk.app.design.ui.component.ChildPageHeader
import uk.govuk.app.design.ui.component.ExternalLinkListItem
import uk.govuk.app.design.ui.component.ExtraLargeVerticalSpacer
import uk.govuk.app.design.ui.component.LargeVerticalSpacer
import uk.govuk.app.design.ui.component.ListHeadingLabel
import uk.govuk.app.design.ui.component.SmallVerticalSpacer
import uk.govuk.app.design.ui.theme.GovUkTheme
import uk.govuk.app.visited.R
import uk.govuk.app.visited.VisitedUiState
import uk.govuk.app.visited.VisitedViewModel
import uk.govuk.app.visited.ui.model.VisitedUi

@Composable
internal fun VisitedRoute(
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
    modifier: Modifier = Modifier
) {
    LaunchedEffect(Unit) {
        onPageView()
    }

    val title = stringResource(R.string.visited_items_title)

    Column(modifier) {
        ChildPageHeader(
            text = title,
            onBack = onBack
        )
        LazyColumn(Modifier.padding(horizontal = GovUkTheme.spacing.medium)) {
            item {
                val visitedItems = uiState?.visited
                if (visitedItems != null) {
                    if (visitedItems.isEmpty()) {
                        NoVisitedItems(modifier)
                    } else {
                        ShowVisitedItems(visitedItems, onClick)
                    }
                }
            }
        }
    }
}

@Composable
private fun NoVisitedItems(
    modifier: Modifier = Modifier
) {
    Column(
        modifier.padding(GovUkTheme.spacing.medium)
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
