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

data class ActionButton(
    val text: String? = null,
    val onClick: () -> Unit
)

@Composable
fun ChildPageHeader(
    text: String? = null,
    backButton: ActionButton? = null,
    actionButton: ActionButton? = null,
    modifier: Modifier = Modifier,
) {
    Column(modifier) {
        if (backButton != null || actionButton != null) {
            Row(
                modifier = Modifier
                    .height(64.dp)
                    .fillMaxWidth()
                    .padding(GovUkTheme.spacing.small),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                ChildPageHeaderBackButton(backButton, modifier = modifier.weight(1f))
                ChildPageHeaderActionButton(
                    actionButton,
                    modifier = modifier.weight(1f)
                )
            }
        }

        if (text != null) {
            ChildPageHeaderText(text = text, modifier = modifier)
        }
    }
}

@Composable
private fun ChildPageHeaderBackButton(
    actionButton: ActionButton? = null,
    modifier: Modifier = Modifier
) {
    if (actionButton == null) return

    TextButton(
        onClick = actionButton.onClick,
        modifier = modifier,
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = stringResource(R.string.content_desc_back),
            tint = GovUkTheme.colourScheme.textAndIcons.link
        )
        Spacer(Modifier.weight(1f))
    }
}

@Composable
private fun ChildPageHeaderActionButton(
    actionButton: ActionButton? = null,
    modifier: Modifier = Modifier
) {
    if (actionButton == null) return

    Spacer(modifier)

    TextButton(
        onClick = actionButton.onClick,
    ) {
        BodyRegularLabel(
            text = actionButton.text!!,
            color = GovUkTheme.colourScheme.textAndIcons.link,
            textAlign = TextAlign.End
        )
    }
}

@Composable
private fun ChildPageHeaderText(
    text: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
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
            backButton = ActionButton(onClick = { }),
            actionButton = ActionButton(text = "Done", onClick = { })
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ChildPageHeaderBackAndActionPreview() {
    GovUkTheme {
        ChildPageHeader(
            text = "Child page title",
            backButton = ActionButton(onClick = { }),
            actionButton = ActionButton(text = "Done", onClick = { })
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ChildPageHeaderActionNoBackPreview() {
    GovUkTheme {
        ChildPageHeader(
            text = "Child page title",
            actionButton = ActionButton(text = "Done", onClick = { })
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun Default_ChildPageHeaderBackNoActionPreview() {
    GovUkTheme {
        ChildPageHeader(
            text = "Child page title",
            backButton = ActionButton(onClick = { }),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ChildPageHeaderNoActionOrBackPreview() {
    GovUkTheme {
        ChildPageHeader(
            text = "Child page title",
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ChildPageHeaderLongTextNoActionOrBackPreview() {
    GovUkTheme {
        ChildPageHeader(
            text = "Child page title Child page title Child page title",
        )
    }
}
