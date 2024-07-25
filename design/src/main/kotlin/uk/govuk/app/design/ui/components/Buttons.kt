package uk.govuk.app.design.ui.components

import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import uk.govuk.app.design.ui.theme.GovUkTheme

@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        enabled = enabled,
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
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    TextButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
    ) {
        Text(
            text = text,
            color = GovUkTheme.colourScheme.textAndIcons.buttonSecondary,
            style = GovUkTheme.typography.bodyRegular,
        )
    }
}
