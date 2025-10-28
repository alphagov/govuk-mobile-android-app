package uk.gov.govuk.design.ui.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import uk.gov.govuk.design.R
import uk.gov.govuk.design.ui.extension.talkBackText
import uk.gov.govuk.design.ui.model.ExternalLinkListItemStyle
import uk.gov.govuk.design.ui.model.IconListItemStyle
import uk.gov.govuk.design.ui.model.InternalLinkListItemStyle
import uk.gov.govuk.design.ui.theme.GovUkTheme

@Composable
fun InternalLinkListItem(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    description: String? = null,
    isFirst: Boolean = true,
    isLast: Boolean = true,
    style: InternalLinkListItemStyle = InternalLinkListItemStyle.Default
) {
    CardListItem(
        modifier = modifier,
        onClick = onClick,
        isFirst = isFirst,
        isLast = isLast
    ) {
        Row(
            modifier = Modifier.padding(all = GovUkTheme.spacing.medium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                BodyRegularLabel(
                    text = title,
                    color = GovUkTheme.colourScheme.textAndIcons.primary
                )
                description?.let { description ->
                    ExtraSmallVerticalSpacer()
                    SubheadlineRegularLabel(
                        text = description,
                        color = GovUkTheme.colourScheme.textAndIcons.secondary
                    )
                }
            }

            MediumHorizontalSpacer()

            when (style) {
                is InternalLinkListItemStyle.Status -> {
                    BodyRegularLabel(
                        text = style.title,
                        color = GovUkTheme.colourScheme.textAndIcons.iconTertiary
                    )
                    MediumHorizontalSpacer()
                }

                else -> { /* Do nothing */ }
            }

            Icon(
                painter = painterResource(R.drawable.ic_arrow),
                contentDescription = null,
                tint = GovUkTheme.colourScheme.textAndIcons.iconTertiary
            )
        }
    }
}

@Composable
fun ExternalLinkListItem(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    description: String? = null,
    isFirst: Boolean = true,
    isLast: Boolean = true,
    style: ExternalLinkListItemStyle = ExternalLinkListItemStyle.Default
) {
    CardListItem(
        modifier = modifier,
        onClick = onClick,
        isFirst = isFirst,
        isLast = isLast
    ) {
        val opensInWebBrowser = stringResource(R.string.opens_in_web_browser)
        Row(
            modifier = Modifier
                .padding(all = GovUkTheme.spacing.medium)
                .fillMaxWidth()
                .talkBackText(title, description, opensInWebBrowser)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                BodyRegularLabel(
                    text = title,
                    color = GovUkTheme.colourScheme.textAndIcons.link
                )

                description?.let { description ->
                    ExtraSmallVerticalSpacer()

                    SubheadlineRegularLabel(
                        text = description,
                        color = GovUkTheme.colourScheme.textAndIcons.secondary
                    )
                }
            }

            when (style) {
                is ExternalLinkListItemStyle.Icon -> {
                    Icon(
                        painter = painterResource(R.drawable.ic_external_link),
                        contentDescription = null,
                        tint = GovUkTheme.colourScheme.textAndIcons.link
                    )
                }

                is ExternalLinkListItemStyle.Button -> {
                    TextButton(
                        onClick = style.onClick,
                        modifier = Modifier
                            .semantics { contentDescription = style.altText }
                            .align(Alignment.CenterVertically),
                        contentPadding = PaddingValues(start = GovUkTheme.spacing.extraLarge)
                    ) {
                        Icon(
                            painter = painterResource(style.icon),
                            contentDescription = null,
                            tint = GovUkTheme.colourScheme.textAndIcons.secondary
                        )
                    }
                }
                else -> { /* Do nothing */ }
            }
        }
    }
}

@Composable
private fun ShowSubText(subText: String) {
    if (subText.isNotEmpty()) {
        Row(
            modifier = Modifier.padding(
                start = GovUkTheme.spacing.medium,
                top = 0.dp,
                bottom = GovUkTheme.spacing.medium
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SubheadlineRegularLabel(
                text = subText,
                color = GovUkTheme.colourScheme.textAndIcons.primary
            )
        }
    }
}

@Composable
fun ToggleListItem(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    isFirst: Boolean = true,
    isLast: Boolean = true
) {
    CardListItem(
        modifier = modifier,
        isFirst = isFirst,
        isLast = isLast
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = GovUkTheme.spacing.medium)
                .padding(vertical = GovUkTheme.spacing.small)
                .toggleable(
                    value = checked,
                    onValueChange = onCheckedChange,
                    role = Role.Switch
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BodyRegularLabel(
                text = title,
                modifier = Modifier.weight(1f)
            )

            MediumHorizontalSpacer()

            ToggleSwitch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                testDescription = title,
                Modifier.clearAndSetSemantics {  }
            )
        }
    }
}

@Composable
fun IconListItem(
    title: String,
    @DrawableRes icon: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: IconListItemStyle = IconListItemStyle.Regular,
    isFirst: Boolean = true,
    isLast: Boolean = true
) {
    CardListItem(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick,
        isFirst = isFirst,
        isLast = isLast,
        drawDivider = false
    ) {
        Box(Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(all = GovUkTheme.spacing.medium),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val iconConfig = when (style) {
                    IconListItemStyle.Regular ->
                        Pair(
                            GovUkTheme.colourScheme.textAndIcons.iconSurroundSecondary,
                            GovUkTheme.colourScheme.textAndIcons.iconSecondary
                        )
                    IconListItemStyle.Bold ->
                        Pair(
                            GovUkTheme.colourScheme.textAndIcons.iconSurroundPrimary,
                            GovUkTheme.colourScheme.textAndIcons.iconPrimary
                        )
                }

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            color = iconConfig.first,
                            shape = CircleShape
                        )
                        .padding(6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(icon),
                        contentDescription = null,
                        tint = iconConfig.second
                    )
                }

                MediumHorizontalSpacer()

                when (style) {
                    IconListItemStyle.Regular -> {
                        BodyRegularLabel(
                            text = title,
                            modifier = Modifier.weight(1f),
                            color = GovUkTheme.colourScheme.textAndIcons.linkPrimary
                        )
                    }
                    IconListItemStyle.Bold -> {
                        BodyBoldLabel(
                            text = title,
                            modifier = Modifier.weight(1f)
                        )

                        MediumHorizontalSpacer()

                        Icon(
                            painter = painterResource(R.drawable.ic_arrow),
                            contentDescription = null,
                            tint = GovUkTheme.colourScheme.textAndIcons.iconTertiary
                        )
                    }
                }

            }
        }

        if (!isLast) {
            ListDivider(
                Modifier
                    .fillMaxWidth()
                    .padding(start = 72.dp)
                    .padding(end = GovUkTheme.spacing.medium)
            )
        }
    }
}

@Composable
fun CardListItem(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    isFirst: Boolean = true,
    isLast: Boolean = true,
    drawDivider: Boolean = true,
    content: @Composable () -> Unit,
) {
    val backgroundColor = GovUkTheme.colourScheme.surfaces.list
    val backgroundColorHighlight = GovUkTheme.colourScheme.surfaces.cardHighlight

    val interactionSource = remember { MutableInteractionSource() }

    var isClicked by remember { mutableStateOf(false) }
    if (onClick != null) {
        LaunchedEffect(isClicked) {
            delay(200)
            isClicked = false
        }
    }

    Box(
        modifier = modifier
            .then(
                if (onClick != null) {
                    Modifier.clickable(
                        interactionSource = interactionSource,
                        indication = null
                    ) {
                        isClicked = true
                        onClick()
                    }
                } else Modifier
            )
            .semantics(mergeDescendants = true) { }
            .drawBehind {
                drawCell(
                    isFirst = isFirst,
                    isLast = isLast,
                    backgroundColor = backgroundColor,
                    backgroundColorHighlight = backgroundColorHighlight,
                    isClicked = isClicked
                )
            }
    ) {
        Column {
            content()

            if (!isLast && drawDivider) {
                ListDivider(Modifier.padding(horizontal = GovUkTheme.spacing.medium))
            }
        }
    }
}

private fun DrawScope.drawCell(
    isFirst: Boolean,
    isLast: Boolean,
    backgroundColor: Color,
    backgroundColorHighlight: Color,
    isClicked: Boolean
) {
    val cornerRadius: Dp = 12.dp

    when {
        isFirst && isLast ->
            singleCell(
                cornerRadius = cornerRadius.toPx(),
                backgroundColor = backgroundColor,
                backgroundColorHighlight = backgroundColorHighlight,
                isClicked = isClicked
            )
        isFirst ->
            firstCell(
                cornerRadius = cornerRadius.toPx(),
                backgroundColor = backgroundColor,
                backgroundColorHighlight = backgroundColorHighlight,
                isClicked = isClicked
            )
        isLast ->
            lastCell(
                cornerRadius = cornerRadius.toPx(),
                backgroundColor = backgroundColor,
                backgroundColorHighlight = backgroundColorHighlight,
                isClicked = isClicked
            )
        else ->
            intermediateCell(
                backgroundColor = backgroundColor,
                backgroundColorHighlight = backgroundColorHighlight,
                isClicked = isClicked
            )
    }
}

private fun DrawScope.singleCell(
    cornerRadius: Float,
    backgroundColor: Color,
    backgroundColorHighlight: Color,
    isClicked: Boolean
) {
    // Path for top, bottom and side borders with rounded corners
    val path = Path().apply {
        // Start at bottom left
        moveTo(0f, size.height - cornerRadius)

        // Line to top left
        lineTo(0f, 0f + cornerRadius)

        // Top left corner
        arcTo(
            rect = Rect(
                0f,
                0f,
                cornerRadius * 2,
                cornerRadius * 2
            ),
            startAngleDegrees = 180f,
            sweepAngleDegrees = 90f,
            forceMoveTo = false
        )

        // Line to top right
        lineTo(size.width - cornerRadius, 0f)

        // Top right corner
        arcTo(
            rect = Rect(
                size.width - cornerRadius * 2,
                0f,
                size.width,
                cornerRadius * 2
            ),
            startAngleDegrees = -90f,
            sweepAngleDegrees = 90f,
            forceMoveTo = false
        )

        // Line to bottom right
        lineTo(size.width, size.height - cornerRadius)

        // Bottom right corner
        arcTo(
            rect = Rect(
                size.width - 2 * cornerRadius,
                size.height - 2 * cornerRadius,
                size.width,
                size.height
            ),
            startAngleDegrees = 0f,
            sweepAngleDegrees = 90f,
            forceMoveTo = false
        )

        // Line to bottom left
        lineTo(cornerRadius, size.height)

        // Bottom left corner
        arcTo(
            rect = Rect(
                0f,
                size.height - 2 * cornerRadius,
                2 * cornerRadius,
                size.height
            ),
            startAngleDegrees = 90f,
            sweepAngleDegrees = 90f,
            forceMoveTo = false
        )
    }

    // Draw background colour
    drawPath(
        path = path,
        color = if (isClicked) backgroundColorHighlight else backgroundColor,
        style = Fill
    )
}

private fun DrawScope.firstCell(
    cornerRadius: Float,
    backgroundColor: Color,
    backgroundColorHighlight: Color,
    isClicked: Boolean
) {
    // Path for top and side borders with rounded corners
    val path = Path().apply {
        // Start at bottom left
        moveTo(0f, size.height)

        // Line to top left
        lineTo(0f, 0f + cornerRadius)

        // Top left corner
        arcTo(
            rect = Rect(
                0f,
                0f,
                cornerRadius * 2,
                cornerRadius * 2
            ),
            startAngleDegrees = 180f,
            sweepAngleDegrees = 90f,
            forceMoveTo = false
        )

        // Line to top right
        lineTo(size.width - cornerRadius, 0f)

        // Top right corner
        arcTo(
            rect = Rect(
                size.width - cornerRadius * 2,
                0f,
                size.width,
                cornerRadius * 2
            ),
            startAngleDegrees = -90f,
            sweepAngleDegrees = 90f,
            forceMoveTo = false
        )

        // Line to bottom right
        lineTo(size.width, size.height)
    }

    // Draw background colour
    drawPath(
        path = path,
        color = if (isClicked) backgroundColorHighlight else backgroundColor,
        style = Fill
    )
}

private fun DrawScope.intermediateCell(
    backgroundColor: Color,
    backgroundColorHighlight: Color,
    isClicked: Boolean
) {
    // Draw background colour
    drawRect(
        color = if (isClicked) backgroundColorHighlight else backgroundColor,
        size = size
    )
}

private fun DrawScope.lastCell(
    cornerRadius: Float,
    backgroundColor: Color,
    backgroundColorHighlight: Color,
    isClicked: Boolean
) {
    // Path for bottom and side borders with rounded corners
    val path = Path().apply {
        // Start at top-right corner
        moveTo(size.width, 0f)

        // Line to bottom right
        lineTo(size.width, size.height - cornerRadius)

        // Bottom right corner
        arcTo(
            rect = Rect(
                size.width - 2 * cornerRadius,
                size.height - 2 * cornerRadius,
                size.width,
                size.height
            ),
            startAngleDegrees = 0f,
            sweepAngleDegrees = 90f,
            forceMoveTo = false
        )

        // Line to bottom left
        lineTo(cornerRadius, size.height)

        // Bottom left corner
        arcTo(
            rect = Rect(
                0f,
                size.height - 2 * cornerRadius,
                2 * cornerRadius,
                size.height
            ),
            startAngleDegrees = 90f,
            sweepAngleDegrees = 90f,
            forceMoveTo = false
        )

        // Line to top left
        lineTo(0f, 0f)
    }

    // Draw background colour
    drawPath(
        path = path,
        color = if (isClicked) backgroundColorHighlight else backgroundColor,
        style = Fill
    )
}

@Preview
@Composable
private fun InternalLinkListItemPreview() {
    GovUkTheme {
        InternalLinkListItem("Title", {})
    }
}

@Preview
@Composable
private fun InternalLinkListItemDescriptionPreview() {
    GovUkTheme {
        InternalLinkListItem("Title", {}, description = "Description")
    }
}

@Preview
@Composable
private fun InternalLinkListItemStatusPreview() {
    GovUkTheme {
        InternalLinkListItem("Title", {}, style = InternalLinkListItemStyle.Status("Status"))
    }
}

@Preview
@Composable
private fun ExternalLinkListItemDefaultPreview() {
    GovUkTheme {
        ExternalLinkListItem("Title", {})
    }
}

@Preview
@Composable
private fun ExternalLinkListItemIconPreview() {
    GovUkTheme {
        ExternalLinkListItem(
            "Title",
            {},
            description = "Description",
            style = ExternalLinkListItemStyle.Icon
        )
    }
}

@Preview
@Composable
private fun ExternalLinkListItemButtonPreview() {
    GovUkTheme {
        ExternalLinkListItem(
            "Title", {}, description = "Description",
            style = ExternalLinkListItemStyle.Button(R.drawable.ic_cancel_round, "Alt text") {})
    }
}
