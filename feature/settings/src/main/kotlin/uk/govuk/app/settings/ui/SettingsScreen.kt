package uk.govuk.app.settings.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import uk.govuk.app.settings.SettingsViewModel

@Composable
internal fun SettingsRoute(onButtonClick: () -> Unit) {
    val viewModel: SettingsViewModel = hiltViewModel()

    SettingsScreen(
        onPageView = { viewModel.onPageView() },
        onButtonClick = onButtonClick
    )
}

@Composable
private fun SettingsScreen(
    onPageView: () -> Unit,
    onButtonClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(Unit) {
        onPageView()
    }

    Column(modifier) {
        Text("Settings Screen")
        Button(onClick = onButtonClick) {
            Text("Click me!")
        }
    }
}