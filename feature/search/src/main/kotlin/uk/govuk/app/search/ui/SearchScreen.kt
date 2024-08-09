package uk.govuk.app.search.ui

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
internal fun SearchRoute() {
    SearchScreen()
}

@Composable
private fun SearchScreen(
    modifier: Modifier = Modifier
) {
    Text(
        "Search Screen!",
        modifier = modifier
    )
}