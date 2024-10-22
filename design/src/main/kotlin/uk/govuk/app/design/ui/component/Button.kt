package uk.govuk.app.design.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults.buttonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import uk.govuk.app.design.R
import uk.govuk.app.design.ui.theme.GovUkTheme

data class GovUkButtonColours(
    val defaultContainerColour: Color,
    val defaultContentColour: Color,
    val defaultBorderColour: Color? = null,
    val focussedContainerColour: Color,
    val focussedContentColour: Color,
    val focussedBorderColour: Color? = null,
    val pressedContainerColour: Color,
    val pressedContentColour: Color,
    val pressedBorderColour: Color? = null,
    val disabledContainerColour: Color,
    val disabledContentColour: Color,
    val disabledBorderColour: Color? = null
)

@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    externalLink: Boolean = false
) {
    val colours = GovUkButtonColours(
        defaultContainerColour = GovUkTheme.colourScheme.surfaces.buttonPrimary,
        defaultContentColour = GovUkTheme.colourScheme.textAndIcons.buttonPrimary,
        focussedContainerColour = GovUkTheme.colourScheme.surfaces.buttonPrimaryFocused,
        focussedContentColour = GovUkTheme.colourScheme.textAndIcons.buttonPrimaryFocused,
        pressedContainerColour = GovUkTheme.colourScheme.surfaces.buttonPrimaryHighlight,
        pressedContentColour = GovUkTheme.colourScheme.textAndIcons.buttonPrimaryHighlight,
        disabledContainerColour = GovUkTheme.colourScheme.surfaces.buttonPrimaryDisabled,
        disabledContentColour = GovUkTheme.colourScheme.textAndIcons.buttonPrimaryDisabled
    )

    BaseButton(
        text = text,
        onClick = onClick,
        colours = colours,
        textStyle = GovUkTheme.typography.bodyBold,
        modifier = modifier.fillMaxWidth(),
        enabled = enabled,
        externalLink = externalLink
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
    val colours = GovUkButtonColours(
        defaultContainerColour = GovUkTheme.colourScheme.surfaces.buttonSecondary,
        defaultContentColour = GovUkTheme.colourScheme.textAndIcons.buttonSecondary,
        focussedContainerColour = GovUkTheme.colourScheme.surfaces.buttonSecondaryFocused,
        focussedContentColour = GovUkTheme.colourScheme.textAndIcons.buttonSecondaryFocused,
        pressedContainerColour = GovUkTheme.colourScheme.surfaces.buttonSecondaryHighlight,
        pressedContentColour = GovUkTheme.colourScheme.textAndIcons.buttonSecondaryHighlight,
        disabledContainerColour = GovUkTheme.colourScheme.surfaces.buttonSecondaryDisabled,
        disabledContentColour = GovUkTheme.colourScheme.textAndIcons.buttonSecondaryDisabled
    )

    BaseButton(
        text = text,
        onClick = onClick,
        colours = colours,
        textStyle = GovUkTheme.typography.bodyRegular,
        modifier = modifier.fillMaxWidth(),
        enabled = enabled,
        externalLink = externalLink,
        shape = RoundedCornerShape(4.dp)
    )
}

@Composable
fun CompactButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    externalLink: Boolean = false
) {
    val colours = GovUkButtonColours(
        defaultContainerColour = GovUkTheme.colourScheme.surfaces.buttonCompact,
        defaultContentColour = GovUkTheme.colourScheme.textAndIcons.buttonCompact,
        defaultBorderColour = GovUkTheme.colourScheme.strokes.buttonCompactBorder,
        focussedContainerColour = GovUkTheme.colourScheme.surfaces.buttonCompactFocused,
        focussedContentColour = GovUkTheme.colourScheme.textAndIcons.buttonCompactFocused,
        pressedContainerColour = GovUkTheme.colourScheme.surfaces.buttonCompactHighlight,
        pressedContentColour = GovUkTheme.colourScheme.textAndIcons.buttonCompactHighlight,
        pressedBorderColour = GovUkTheme.colourScheme.strokes.buttonCompactBorder,
        disabledContainerColour = GovUkTheme.colourScheme.surfaces.buttonCompactDisabled,
        disabledContentColour = GovUkTheme.colourScheme.textAndIcons.buttonCompactDisabled
    )

    BaseButton(
        text = text,
        onClick = onClick,
        colours = colours,
        textStyle = GovUkTheme.typography.bodyRegular,
        modifier = modifier,
        enabled = enabled,
        externalLink = externalLink
    )
}

@Composable
private fun BaseButton(
    text: String,
    onClick: () -> Unit,
    colours: GovUkButtonColours,
    textStyle: TextStyle,
    modifier: Modifier = Modifier,
    externalLink: Boolean = false,
    enabled: Boolean = true,
    shape: RoundedCornerShape = RoundedCornerShape(30.dp),
) {
    val interactionSource = remember { MutableInteractionSource() }
    val focusRequester = FocusRequester()
    val isFocused by interactionSource.collectIsFocusedAsState()
    val isPressed by interactionSource.collectIsPressedAsState()
    val isHovered by interactionSource.collectIsHoveredAsState()

    var stateMappedColours = buttonColors(
        containerColor = colours.defaultContainerColour,
        contentColor = colours.defaultContentColour,
        disabledContainerColor = colours.disabledContainerColour,
        disabledContentColor = colours.disabledContentColour,
    )

    stateMappedColours = when {
        isFocused -> stateMappedColours.copy(
            containerColor = colours.focussedContainerColour,
            contentColor = colours.focussedContentColour,
        )
        isPressed || isHovered -> stateMappedColours.copy(
            containerColor = colours.pressedContainerColour,
            contentColor = colours.pressedContentColour
        )
        else -> stateMappedColours
    }

    val borderColour = if (enabled) {
        when {
            isFocused -> colours.focussedBorderColour
            isPressed || isHovered -> colours.pressedBorderColour
            else -> colours.defaultBorderColour
        }
    } else {
        colours.disabledBorderColour
    }

    Button(
        onClick = onClick,
        modifier = modifier
            .padding(GovUkTheme.spacing.small)
            .focusRequester(focusRequester)
            .focusable(interactionSource = interactionSource),
        enabled = enabled,
        shape = shape,
        colors = stateMappedColours,
        border = borderColour?.let { BorderStroke(1.dp, it) },
        interactionSource = interactionSource
    ) {
        Text(text = text, style = textStyle)
        if (externalLink) ExternalLinkIcon()
    }
}

@Composable
private fun ExternalLinkIcon() {
    Icon(
        painter = painterResource(id = R.drawable.ic_external_link),
        contentDescription = stringResource(R.string.opens_in_web_browser),
        modifier = Modifier
            .padding(start = GovUkTheme.spacing.small)
            .testTag("openInNewTabIcon")
    )
}

@Composable
fun VerticalButtonGroup(
    primaryText: String,
    onPrimary: () -> Unit,
    secondaryText: String,
    onSecondary: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier) {
        PrimaryButton(
            text = primaryText,
            onClick = onPrimary,
            modifier = Modifier.fillMaxWidth()
        )
        SecondaryButton(
            text = secondaryText,
            onClick = onSecondary,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun HorizontalButtonGroup(
    primaryText: String,
    onPrimary: () -> Unit,
    secondaryText: String,
    onSecondary: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        PrimaryButton(
            text = primaryText,
            onClick = onPrimary,
            modifier = Modifier.weight(0.5f)
        )
        SecondaryButton(
            text = secondaryText,
            onClick = onSecondary,
            modifier = Modifier.weight(0.5f)
        )
    }
}

@Preview
@Composable
private fun Primary()
{
    GovUkTheme {
        PrimaryButton(
            text = "Primary button",
            onClick = { }
        )
    }
}

@Preview
@Composable
private fun PrimaryExternalLink()
{
    GovUkTheme {
        PrimaryButton(
            text = "Primary button",
            onClick = { },
            externalLink = true
        )
    }
}

@Preview
@Composable
private fun PrimaryDisabled()
{
    GovUkTheme {
        PrimaryButton(
            text = "Primary button",
            onClick = { },
            enabled = false
        )
    }
}

@Preview
@Composable
private fun Secondary()
{
    GovUkTheme {
        SecondaryButton(
            text = "Secondary button",
            onClick = { }
        )
    }
}

@Preview
@Composable
private fun SecondaryExternalLink()
{
    GovUkTheme {
        SecondaryButton(
            text = "Secondary button",
            onClick = { },
            externalLink = true
        )
    }
}

@Preview
@Composable
private fun SecondaryDisabled()
{
    GovUkTheme {
        SecondaryButton(
            text = "Secondary button",
            onClick = { },
            enabled = false
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun Compact()
{
    GovUkTheme {
        CompactButton(
            text = "Compact button",
            onClick = { }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CompactExternalLink()
{
    GovUkTheme {
        CompactButton(
            text = "Compact button",
            onClick = { },
            externalLink = true
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CompactDisabled()
{
    GovUkTheme {
        CompactButton(
            text = "Compact button",
            onClick = { },
            enabled = false
        )
    }
}

@Preview
@Composable
private fun VerticalButtons()
{
    GovUkTheme {
        VerticalButtonGroup(
            primaryText = "Primary",
            onPrimary = {},
            secondaryText = "Seondary",
            onSecondary = {}
        )
    }
}

@Preview
@Composable
private fun HorizontalButtons()
{
    GovUkTheme {
        HorizontalButtonGroup(
            primaryText = "Primary",
            onPrimary = {},
            secondaryText = "Seondary",
            onSecondary = {}
        )
    }
}
