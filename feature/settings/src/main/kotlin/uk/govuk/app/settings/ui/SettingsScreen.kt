package uk.govuk.app.settings.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
internal fun SettingsRoute(onButtonClick: () -> Unit) {
    // Collect UI state from view model here and pass to screen (if necessary)
    SettingsScreen(onButtonClick)
}

@Composable
private fun SettingsScreen(onButtonClick: () -> Unit) {
    Column {
        Text("Settings Screen")
        Button(onClick = onButtonClick) {
            Text("Click me!")
        }
    }
}