package uk.govuk.app.design.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import uk.govuk.app.design.R
import uk.govuk.app.design.ui.theme.GovUkTheme

@Composable
fun TabHeader(
    text: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
            .padding(horizontal = GovUkTheme.spacing.medium),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Title2BoldLabel(
            text = text,
            textAlign = TextAlign.Center,
            modifier = modifier.weight(1f)
        )
    }
}

@Composable
fun ChildPageHeader(
    text: String? = null,
    includeActionButton: Boolean = false,
    actionText: String = "",
    onAction: () -> Unit = {},
    includeBackButton: Boolean = true,
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Column(modifier) {
        if (includeBackButton || includeActionButton) {
            Row(
                modifier = Modifier
                    .height(64.dp)
                    .fillMaxWidth()
                    .padding(GovUkTheme.spacing.small),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (includeBackButton) {
                    TextButton(
                        onClick = onBack,
                        modifier = Modifier.weight(1f),
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.content_desc_back),
                            tint = GovUkTheme.colourScheme.textAndIcons.link
                        )
                        Spacer(Modifier.weight(1f))
                    }
                }

                if (includeActionButton) {
                    Spacer(Modifier.weight(1f))

                    TextButton(
                        onClick = onAction,
                    ) {
                        BodyRegularLabel(
                            text = actionText,
                            color = GovUkTheme.colourScheme.textAndIcons.link,
                            textAlign = TextAlign.End
                        )
                    }
                }
            }
        }

        if (text != null) {
            Row(
                modifier = Modifier
                    .defaultMinSize(64.dp)
                    .fillMaxWidth()
                    .padding(GovUkTheme.spacing.small),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                LargeTitleBoldLabel(
                    text = text,
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(horizontal = GovUkTheme.spacing.medium)
                )
            }
        }
    }
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
            includeActionButton = true,
            actionText = "Done",
            onBack = { }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ChildPageHeaderBackAndActionPreview() {
    GovUkTheme {
        ChildPageHeader(
            text = "Child page title",
            includeActionButton = true,
            actionText = "Done",
            onBack = { }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ChildPageHeaderActionNoBackPreview() {
    GovUkTheme {
        ChildPageHeader(
            text = "Child page title",
            includeActionButton = true,
            actionText = "Done",
            includeBackButton = false
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun Default_ChildPageHeaderBackNoActionPreview() {
    GovUkTheme {
        ChildPageHeader(
            text = "Child page title",
            onBack = { }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ChildPageHeaderNoActionOrBackPreview() {
    GovUkTheme {
        ChildPageHeader(
            text = "Child page title",
            includeBackButton = false
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ChildPageHeaderLongTextNoActionOrBackPreview() {
    GovUkTheme {
        ChildPageHeader(
            text = "Child page title Child page title Child page title",
            includeBackButton = false
        )
    }
}
