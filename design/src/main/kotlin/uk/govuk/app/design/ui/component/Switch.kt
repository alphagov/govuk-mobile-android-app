package uk.govuk.app.design.ui.component

import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.tooling.preview.Preview
import uk.govuk.app.design.ui.theme.GovUkTheme

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ToggleSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    testDescription: String,
    modifier: Modifier = Modifier,
) {
    Switch(
        checked = checked,
        onCheckedChange = {
            onCheckedChange(it)
        },
        colors = SwitchDefaults.colors(
            checkedThumbColor = GovUkTheme.colourScheme.surfaces.toggleHandle,
            checkedTrackColor = GovUkTheme.colourScheme.surfaces.toggleEnabled,
            checkedBorderColor = GovUkTheme.colourScheme.surfaces.toggleBorder,
            uncheckedThumbColor = GovUkTheme.colourScheme.surfaces.toggleHandle,
            uncheckedTrackColor = GovUkTheme.colourScheme.surfaces.toggleDisabled,
            uncheckedBorderColor = GovUkTheme.colourScheme.surfaces.toggleBorder,
        ),
        modifier = modifier.semantics {
            testTagsAsResourceId = true
        }.testTag("toggle $testDescription")
    )
}

@Preview(showBackground = true)
@Composable
private fun ToggleSwitchPreview() {
    GovUkTheme {
        ToggleSwitch(
            checked = true,
            onCheckedChange = {},
            testDescription = "toggle"
        )
    }
}
