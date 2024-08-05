package uk.govuk.app.design.ui.components

import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults.buttonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import uk.govuk.app.design.R
import uk.govuk.app.design.ui.theme.GovUkTheme

sealed class ButtonState {
    data object Default : ButtonState()
    data object Focused : ButtonState()
    data object Pressed : ButtonState()
    data object Hovered : ButtonState()
}

@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    externalLink: Boolean = false
) {
    BaseButton(
        text = text,
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        externalLink = externalLink,
        primary = true
    )
}

@Composable
fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    externalLink: Boolean = false
) {
    BaseButton(
        text = text,
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        externalLink = externalLink,
        primary = false,
        shape = RoundedCornerShape(4.dp)
    )
}

@Composable
fun BaseButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    externalLink: Boolean = false,
    primary: Boolean = true,
    shape: RoundedCornerShape = RoundedCornerShape(30.dp),
) {
    val interactionSource = remember { MutableInteractionSource() }
    val focusRequester = FocusRequester()
    val isFocused by interactionSource.collectIsFocusedAsState()
    val isPressed by interactionSource.collectIsPressedAsState()
    val isHovered by interactionSource.collectIsHoveredAsState()

    val colors = buttonColors(
        primary,
        buttonState = when {
            isFocused -> ButtonState.Focused
            isPressed -> ButtonState.Pressed
            isHovered -> ButtonState.Hovered
            else -> ButtonState.Default
        }
    )

    Button(
        onClick = onClick,
        enabled = enabled,
        shape = shape,
        interactionSource = interactionSource,
        modifier = modifier
            .fillMaxWidth()
            .padding(GovUkTheme.spacing.small)
            .focusRequester(focusRequester)
            .focusable(interactionSource = interactionSource),
        colors = colors
    ) {
        Text(text = text, style = buttonTextStyle(primary))
        if (externalLink) NewTabIcon()
    }
}

@Composable
fun buttonTextStyle(primary: Boolean): TextStyle {
    return when (primary) {
        true -> GovUkTheme.typography.bodyBold
        false -> GovUkTheme.typography.bodyRegular
    }
}

@Composable
fun buttonColors(primary: Boolean, buttonState: ButtonState): ButtonColors {
    return when (primary) {
        true -> primaryButtonColors(buttonState)
        false -> secondaryButtonColors(buttonState)
    }
}

@Composable
fun primaryButtonColors(buttonState: ButtonState): ButtonColors {
    val defaultColors = buttonColors(
        containerColor = GovUkTheme.colourScheme.surfaces.buttonPrimary,
        contentColor = GovUkTheme.colourScheme.textAndIcons.buttonPrimary,
        disabledContainerColor = GovUkTheme.colourScheme.surfaces.buttonPrimaryDisabled,
        disabledContentColor = GovUkTheme.colourScheme.textAndIcons.buttonPrimaryDisabled,
    )

    return when (buttonState) {
        ButtonState.Focused -> defaultColors.copy(
            containerColor = GovUkTheme.colourScheme.surfaces.buttonPrimaryFocused,
            contentColor = GovUkTheme.colourScheme.textAndIcons.buttonPrimaryFocused,
        )
        ButtonState.Pressed, ButtonState.Hovered -> defaultColors.copy(
            containerColor = GovUkTheme.colourScheme.surfaces.buttonPrimaryHighlight,
            contentColor = GovUkTheme.colourScheme.textAndIcons.buttonPrimaryHighlight
        )
        else -> defaultColors
    }
}

@Composable
fun secondaryButtonColors(buttonState: ButtonState): ButtonColors {
    val defaultColors = buttonColors(
        containerColor = GovUkTheme.colourScheme.surfaces.buttonSecondary,
        contentColor = GovUkTheme.colourScheme.textAndIcons.buttonSecondary,
        disabledContainerColor = GovUkTheme.colourScheme.surfaces.buttonSecondaryDisabled,
        disabledContentColor = GovUkTheme.colourScheme.textAndIcons.buttonSecondaryDisabled,
    )

    return when (buttonState) {
        ButtonState.Focused -> defaultColors.copy(
            containerColor = GovUkTheme.colourScheme.surfaces.buttonSecondaryFocused,
            contentColor = GovUkTheme.colourScheme.textAndIcons.buttonSecondaryFocused
        )
        ButtonState.Pressed, ButtonState.Hovered -> defaultColors.copy(
            containerColor = GovUkTheme.colourScheme.surfaces.buttonSecondaryHighlight,
            contentColor = GovUkTheme.colourScheme.textAndIcons.buttonSecondaryHighlight
        )
        else -> defaultColors
    }
}

@Composable
fun NewTabIcon() {
    Icon(
        painter = painterResource(id = R.drawable.baseline_open_in_new_24),
        contentDescription = "Opens link in a new tab",
        modifier = Modifier.padding(start = GovUkTheme.spacing.small)
            .testTag("openInNewTabIcon")
    )
}
