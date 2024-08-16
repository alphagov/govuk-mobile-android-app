package uk.govuk.app.search.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import kotlinx.coroutines.delay
import uk.govuk.app.design.ui.component.SearchHeader

@Composable
internal fun SearchRoute(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    SearchScreen(
        onBack = onBack,
        modifier = modifier
    )
}

@Composable
private fun SearchScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val focusRequester = remember { FocusRequester() }
    val keyboard = LocalSoftwareKeyboardController.current

    var searchQuery by rememberSaveable {
        mutableStateOf("")
    }

    Column(modifier) {
       SearchHeader(
           onBack = onBack,
           searchQuery = searchQuery,
           onSearchQueryChange = { searchQuery = it },
           placeholder = "Search", // Todo - extract string
           focusRequester = focusRequester
       )
    }

    LaunchedEffect(focusRequester) {
        focusRequester.requestFocus()
        delay(100)
        keyboard?.show()
    }
}




