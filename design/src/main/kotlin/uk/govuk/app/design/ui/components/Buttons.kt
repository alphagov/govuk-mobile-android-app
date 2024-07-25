package uk.govuk.app.design.ui.components

import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import uk.govuk.app.design.ui.theme.GovUkTheme

@Composable
fun TestButton(onClick: () -> Unit) {
    var buttonClicked by remember { mutableStateOf(false) }

    Button(onClick = {
        buttonClicked = true
        onClick()
    }) {
        Text(if (buttonClicked) "Clicked" else "Not Clicked")
    }
}

@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = GovUkTheme.colourScheme.surfaces.buttonPrimary,
            contentColor = GovUkTheme.colourScheme.textAndIcons.buttonPrimary
        )
    ) {
        Text(
            text = text,
            style = GovUkTheme.typography.bodyBold,
        )
    }
}

@Composable
fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    TextButton(
        onClick = onClick,
        modifier = modifier
    ) {
        Text(
            text = text,
            color = GovUkTheme.colourScheme.textAndIcons.link,
            style = GovUkTheme.typography.bodyRegular,
        )
    }
}
