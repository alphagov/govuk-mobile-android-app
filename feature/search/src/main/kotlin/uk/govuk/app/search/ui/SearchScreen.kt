package uk.govuk.app.search.ui

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
internal fun SearchRoute(
    modifier: Modifier = Modifier
) {
    SearchScreen(modifier)
}

@Composable
private fun SearchScreen(
    modifier: Modifier = Modifier
) {
    Text(
        text = "Search!!!",
        modifier = modifier
    )
}