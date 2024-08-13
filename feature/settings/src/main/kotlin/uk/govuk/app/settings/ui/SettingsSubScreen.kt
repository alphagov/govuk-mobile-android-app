package uk.govuk.app.settings.ui

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
internal fun SettingsSubRoute(
    modifier: Modifier = Modifier
) {
    // Collect UI state from view model here and pass to screen (if necessary)
    SettingsSubScreen(modifier)
}

@Composable
private fun SettingsSubScreen(
    modifier: Modifier = Modifier
) {
    Text(
        text = "Settings Sub Screen",
        modifier = modifier
    )
}