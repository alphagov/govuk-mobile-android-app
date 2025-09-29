package uk.gov.govuk.design.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.invisibleToUser
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import uk.gov.govuk.design.R
import uk.gov.govuk.design.ui.theme.GovUkTheme

@Composable
fun TabHeader(
    text: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
            .background(GovUkTheme.colourScheme.surfaces.homeHeader)
            .padding(horizontal = GovUkTheme.spacing.medium),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Title2BoldLabel(
            text = text,
            modifier = Modifier
                .weight(1f)
                .semantics { heading() },
            color = GovUkTheme.colourScheme.textAndIcons.header,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun Header(
    modifier: Modifier = Modifier,
    text: String? = null,
    onBack: (() -> Unit)? = null,
    onAction: (() -> Unit)? = null,
    actionText: String? = null,
    actionAltText: String? = null,
    backgroundColour: Color = GovUkTheme.colourScheme.surfaces.background,
    actionColour: Color = GovUkTheme.colourScheme.textAndIcons.link,
    titleColour: Color = GovUkTheme.colourScheme.textAndIcons.primary
) {
    Column(
        modifier
            .background(backgroundColour)
            .semantics { this.invisibleToUser() }
    ) {
        if (onBack != null || onAction != null) {
            Row(
                modifier = Modifier
                    .height(64.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (onBack != null) {
                    TextButton(
                        onClick = onBack
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.content_desc_back),
                            tint = actionColour
                        )
                    }
                }

                if (onAction != null && actionText != null) {
                    Spacer(Modifier.weight(1f))

                    TextButton(
                        onClick = onAction
                    ) {
                        BodyRegularLabel(
                            text = actionText,
                            color = actionColour,
                            textAlign = TextAlign.End,
                            modifier = Modifier.semantics {
                                contentDescription = actionAltText ?: actionText
                            }
                        )
                    }
                }
            }
        }

        if (text != null) {
            LargeTitleBoldLabel(
                text = text,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = GovUkTheme.spacing.medium)
                    .focusable()
                    .semantics { heading() },
                color = titleColour
            )
        }
    }
}

@Composable
fun FullScreenHeader(
    modifier: Modifier = Modifier,
    text: String? = null,
    onBack: (() -> Unit)? = null,
    onAction: (() -> Unit)? = null,
    actionText: String? = null,
    actionAltText: String? = null
) {
    Header(
        modifier = modifier,
        text = text,
        onBack = onBack,
        onAction = onAction,
        actionText = actionText,
        actionAltText = actionAltText
    )
}

@Composable
fun ChildPageHeader(
    modifier: Modifier = Modifier,
    text: String? = null,
    onBack: (() -> Unit)? = null,
    onAction: (() -> Unit)? = null,
    actionText: String? = null,
    actionAltText: String? = null
) {
    Header(
        modifier = modifier,
        text = text,
        onBack = onBack,
        onAction = onAction,
        actionText = actionText,
        actionAltText = actionAltText,
        backgroundColour = GovUkTheme.colourScheme.surfaces.homeHeader,
        actionColour = GovUkTheme.colourScheme.textAndIcons.linkHeader,
        titleColour = GovUkTheme.colourScheme.textAndIcons.header
    )
}

@Preview(showBackground = true)
@Composable
private fun TabHeaderPreview() {
    GovUkTheme {
        TabHeader("Tab label")
    }
}

@Preview(showBackground = true)
@Composable
private fun ChildPageHeaderNoTextWithBackAndActionPreview() {
    GovUkTheme {
        ChildPageHeader(
            onBack = {},
            onAction = {},
            actionText = "Done"
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ChildPageHeaderBackAndActionPreview() {
    GovUkTheme {
        ChildPageHeader(
            text = "Child page title",
            onBack = {},
            onAction = {},
            actionText = "Done"
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ChildPageHeaderActionNoBackPreview() {
    GovUkTheme {
        ChildPageHeader(
            text = "Child page title",
            onAction = {},
            actionText = "Done"
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ChildPageHeaderBackNoActionPreview() {
    GovUkTheme {
        ChildPageHeader(
            text = "Child page title",
            onBack = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ChildPageHeaderNoActionOrBackPreview() {
    GovUkTheme {
        ChildPageHeader(
            text = "Child page title"
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ChildPageHeaderLongTextNoActionOrBackPreview() {
    GovUkTheme {
        ChildPageHeader(
            text = "This is a very long child page title that goes on and on"
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun FullScreenHeaderNoTextWithBackAndActionPreview() {
    GovUkTheme {
        FullScreenHeader(
            onBack = {},
            onAction = {},
            actionText = "Done"
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun FullScreenHeaderBackAndActionPreview() {
    GovUkTheme {
        FullScreenHeader(
            text = "Child page title",
            onBack = {},
            onAction = {},
            actionText = "Done"
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun FullScreenHeaderActionNoBackPreview() {
    GovUkTheme {
        FullScreenHeader(
            text = "Child page title",
            onAction = {},
            actionText = "Done"
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun FullScreenHeaderBackNoActionPreview() {
    GovUkTheme {
        FullScreenHeader(
            text = "Child page title",
            onBack = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun FullScreenHeaderNoActionOrBackPreview() {
    GovUkTheme {
        FullScreenHeader(
            text = "Child page title"
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun FullScreenHeaderLongTextNoActionOrBackPreview() {
    GovUkTheme {
        FullScreenHeader(
            text = "This is a very long child page title that goes on and on"
        )
    }
}
