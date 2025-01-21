package uk.govuk.app.search.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.delay
import uk.govuk.app.search.R
import uk.govuk.app.search.SearchUiState
import uk.govuk.app.search.SearchUiState.Error
import uk.govuk.app.search.SearchViewModel
import uk.govuk.app.search.ui.component.SearchHeader
import uk.govuk.app.search.ui.component.SearchHeaderActions

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
        actions = SearchScreenActions(
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
            onRemoveAllPreviousSearches = {
                viewModel.onRemoveAllPreviousSearches()
            },
            onRemovePreviousSearch = { searchTerm ->
                viewModel.onRemovePreviousSearch(searchTerm)
            },
            onAutocomplete = { searchTerm ->
                viewModel.onAutocomplete(searchTerm)
            }
        ),
        modifier = modifier
    )
}

private class SearchScreenActions(
    val onPageView: () -> Unit,
    val onBack: () -> Unit,
    val onSearch: (String) -> Unit,
    val onClear: () -> Unit,
    val onResultClick: (String, String) -> Unit,
    val onRetry: (String) -> Unit,
    val onRemoveAllPreviousSearches: () -> Unit,
    val onRemovePreviousSearch: (String) -> Unit,
    val onAutocomplete: (String) -> Unit,
)

@Composable
private fun SearchScreen(
    uiState: SearchUiState?,
    actions: SearchScreenActions,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(Unit) {
        actions.onPageView()
    }

    val focusRequester = remember { FocusRequester() }
    val keyboard = LocalSoftwareKeyboardController.current

    var searchTerm by remember { mutableStateOf("") }

    Column(modifier) {
        SearchHeader(
            searchTerm = searchTerm,
            placeholder = stringResource(R.string.search_placeholder),
            actions = SearchHeaderActions(
                onBack = actions.onBack,
                onSearchTermChange = {
                    searchTerm = it
                    if (searchTerm.isBlank()) {
                        actions.onClear()
                    } else {
                        actions.onAutocomplete(searchTerm)
                    }
                },
                onSearch = { actions.onSearch(searchTerm) },
                onClear = actions.onClear
            ),
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
                is SearchUiState.Default ->
                    PreviousSearches(
                        previousSearches = it.previousSearches,
                        onClick = {
                            searchTerm = it
                            actions.onSearch(it)
                        },
                        onRemoveAll = actions.onRemoveAllPreviousSearches,
                        onRemove = actions.onRemovePreviousSearch
                    )

                is SearchUiState.Autocomplete ->
                    SearchAutocomplete(it.searchTerm, it.suggestions, actions.onSearch)

                is SearchUiState.Results ->
                    SearchResults(it.searchTerm, it.searchResults, actions.onResultClick)

                else -> SearchError(uiState as Error, actions.onRetry)
            }
        }
    }

    LaunchedEffect(focusRequester) {
        focusRequester.requestFocus()
        delay(100)
        keyboard?.show()
    }
}
