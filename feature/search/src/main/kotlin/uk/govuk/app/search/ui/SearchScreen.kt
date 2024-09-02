package uk.govuk.app.search.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.delay
import uk.govuk.app.design.ui.component.SearchHeader
import uk.govuk.app.search.R
import uk.govuk.app.search.SearchViewModel

@Composable
internal fun SearchRoute(
    onBack: () -> Unit,
    onSearch: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: SearchViewModel = hiltViewModel()

    SearchScreen(
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
    onPageView: () -> Unit,
    onBack: () -> Unit,
    onSearch: (String) -> Unit,
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
           placeholder = stringResource(R.string.search_placeholder),
           focusRequester = focusRequester
       )
    }

    LaunchedEffect(focusRequester) {
        focusRequester.requestFocus()
        delay(100)
        keyboard?.show()
    }
}




