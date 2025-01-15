package uk.govuk.app.search.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.TextButton
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
import uk.govuk.app.design.ui.component.BodyBoldLabel
import uk.govuk.app.design.ui.component.BodyRegularLabel
import uk.govuk.app.design.ui.component.ExtraSmallHorizontalSpacer
import uk.govuk.app.design.ui.component.GovUkCard
import uk.govuk.app.design.ui.component.ListDivider
import uk.govuk.app.design.ui.component.MediumVerticalSpacer
import uk.govuk.app.design.ui.component.SmallHorizontalSpacer
import uk.govuk.app.design.ui.component.SmallVerticalSpacer
import uk.govuk.app.design.ui.component.Title3BoldLabel
import uk.govuk.app.design.ui.theme.GovUkTheme
import uk.govuk.app.networking.ui.component.OfflineMessage
import uk.govuk.app.networking.ui.component.ProblemMessage
import uk.govuk.app.search.R
import uk.govuk.app.search.SearchUiState
import uk.govuk.app.search.SearchUiState.Error
import uk.govuk.app.search.SearchViewModel
import uk.govuk.app.search.data.remote.model.Result
import uk.govuk.app.search.domain.StringUtils
import uk.govuk.app.search.ui.component.SearchHeader

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
        onRemoveAllPreviousSearches = {
            viewModel.onRemoveAllPreviousSearches()
        },
        onRemovePreviousSearch = { searchTerm ->
            viewModel.onRemovePreviousSearch(searchTerm)
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
    onRemoveAllPreviousSearches: () -> Unit,
    onRemovePreviousSearch: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(Unit) {
        onPageView()
    }

    val focusRequester = remember { FocusRequester() }
    val keyboard = LocalSoftwareKeyboardController.current

    var searchTerm by remember { mutableStateOf("") }

    Column(modifier) {
        SearchHeader(
            onBack = onBack,
            searchTerm = searchTerm,
            placeholder = stringResource(R.string.search_placeholder),
            onSearchTermChange = {
                searchTerm = it
                if (searchTerm.isBlank()) {
                    onClear()
                }
            },
            onSearch = { onSearch(searchTerm) },
            onClear = onClear,
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
                    ShowPreviousSearches(
                        previousSearches = it.previousSearches,
                        onClick = {
                            searchTerm = it
                            onSearch(it)
                        },
                        onRemoveAll = onRemoveAllPreviousSearches,
                        onRemove = onRemovePreviousSearch
                    )
                is SearchUiState.Results ->
                    ShowResults(it.searchTerm, it.searchResults, onResultClick)
                else -> ShowError(uiState as Error, onRetry)
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
private fun ShowPreviousSearches(
    previousSearches: List<String>,
    onClick: (String) -> Unit,
    onRemoveAll: () -> Unit,
    onRemove: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (previousSearches.isNotEmpty()) {
        var showDialog by remember { mutableStateOf(false) }

        LazyColumn(
            modifier
                .fillMaxWidth()
                .padding(horizontal = GovUkTheme.spacing.medium)
        ) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            top = GovUkTheme.spacing.small,
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BodyBoldLabel(
                        text = stringResource(R.string.previous_searches_heading),
                        modifier = Modifier
                            .weight(1f)
                            .semantics { heading() }
                    )

                    SmallHorizontalSpacer()

                    TextButton(
                        onClick = { showDialog = true }
                    ) {
                        BodyRegularLabel(
                            text = stringResource(R.string.remove_all_button),
                            color = GovUkTheme.colourScheme.textAndIcons.link,
                        )
                    }
                }
            }
            items(previousSearches) { searchTerm ->
                Column{
                    ListDivider()
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onClick(searchTerm) },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = GovUkTheme.colourScheme.textAndIcons.secondary
                        )
                        ExtraSmallHorizontalSpacer()
                        BodyRegularLabel(
                            text = searchTerm,
                            modifier = Modifier.weight(1f)
                        )
                        ExtraSmallHorizontalSpacer()
                        TextButton(
                            onClick = { onRemove(searchTerm) }
                        ) {
                                Icon(
                                    imageVector = Icons.Filled.Clear,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp),
                                    tint = GovUkTheme.colourScheme.textAndIcons.trailingIcon
                                )
                        }
                    }
                }
            }
            item {
                ListDivider()
            }
        }

        if (showDialog) {
            ShowRemoveAllConfirmationDialog(
                onConfirm = onRemoveAll,
                onDismiss = { showDialog = false }
            )
        }
    }
}

@Composable
private fun ShowRemoveAllConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = onConfirm
            ) {
                BodyBoldLabel(
                    text = stringResource(R.string.remove_confirmation_dialog_button),
                    color = GovUkTheme.colourScheme.textAndIcons.buttonRemove
                )
            }
        },
        modifier = modifier,
        title = {
            BodyBoldLabel(stringResource(R.string.remove_confirmation_dialog_title))
        },
        text = {
            BodyBoldLabel(
                text = stringResource(R.string.remove_confirmation_dialog_message),
                color = GovUkTheme.colourScheme.textAndIcons.secondary
            )
        }
    )
}

@Composable
private fun ShowResults(
    searchTerm: String,
    searchResults: List<Result>,
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
    focusRequester: FocusRequester,
    modifier: Modifier = Modifier
) {
    Row(
        modifier.padding(
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
    error: Error,
    onRetry: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusRequester = remember { FocusRequester() }

    Column(modifier.verticalScroll(rememberScrollState())) {
        when (error) {
            is Error.Empty ->
                NoResultsFound(
                    searchTerm = error.searchTerm,
                    focusRequester = focusRequester
                )
            is Error.Offline ->
                OfflineMessage(
                    onButtonClick = { onRetry(error.searchTerm) },
                    focusRequester = focusRequester
                )
            is Error.ServiceError ->
                ProblemMessage(
                    focusRequester = focusRequester
                )
            else -> { } // Do nothing
        }
    }

    LaunchedEffect(error.uuid) {
        focusRequester.requestFocus()
    }
}

@Composable
private fun ShowNothing() {
    // does nothing on purpose as this is shown before
    // the user actually searches or when an unknown
    // error occurs.
}
