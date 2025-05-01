package uk.govuk.app.local.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import uk.gov.govuk.design.ui.component.BodyRegularLabel
import uk.gov.govuk.design.ui.component.MediumVerticalSpacer
import uk.gov.govuk.design.ui.theme.GovUkTheme

@Composable
fun RadioButtonGroup(radioOptions: List<String>, modifier: Modifier = Modifier) {
    val (selectedOption, onOptionSelected) = remember { mutableStateOf(radioOptions[0]) }

    Column(modifier.selectableGroup()) {
        radioOptions.forEachIndexed { index, text ->
            MediumVerticalSpacer()

            Row(
                Modifier
                    .fillMaxWidth()
                    .selectable(
                        selected = (text == selectedOption),
                        onClick = { onOptionSelected(text) },
                        role = Role.RadioButton
                    ),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                RadioButton(
                    selected = (text == selectedOption),
                    onClick = null,
                    colors = RadioButtonColors(
                        selectedColor = GovUkTheme.colourScheme.surfaces.radioSelected,
                        unselectedColor = GovUkTheme.colourScheme.surfaces.radioUnselected,
                        disabledSelectedColor = GovUkTheme.colourScheme.surfaces.radioUnselected,
                        disabledUnselectedColor = GovUkTheme.colourScheme.surfaces.radioUnselected
                    )
                )
                BodyRegularLabel(
                    text = text,
                    modifier = Modifier.padding(horizontal = GovUkTheme.spacing.medium)
                )
            }

            MediumVerticalSpacer()

            if (index < radioOptions.lastIndex) {
                RadioDivider()
            }
        }
    }
}


@Composable
private fun RadioDivider(modifier: Modifier = Modifier) {
    HorizontalDivider(
        modifier = modifier,
        thickness = 1.dp,
        color = GovUkTheme.colourScheme.strokes.radioDivider
    )
}

@Preview
@Composable
private fun RadioButtonGroupPreview() {
    GovUkTheme {
        RadioButtonGroup(
            listOf(
                "Bournemouth, Christchurch and Poole Council",
                "Dorset Council"
            )
        )
    }
}
