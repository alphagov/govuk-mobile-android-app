package uk.govuk.app.settings.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
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
    modifier: Modifier = Modifier,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current
) {
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                onPageView()
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Column(modifier) {
        Text("Settings Screen")
        Button(onClick = onButtonClick) {
            Text("Click me!")
        }
    }
}