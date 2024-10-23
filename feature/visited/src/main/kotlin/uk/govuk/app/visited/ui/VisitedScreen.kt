package uk.govuk.app.visited.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import uk.govuk.app.design.ui.component.BodyBoldLabel
import uk.govuk.app.design.ui.component.BodyRegularLabel
import uk.govuk.app.design.ui.component.ChildPageHeader
import uk.govuk.app.design.ui.component.ExtraLargeVerticalSpacer
import uk.govuk.app.design.ui.component.LargeVerticalSpacer
import uk.govuk.app.design.ui.component.ListDivider
import uk.govuk.app.design.ui.component.SubheadlineRegularLabel
import uk.govuk.app.design.ui.theme.GovUkTheme
import uk.govuk.app.visited.R
import uk.govuk.app.visited.VisitedViewModel
import uk.govuk.app.visited.data.capitaliseMonth
import uk.govuk.app.visited.ui.model.VisitedUi

@Composable
internal fun VisitedRoute(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: VisitedViewModel = hiltViewModel()

    VisitedScreen(
        viewModel = viewModel,
        onPageView = { viewModel.onPageView() },
        onBack = onBack,
        modifier = modifier
    )
}

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
private fun VisitedScreen(
    viewModel: VisitedViewModel,
    onPageView: () -> Unit,
    onBack: () -> Unit,
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
        Column(
            Modifier
                .verticalScroll(rememberScrollState())
                .padding(
                    top = GovUkTheme.spacing.small,
                    bottom = GovUkTheme.spacing.extraLarge
                )
        ) {
            val visitedItems = viewModel.uiState.value?.visited

            if (visitedItems != null) {
                if (visitedItems.isEmpty()) {
                    NoVisitedItems(modifier)
                } else {
                    ShowVisitedItems(visitedItems, viewModel, modifier)
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
    items: Map<String, List<VisitedUi>>?,
    viewModel: VisitedViewModel,
    modifier: Modifier = Modifier
) {
    val lastVisitedText = stringResource(R.string.visited_items_last_visited)

    items?.forEach { (sectionTitle, visitedItems) ->
        if (visitedItems.isNotEmpty()) {
            BodyBoldLabel(
                capitaliseMonth(sectionTitle),
                modifier = Modifier.padding(start = GovUkTheme.spacing.large)
            )

            visitedItems?.forEach { item ->
                val title = item.title
                val lastVisited = item.lastVisited
                val url = item.url
                val context = LocalContext.current
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(url)

                Row(
                    modifier.padding(GovUkTheme.spacing.medium)
                        .clickable(
                            onClick = {
                                viewModel.onVisitedItemClicked(title, url)
                                context.startActivity(intent)
                            }
                        ),
                    verticalAlignment = Alignment.Top
                ) {
                    BodyRegularLabel(
                        text = title,
                        modifier = Modifier.weight(1f),
                        color = GovUkTheme.colourScheme.textAndIcons.link,
                    )

                    Icon(
                        painter = painterResource(
                            uk.govuk.app.design.R.drawable.baseline_open_in_new_24
                        ),
                        contentDescription = stringResource(
                            uk.govuk.app.design.R.string.opens_in_web_browser
                        ),
                        tint = GovUkTheme.colourScheme.textAndIcons.link
                    )
                }

                SubheadlineRegularLabel(
                    text = "$lastVisitedText $lastVisited",
                    modifier = modifier.padding(
                        GovUkTheme.spacing.medium,
                        0.dp,
                        GovUkTheme.spacing.medium,
                        GovUkTheme.spacing.medium
                    )
                )

                ListDivider(modifier)
                LargeVerticalSpacer()
            }
        }
    }
}
