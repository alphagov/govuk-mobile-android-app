package uk.govuk.app.search.ui.widget

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun SearchWidget(
    onClick: () -> Unit
) {
    Button(onClick = onClick) {
        Text("Search for some stuff!")
    }
}