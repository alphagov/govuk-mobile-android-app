package uk.govuk.app.search.ui

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.delay
import uk.govuk.app.design.ui.component.MediumVerticalSpacer
import uk.govuk.app.design.ui.theme.GovUkTheme
import uk.govuk.app.search.R
import uk.govuk.app.search.SearchUiState
import uk.govuk.app.search.SearchViewModel
import uk.govuk.app.search.ui.component.SearchField
import uk.govuk.app.search.ui.component.SearchFieldActions

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
            onPreviousSearchClick = { searchTerm ->
                keyboardController?.hide()
                viewModel.onPreviousSearchClick(searchTerm)
            },
            onAutocomplete = { searchTerm ->
                viewModel.onAutocomplete(searchTerm)
            },
            onAutocompleteResultClick = { searchTerm ->
                keyboardController?.hide()
                viewModel.onAutocompleteResultClick(searchTerm)
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
    val onPreviousSearchClick: (String) -> Unit,
    val onAutocompleteResultClick: (String) -> Unit,
)

@Composable
private fun SearchScreen(
    uiState: SearchUiState,
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
        Column(
            Modifier
                .fillMaxWidth()
                .background(GovUkTheme.colourScheme.surfaces.homeHeader)
                .padding(horizontal = GovUkTheme.spacing.medium)
        ) {
            MediumVerticalSpacer()

            val isLogoVisible = LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT

            if (isLogoVisible) {
                Image(
                    painter = painterResource(id = uk.govuk.app.design.R.drawable.logo),
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                )

                MediumVerticalSpacer()
            }

            SearchField(
                searchTerm = searchTerm,
                placeholder = stringResource(R.string.search_placeholder),
                actions = SearchFieldActions(
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

            MediumVerticalSpacer()
        }

        when {
            uiState.error != null ->
                SearchError(uiState.error, actions.onRetry)
            uiState.searchResults != null ->
                SearchResults(
                    searchTerm = uiState.searchResults.searchTerm,
                    searchResults = uiState.searchResults.values,
                    onClick = actions.onResultClick
                )
            uiState.suggestions != null ->
                SearchAutocomplete(
                    searchTerm = uiState.suggestions.searchTerm,
                    suggestions = uiState.suggestions.values,
                    onSearch = {
                        searchTerm = it
                        actions.onAutocompleteResultClick(it)
                    }
                )
            else -> {
                PreviousSearches(
                    previousSearches = uiState.previousSearches,
                    onClick = {
                        searchTerm = it
                        actions.onPreviousSearchClick(it)
                    },
                    onRemoveAll = actions.onRemoveAllPreviousSearches,
                    onRemove = actions.onRemovePreviousSearch
                )
            }
        }
    }

    LaunchedEffect(focusRequester) {
        focusRequester.requestFocus()
        delay(100)
        keyboard?.show()
    }
}
