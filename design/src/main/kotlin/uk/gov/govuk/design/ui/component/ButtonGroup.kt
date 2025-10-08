package uk.gov.govuk.design.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.window.core.layout.WindowHeightSizeClass
import uk.gov.govuk.design.ui.theme.GovUkTheme

@Composable
fun FixedPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    externalLink: Boolean = false
) {
    Column(modifier.fillMaxWidth()) {
        FixedContainerDivider()
        MediumVerticalSpacer()
        PrimaryButton(
            text = text,
            onClick = onClick,
            modifier = Modifier.padding(horizontal = GovUkTheme.spacing.medium),
            enabled = enabled,
            externalLink = externalLink
        )
        ExtraLargeVerticalSpacer()
    }
}

@Composable
fun FixedSecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    externalLink: Boolean = false
) {
    Column(modifier.fillMaxWidth()) {
        FixedContainerDivider()
        MediumVerticalSpacer()
        SecondaryButton(
            text = text,
            onClick = onClick,
            modifier = Modifier.padding(horizontal = GovUkTheme.spacing.medium),
            enabled = enabled,
            externalLink = externalLink
        )
        ExtraLargeVerticalSpacer()
    }
}

@Composable
fun FixedDoubleButtonGroup(
    primaryText: String,
    onPrimary: () -> Unit,
    secondaryText: String,
    onSecondary: () -> Unit,
    modifier: Modifier = Modifier,
    primaryDestructive: Boolean = false,
    primaryEnabled: Boolean = true,
    secondaryEnabled: Boolean = true,
    windowHeightSizeClass: WindowHeightSizeClass = currentWindowAdaptiveInfo().windowSizeClass.windowHeightSizeClass
) {
    Column(modifier.fillMaxWidth()) {
        FixedContainerDivider()
        MediumVerticalSpacer()
        DoubleButtonGroup(
            primaryText = primaryText,
            onPrimary = onPrimary,
            secondaryText = secondaryText,
            onSecondary = onSecondary,
            primaryDestructive = primaryDestructive,
            primaryEnabled = primaryEnabled,
            secondaryEnabled = secondaryEnabled,
            windowHeightSizeClass = windowHeightSizeClass
        )
        ExtraLargeVerticalSpacer()
    }
}

@Composable
fun DoubleButtonGroup(
    primaryText: String,
    onPrimary: () -> Unit,
    secondaryText: String,
    onSecondary: () -> Unit,
    modifier: Modifier = Modifier,
    primaryDestructive: Boolean = false,
    primaryEnabled: Boolean = true,
    secondaryEnabled: Boolean = true,
    windowHeightSizeClass: WindowHeightSizeClass = currentWindowAdaptiveInfo().windowSizeClass.windowHeightSizeClass
) {
    if (windowHeightSizeClass == WindowHeightSizeClass.COMPACT) {
        HorizontalButtonGroup(
            primaryText = primaryText,
            onPrimary = onPrimary,
            secondaryText = secondaryText,
            onSecondary = onSecondary,
            modifier = modifier.padding(horizontal = GovUkTheme.spacing.medium),
            primaryDestructive = primaryDestructive,
            primaryEnabled = primaryEnabled,
            secondaryEnabled = secondaryEnabled
        )
    } else {
        VerticalButtonGroup(
            primaryText = primaryText,
            onPrimary = onPrimary,
            secondaryText = secondaryText,
            onSecondary = onSecondary,
            modifier = modifier.padding(horizontal = GovUkTheme.spacing.medium),
            primaryDestructive = primaryDestructive,
            primaryEnabled = primaryEnabled,
            secondaryEnabled = secondaryEnabled
        )
    }
}

@Composable
private fun VerticalButtonGroup(
    primaryText: String,
    onPrimary: () -> Unit,
    secondaryText: String,
    onSecondary: () -> Unit,
    modifier: Modifier = Modifier,
    primaryDestructive: Boolean = false,
    primaryEnabled: Boolean = true,
    secondaryEnabled: Boolean = true,
) {
    Column(modifier) {
        if (primaryDestructive) {
            DestructiveButton(
                text = primaryText,
                onClick = onPrimary,
                modifier = Modifier.fillMaxWidth(),
                enabled = primaryEnabled
            )
        } else {
            PrimaryButton(
                text = primaryText,
                onClick = onPrimary,
                modifier = Modifier.fillMaxWidth(),
                enabled = primaryEnabled
            )
        }
        MediumVerticalSpacer()
        SecondaryButton(
            text = secondaryText,
            onClick = onSecondary,
            modifier = Modifier.fillMaxWidth(),
            enabled = secondaryEnabled
        )
    }
}

@Composable
private fun HorizontalButtonGroup(
    primaryText: String,
    onPrimary: () -> Unit,
    secondaryText: String,
    onSecondary: () -> Unit,
    modifier: Modifier = Modifier,
    primaryDestructive: Boolean = false,
    primaryEnabled: Boolean = true,
    secondaryEnabled: Boolean = true,
) {
    Row(modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        if (primaryDestructive) {
            DestructiveButton(
                text = primaryText,
                onClick = onPrimary,
                modifier = Modifier.weight(0.5f),
                enabled = primaryEnabled
            )
        } else {
            PrimaryButton(
                text = primaryText,
                onClick = onPrimary,
                modifier = Modifier.weight(0.5f),
                enabled = primaryEnabled
            )
        }
        MediumHorizontalSpacer()
        SecondaryButton(
            text = secondaryText,
            onClick = onSecondary,
            modifier = Modifier.weight(0.5f),
            enabled = secondaryEnabled
        )
    }
}

@Composable
fun ConnectedButtonGroup(
    firstText: String,
    onFirst: () -> Unit,
    firstActive: Boolean,
    secondText: String,
    onSecond: () -> Unit,
    secondActive: Boolean,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        ConnectedButton(
            text = firstText,
            onClick = onFirst,
            active = firstActive,
            modifier = Modifier.weight(0.5f),
        )
        MediumHorizontalSpacer()
        ConnectedButton(
            text = secondText,
            onClick = onSecond,
            active = secondActive,
            modifier = Modifier.weight(0.5f),
        )
    }
}

@Preview
@Composable
private fun FixedPrimaryButtonPreview()
{
    GovUkTheme {
        FixedPrimaryButton(
            text = "Primary",
            onClick = {}
        )
    }
}

@Preview
@Composable
private fun FixedSecondaryButtonPreview()
{
    GovUkTheme {
        FixedSecondaryButton(
            text = "Primary",
            onClick = {}
        )
    }
}

@Preview
@Composable
private fun VerticalButtonGroupPreview()
{
    GovUkTheme {
        FixedDoubleButtonGroup(
            primaryText = "Primary",
            onPrimary = {},
            secondaryText = "Secondary",
            onSecondary = {},
            windowHeightSizeClass = WindowHeightSizeClass.MEDIUM
        )
    }
}

@Preview
@Composable
private fun HorizontalButtonGroupPreview()
{
    GovUkTheme {
        FixedDoubleButtonGroup(
            primaryText = "Primary",
            onPrimary = {},
            secondaryText = "Secondary",
            onSecondary = {},
            windowHeightSizeClass = WindowHeightSizeClass.COMPACT
        )
    }
}

@Preview
@Composable
private fun VerticalDestructiveButtonGroupPreview()
{
    GovUkTheme {
        FixedDoubleButtonGroup(
            primaryText = "Primary",
            onPrimary = {},
            secondaryText = "Secondary",
            onSecondary = {},
            primaryDestructive = true,
            windowHeightSizeClass = WindowHeightSizeClass.MEDIUM
        )
    }
}

@Preview
@Composable
private fun HorizontalDestructiveButtonGroupPreview()
{
    GovUkTheme {
        FixedDoubleButtonGroup(
            primaryText = "Primary",
            onPrimary = {},
            secondaryText = "Secondary",
            onSecondary = {},
            primaryDestructive = true,
            windowHeightSizeClass = WindowHeightSizeClass.COMPACT
        )
    }
}

@Preview
@Composable
private fun ConnectedButtonGroupPreview()
{
    GovUkTheme {
        ConnectedButtonGroup(
            firstText = "First",
            onFirst = { },
            firstActive = true,
            secondText = "Second",
            onSecond = { },
            secondActive = false
        )
    }
}