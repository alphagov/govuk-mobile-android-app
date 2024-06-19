package uk.govuk.app.settings.ui

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
internal fun SettingsSubRoute() {
    // Collect UI state from view model here and pass to screen (if necessary)
    SettingsSubScreen()
}

@Composable
private fun SettingsSubScreen() {
    Text("Settings Sub Screen")
}