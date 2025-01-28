package uk.govuk.app.search.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import uk.govuk.app.design.ui.component.BodyRegularLabel
import uk.govuk.app.design.ui.component.GovUkCard
import uk.govuk.app.design.ui.component.MediumVerticalSpacer
import uk.govuk.app.design.ui.component.SmallVerticalSpacer
import uk.govuk.app.design.ui.component.Title3BoldLabel
import uk.govuk.app.design.ui.theme.GovUkTheme
import uk.govuk.app.search.R
import uk.govuk.app.search.data.remote.model.SearchResult
import uk.govuk.app.search.domain.StringUtils

@Composable
internal fun SearchResults(
    searchTerm: String,
    searchResults: List<SearchResult>,
    onClick: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    val focusRequester = remember { FocusRequester() }

    var previousSearchTerm by rememberSaveable { mutableStateOf("") }

    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        state = listState
    ) {
        item {
            Header(focusRequester)
        }
        items(searchResults) { searchResult ->
            SearchResult(searchResult, onClick)
        }
        item {
            MediumVerticalSpacer()
        }
    }

    LaunchedEffect(searchTerm) {
        // We only want to trigger scroll and focus if we have a new search (rather than orientation change)
        if (searchTerm != previousSearchTerm) {
            listState.animateScrollToItem(0)
            focusRequester.requestFocus()
            previousSearchTerm = searchTerm
        }
    }
}

@Composable
private fun Header(
    focusRequester: FocusRequester,
    modifier: Modifier = Modifier
) {
    Title3BoldLabel(
        text = stringResource(R.string.search_results_heading),
        modifier = modifier
            .padding(horizontal = GovUkTheme.spacing.extraLarge,)
            .padding(top = GovUkTheme.spacing.medium)
            .focusRequester(focusRequester)
            .focusable()
            .semantics { heading() }
    )
}

@Composable
private fun SearchResult(
    searchResult: SearchResult,
    onClick: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val title = StringUtils.collapseWhitespace(searchResult.title)
    val url = StringUtils.buildFullUrl(searchResult.link)

    val context = LocalContext.current
    val intent = Intent(Intent.ACTION_VIEW)
    intent.data = Uri.parse(url)

    GovUkCard(
        modifier = modifier.padding(
            GovUkTheme.spacing.medium,
            GovUkTheme.spacing.medium,
            GovUkTheme.spacing.medium,
            0.dp
        ),
        onClick = {
            onClick(title, url)
            context.startActivity(intent)
        }
    ) {
        Row(
            verticalAlignment = Alignment.Top
        ) {
            BodyRegularLabel(
                text = title,
                modifier = Modifier.weight(1f),
                color = GovUkTheme.colourScheme.textAndIcons.link,
            )

            Icon(
                painter = painterResource(
                    uk.govuk.app.design.R.drawable.ic_external_link
                ),
                contentDescription = stringResource(
                    uk.govuk.app.design.R.string.opens_in_web_browser
                ),
                tint = GovUkTheme.colourScheme.textAndIcons.link,
                modifier = Modifier.padding(start = GovUkTheme.spacing.medium)
            )
        }

        val description = searchResult.description
        if (!description.isNullOrBlank()) {
            SmallVerticalSpacer()
            BodyRegularLabel(StringUtils.collapseWhitespace(description))
        }
    }
}