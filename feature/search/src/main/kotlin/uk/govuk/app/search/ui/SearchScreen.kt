package uk.govuk.app.search.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.delay
import uk.govuk.app.design.ui.component.BodyRegularLabel
import uk.govuk.app.design.ui.component.GovUkCard
import uk.govuk.app.design.ui.component.SmallVerticalSpacer
import uk.govuk.app.design.ui.theme.GovUkTheme
import uk.govuk.app.networking.ui.component.OfflineMessage
import uk.govuk.app.networking.ui.component.ProblemMessage
import uk.govuk.app.search.R
import uk.govuk.app.search.SearchUiState
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
    val keyboardController = LocalSoftwareKeyboardController.current

    SearchScreen(
        viewModel = viewModel,
        onPageView = { viewModel.onPageView() },
        onBack = onBack,
        onSearch = { searchTerm ->
            keyboardController?.hide()
            viewModel.onSearch(searchTerm)
        },
        onClear = {
            viewModel.onClear()
        },
        onClick = { title, url ->
            viewModel.onSearchResultClicked(title, url)
        },
        modifier = modifier
    )
}

@Composable
private fun SearchScreen(
    viewModel: SearchViewModel,
    onPageView: () -> Unit,
    onBack: () -> Unit,
    onSearch: (String) -> Unit,
    onClear: () -> Unit,
    onClick: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

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
                        NoResultsFound(searchTerm = it.searchTerm)
                    } else {
                        ShowResults(it.searchResults, onClick)
                    }
                }
                is SearchUiState.Offline -> OfflineMessage { viewModel.onSearch(it.searchTerm) }
                is SearchUiState.ServiceError -> ProblemMessage()
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
fun ShowResults(searchResults: List<Result>, onClick: (String, String) -> Unit) {
    Column(
        modifier = Modifier
            .padding(bottom = GovUkTheme.spacing.medium)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LazyColumn {
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
                            tint = GovUkTheme.colourScheme.textAndIcons.link
                        )
                    }

                    val description = searchResult.description
                    if (!description.isNullOrBlank()) {
                        SmallVerticalSpacer()
                        BodyRegularLabel(StringUtils.collapseWhitespace(description))
                    }
                }
            }
        }
    }
}

@Composable
fun NoResultsFound(searchTerm: String) {
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
            modifier = Modifier.align(Alignment.CenterVertically),
        )
    }
}

@Composable
fun ShowNothing() {
    // does nothing on purpose as this is shown before
    // the user actually searches or when an unknown
    // error occurs.
}
