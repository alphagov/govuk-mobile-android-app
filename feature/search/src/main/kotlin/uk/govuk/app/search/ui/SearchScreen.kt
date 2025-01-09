package uk.govuk.app.search.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.delay
import uk.govuk.app.design.ui.component.BodyRegularLabel
import uk.govuk.app.design.ui.component.GovUkCard
import uk.govuk.app.design.ui.component.MediumVerticalSpacer
import uk.govuk.app.design.ui.component.SmallVerticalSpacer
import uk.govuk.app.design.ui.component.Title3BoldLabel
import uk.govuk.app.design.ui.theme.GovUkTheme
import uk.govuk.app.networking.ui.component.OfflineMessage
import uk.govuk.app.networking.ui.component.ProblemMessage
import uk.govuk.app.search.R
import uk.govuk.app.search.SearchUiState
import uk.govuk.app.search.SearchViewModel
import uk.govuk.app.search.data.remote.model.Result
import uk.govuk.app.search.domain.StringUtils
import uk.govuk.app.search.ui.component.SearchHeader
import java.util.UUID

@Composable
internal fun SearchRoute(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: SearchViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()

    val keyboardController = LocalSoftwareKeyboardController.current

    SearchScreen(
        uiState = uiState,
        onPageView = { viewModel.onPageView() },
        onBack = onBack,
        onSearch = { searchTerm ->
            keyboardController?.hide()
            viewModel.onSearch(searchTerm)
        },
        onClear = {
            viewModel.onClear()
        },
        onResultClick = { title, url ->
            viewModel.onSearchResultClicked(title, url)
        },
        onRetry = { searchTerm ->
            viewModel.onSearch(searchTerm)
        },
        modifier = modifier
    )
}

@Composable
private fun SearchScreen(
    uiState: SearchUiState?,
    onPageView: () -> Unit,
    onBack: () -> Unit,
    onSearch: (String) -> Unit,
    onClear: () -> Unit,
    onResultClick: (String, String) -> Unit,
    onRetry: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(Unit) {
        onPageView()
    }

    val focusRequester = remember { FocusRequester() }
    val keyboard = LocalSoftwareKeyboardController.current

    Column(modifier) {
        SearchHeader(
            onBack = onBack,
            onSearch = onSearch,
            onClear = onClear,
            placeholder = stringResource(R.string.search_placeholder),
            focusRequester = focusRequester
        )
    }

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(58.dp))

        uiState?.let {
            when (it) {
                is SearchUiState.Default -> {
                    if (it.searchResults.isEmpty()) {
                        ShowError(it.uuid) { focusRequester ->
                            NoResultsFound(
                                searchTerm = it.searchTerm,
                                focusRequester = focusRequester
                            )
                        }
                    } else {
                        ShowResults(it.searchTerm, it.searchResults, onResultClick)
                    }
                }
                is SearchUiState.Offline -> ShowError(it.uuid) { focusRequester ->
                    OfflineMessage(
                        onButtonClick = { onRetry(it.searchTerm) },
                        focusRequester = focusRequester
                    )
                }
                is SearchUiState.ServiceError -> ShowError(it.uuid) { focusRequester ->
                    ProblemMessage(
                        focusRequester = focusRequester
                    )
                }
            }
        } ?: ShowNothing()
    }

    LaunchedEffect(focusRequester) {
        focusRequester.requestFocus()
        delay(100)
        keyboard?.show()
    }
}

@Composable
private fun ShowResults(
    searchTerm: String,
    searchResults: List<Result>,
    onClick: (String, String) -> Unit
) {
    val listState = rememberLazyListState()
    val focusRequester = remember { FocusRequester() }

    var previousSearchTerm by rememberSaveable { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        state = listState
    ) {
        item {
            Title3BoldLabel(
                text = stringResource(R.string.search_results_heading),
                modifier = Modifier
                    .padding(horizontal = GovUkTheme.spacing.extraLarge,)
                    .padding(top = GovUkTheme.spacing.medium)
                    .focusRequester(focusRequester)
                    .focusable()
                    .semantics { heading() }
            )
        }
        items(searchResults) { searchResult ->
            val title = StringUtils.collapseWhitespace(searchResult.title)
            val url = StringUtils.buildFullUrl(searchResult.link)

            val context = LocalContext.current
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)

            GovUkCard(
                modifier = Modifier.padding(
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
private fun NoResultsFound(
    searchTerm: String,
    focusRequester: FocusRequester
) {
    Row(
        Modifier.padding(
            GovUkTheme.spacing.medium,
            GovUkTheme.spacing.large,
            GovUkTheme.spacing.medium,
            GovUkTheme.spacing.medium
        ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        BodyRegularLabel(
            text = "${stringResource(R.string.search_no_results)} '${searchTerm}'",
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .focusRequester(focusRequester)
                .focusable()
        )
    }
}

@Composable
private fun ShowError(
    uuid: UUID,
    modifier: Modifier = Modifier,
    content: @Composable (FocusRequester) -> Unit
) {
    val focusRequester = remember { FocusRequester() }

    Column(modifier.verticalScroll(rememberScrollState())) {
        content(focusRequester)
    }

    LaunchedEffect(uuid) {
        focusRequester.requestFocus()
    }
}

@Composable
private fun ShowNothing() {
    // does nothing on purpose as this is shown before
    // the user actually searches or when an unknown
    // error occurs.
}
