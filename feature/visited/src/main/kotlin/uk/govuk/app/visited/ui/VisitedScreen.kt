package uk.govuk.app.visited.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import uk.govuk.app.design.ui.component.BodyBoldLabel
import uk.govuk.app.design.ui.component.BodyRegularLabel
import uk.govuk.app.design.ui.component.ChildPageHeader
import uk.govuk.app.design.ui.component.ExtraLargeVerticalSpacer
import uk.govuk.app.design.ui.theme.GovUkTheme
import uk.govuk.app.visited.R
import uk.govuk.app.visited.VisitedViewModel

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
                    /* TODO: display the visited items */
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
