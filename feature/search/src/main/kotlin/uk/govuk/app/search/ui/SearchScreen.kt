package uk.govuk.app.search.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedCard
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.delay
import uk.govuk.app.design.ui.component.BodyBoldLabel
import uk.govuk.app.design.ui.component.BodyRegularLabel
import uk.govuk.app.design.ui.component.SearchHeader
import uk.govuk.app.design.ui.theme.GovUkTheme
import uk.govuk.app.search.R
import uk.govuk.app.search.SearchViewModel
import uk.govuk.app.search.api.SearchConfig
import uk.govuk.app.search.api_result.Result
import uk.govuk.app.search.api_result.ResultStatus

@Composable
internal fun SearchRoute(
    onBack: () -> Unit,
    onSearch: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: SearchViewModel = hiltViewModel()

    SearchScreen(
        viewModel = viewModel,
        onPageView = { viewModel.onPageView() },
        onBack = onBack,
        onSearch = { searchTerm ->
            viewModel.onSearch(searchTerm)
            onSearch(searchTerm)
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
    modifier: Modifier = Modifier
) {
    val searchResults by viewModel.searchResults.observeAsState(emptyList())
    val resultStatus by viewModel.resultStatus.observeAsState()

    LaunchedEffect(Unit) {
        onPageView()
    }

    val focusRequester = remember { FocusRequester() }
    val keyboard = LocalSoftwareKeyboardController.current
    val altText: String = stringResource(
        id = uk.govuk.app.design.R.string.opens_in_web_browser
    )

    Column(modifier) {
       SearchHeader(
           onBack = onBack,
           onSearch = onSearch,
           placeholder = stringResource(R.string.search_placeholder),
           focusRequester = focusRequester
       )
    }

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(58.dp))

        when (resultStatus) {
            ResultStatus.SUCCESS ->
                ShowResults(searchResults, altText)
            ResultStatus.NO_RESULTS_FOUND ->
                NoResultsFound(searchTerm = viewModel.searchTerm)
            ResultStatus.DEVICE_OFFLINE ->
                DeviceIsOffline()
            ResultStatus.SERVICE_NOT_RESPONDING ->
                ServiceNotResponding(altText)
            null ->
                ShowNothing()
        }
    }

    LaunchedEffect(focusRequester) {
        focusRequester.requestFocus()
        delay(100)
        keyboard?.show()
    }
}

@Composable
fun ShowResults(searchResults: List<Result>, altText: String) {
    val viewModel: SearchViewModel = hiltViewModel()

    Column(
        modifier = Modifier.padding(bottom = GovUkTheme.spacing.medium)
                .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LazyColumn {
            items(searchResults) { searchResult ->
                val title = removeNewlines(searchResult.title)
                val description = removeNewlines(searchResult.description)
                val url = pathOrFullUrl(searchResult.link)

                val context = LocalContext.current
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(url)

                OutlinedCard(
                    colors = CardDefaults.cardColors(
                        containerColor = GovUkTheme.colourScheme.surfaces.card
                    ),
                    modifier = Modifier.padding(
                            GovUkTheme.spacing.medium,
                            GovUkTheme.spacing.medium,
                            GovUkTheme.spacing.medium,
                            0.dp
                        )
                        .fillMaxWidth()
                        .clickable(
                            onClick = {
                                viewModel.onSearchResultClicked(title, url)
                                context.startActivity(intent)
                            }
                        )
                        .semantics { contentDescription = altText },
                ) {
                    Row(
                        Modifier.padding(
                            GovUkTheme.spacing.medium,
                            GovUkTheme.spacing.medium,
                            GovUkTheme.spacing.medium,
                            GovUkTheme.spacing.small
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

                    BodyRegularLabel(
                        text = description,
                        modifier = Modifier.padding(
                            GovUkTheme.spacing.medium,
                            0.dp,
                            GovUkTheme.spacing.medium,
                            GovUkTheme.spacing.medium
                        )
                    )
                }
            }
        }
    }
}

private fun pathOrFullUrl(path: String): String {
    return if (path.startsWith("http")) path else "${SearchConfig.GOV_UK_URL}$path"
}

private fun removeNewlines(string: String): String {
    return string.replace("\n", "")
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
fun DeviceIsOffline() {
    Row(
        Modifier.padding(
            GovUkTheme.spacing.medium,
            GovUkTheme.spacing.large,
            GovUkTheme.spacing.medium,
            0.dp
        ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        BodyBoldLabel(
            text = stringResource(R.string.search_device_offline),
            modifier = Modifier.align(Alignment.CenterVertically)
        )
    }

    Row(
        Modifier.padding(
            GovUkTheme.spacing.medium,
            GovUkTheme.spacing.small,
            GovUkTheme.spacing.medium,
            GovUkTheme.spacing.medium
        ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        BodyRegularLabel(
            text = stringResource(R.string.search_check_connection),
            modifier = Modifier.align(Alignment.CenterVertically)
        )
    }
}

@Composable
fun ServiceNotResponding(altText: String) {
    val context = LocalContext.current
    val intent = Intent(Intent.ACTION_VIEW)
    intent.data = Uri.parse(SearchConfig.GOV_UK_URL)

    Row(
        Modifier.padding(
            GovUkTheme.spacing.medium,
            GovUkTheme.spacing.large,
            GovUkTheme.spacing.medium,
            0.dp
        ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        BodyBoldLabel(
            text = stringResource(R.string.search_service_problem),
            modifier = Modifier.align(Alignment.CenterVertically),
        )
    }

    Row(
        Modifier.padding(
            GovUkTheme.spacing.medium,
            GovUkTheme.spacing.small,
            GovUkTheme.spacing.medium,
            GovUkTheme.spacing.medium
        ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        BodyRegularLabel(
            text = stringResource(R.string.search_not_working),
            textAlign = TextAlign.Center,
            modifier = Modifier.align(Alignment.CenterVertically)
        )
    }

    Row(
        Modifier.padding(GovUkTheme.spacing.medium)
            .clickable(onClick =  { context.startActivity(intent) })
            .semantics { contentDescription = altText },
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.Center
    ) {
        BodyRegularLabel(
            text = stringResource(R.string.search_on_website),
            modifier = Modifier.align(Alignment.CenterVertically),
            color = GovUkTheme.colourScheme.textAndIcons.link,
        )

        Icon(
            painter = painterResource(
                uk.govuk.app.design.R.drawable.baseline_open_in_new_24
            ),
            contentDescription = stringResource(
                uk.govuk.app.design.R.string.opens_in_web_browser
            ),
            tint = GovUkTheme.colourScheme.textAndIcons.link,
            modifier = Modifier.padding(start = GovUkTheme.spacing.small)
        )
    }
}

@Composable
fun ShowNothing() {
    // does nothing on purpose as this is shown before
    // the user actually searches or when an unknown
    // error occurs.
}
