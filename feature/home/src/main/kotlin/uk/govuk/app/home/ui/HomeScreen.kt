package uk.govuk.app.home.ui

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
internal fun HomeRoute() {
    // Collect UI state from view model here and pass to screen (if necessary)
    HomeScreen()
}

@Composable
private fun HomeScreen() {
    Text("Home Screen")
}