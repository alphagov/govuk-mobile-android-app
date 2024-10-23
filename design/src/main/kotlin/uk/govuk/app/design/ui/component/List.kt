package uk.govuk.app.design.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import uk.govuk.app.design.R
import uk.govuk.app.design.ui.theme.GovUkTheme

@Composable
fun InternalLinkListItem(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    index: Int = 0,
    lastIndex: Int = 0
) {
    CardListItem(
        index = index,
        lastIndex = lastIndex,
        modifier = modifier,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(all = GovUkTheme.spacing.medium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BodyRegularLabel(
                text = title,
                modifier = Modifier.weight(1f)
            )

            Icon(
                painter = painterResource(R.drawable.ic_chevron),
                contentDescription = null,
                tint = GovUkTheme.colourScheme.textAndIcons.trailingIcon
            )
        }
    }
}

@Composable
fun ExternalLinkListItem(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    index: Int = 0,
    lastIndex: Int = 0
) {
    CardListItem(
        index = index,
        lastIndex = lastIndex,
        modifier = modifier,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(all = GovUkTheme.spacing.medium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BodyRegularLabel(
                text = title,
                modifier = Modifier.weight(1f),
                color = GovUkTheme.colourScheme.textAndIcons.link,
            )

            Icon(
                painter = painterResource(R.drawable.ic_external_link),
//                contentDescription = stringResource(R.string.link_opens_in),
                contentDescription = null, // Todo - fix!!!
                tint = GovUkTheme.colourScheme.textAndIcons.link
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
    index: Int = 0,
    lastIndex: Int = 0
) {
    CardListItem(
        index = index,
        lastIndex = lastIndex,
        modifier = modifier,
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = GovUkTheme.spacing.medium)
                .padding(vertical = GovUkTheme.spacing.small),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BodyRegularLabel(
                text = title,
                modifier = Modifier.weight(1f)
            )

            ToggleSwitch(
                checked = checked,
                onCheckedChange = onCheckedChange
            )
        }
    }
}

@Composable
fun CardListItem(
    index: Int,
    lastIndex: Int,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val borderColor = GovUkTheme.colourScheme.strokes.listDivider
    val backgroundColor = GovUkTheme.colourScheme.surfaces.card
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
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                onClick?.let {
                    isClicked = true
                    it()
                }
            }
            .drawBehind {
                drawCell(
                    index = index,
                    lastIndex = lastIndex,
                    borderColor = borderColor,
                    backgroundColor = backgroundColor,
                    backgroundColorHighlight = backgroundColorHighlight,
                    isClicked = isClicked
                )
            }
    ) {
        Column {
            content()

            if (index < lastIndex) {
                ListDivider(Modifier.padding(horizontal = GovUkTheme.spacing.medium))
            }
        }
    }
}

private fun DrawScope.drawCell(
    index: Int,
    lastIndex: Int,
    borderColor: Color,
    backgroundColor: Color,
    backgroundColorHighlight: Color,
    isClicked: Boolean
) {
    val borderWidth = 1.dp
    val cornerRadius: Dp = 12.dp

    if (index == 0 && lastIndex == 0) {
        singleCell(
            borderWidth = borderWidth.toPx(),
            cornerRadius = cornerRadius.toPx(),
            borderColor = borderColor,
            backgroundColor = backgroundColor,
            backgroundColorHighlight = backgroundColorHighlight,
            isClicked = isClicked
        )
    } else {
        when (index) {
            0 -> {
                firstCell(
                    borderWidth = borderWidth.toPx(),
                    cornerRadius = cornerRadius.toPx(),
                    borderColor = borderColor,
                    backgroundColor = backgroundColor,
                    backgroundColorHighlight = backgroundColorHighlight,
                    isClicked = isClicked
                )
            }

            lastIndex -> {
                lastCell(
                    borderWidth = borderWidth.toPx(),
                    cornerRadius = cornerRadius.toPx(),
                    borderColor = borderColor,
                    backgroundColor = backgroundColor,
                    backgroundColorHighlight = backgroundColorHighlight,
                    isClicked = isClicked
                )
            }

            else -> {
                intermediateCell(
                    borderWidth = borderWidth.toPx(),
                    borderColor = borderColor,
                    backgroundColor = backgroundColor,
                    backgroundColorHighlight = backgroundColorHighlight,
                    isClicked = isClicked
                )
            }
        }
    }
}

private fun DrawScope.singleCell(
    borderWidth: Float,
    cornerRadius: Float,
    borderColor: Color,
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

    // Draw the path as a border
    drawPath(
        path = path,
        color = borderColor,
        style = Stroke(width = borderWidth)
    )
}

private fun DrawScope.firstCell(
    borderWidth: Float,
    cornerRadius: Float,
    borderColor: Color,
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

    // Draw the path as a border
    drawPath(
        path = path,
        color = borderColor,
        style = Stroke(width = borderWidth)
    )
}

private fun DrawScope.intermediateCell(
    borderWidth: Float,
    borderColor: Color,
    backgroundColor: Color,
    backgroundColorHighlight: Color,
    isClicked: Boolean
) {
    // Path for only side borders
    val path = Path().apply {
        // Start at top right corner
        moveTo(size.width, 0f)

        // Line to bottom right
        lineTo(size.width, size.height)

        // Move to bottom left
        moveTo(0f, size.height)

        // Line to top left
        lineTo(0f, 0f)
    }

    // Draw background colour
    drawRect(
        color = if (isClicked) backgroundColorHighlight else backgroundColor,
        size = size
    )

    // Draw the path as a border
    drawPath(
        path = path,
        color = borderColor,
        style = Stroke(width = borderWidth)
    )
}

private fun DrawScope.lastCell(
    borderWidth: Float,
    cornerRadius: Float,
    borderColor: Color,
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

    // Draw the path as a border
    drawPath(
        path = path,
        color = borderColor,
        style = Stroke(width = borderWidth)
    )
}