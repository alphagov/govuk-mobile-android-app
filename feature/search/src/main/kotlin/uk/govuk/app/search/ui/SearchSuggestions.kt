package uk.govuk.app.search.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import uk.govuk.app.design.ui.component.BodyBoldLabel
import uk.govuk.app.design.ui.component.BodyRegularLabel
import uk.govuk.app.design.ui.component.ExtraSmallHorizontalSpacer
import uk.govuk.app.design.ui.component.ListDivider
import uk.govuk.app.design.ui.component.SmallHorizontalSpacer
import uk.govuk.app.design.ui.theme.GovUkTheme
import uk.govuk.app.search.R

@Composable
internal fun PreviousSearches(
    previousSearches: List<String>,
    onClick: (String) -> Unit,
    onRemoveAll: () -> Unit,
    onRemove: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (previousSearches.isNotEmpty()) {
        var showDialog by remember { mutableStateOf(false) }

        LazyColumn(
            modifier
                .fillMaxWidth()
                .padding(horizontal = GovUkTheme.spacing.medium)
        ) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            top = GovUkTheme.spacing.small,
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BodyBoldLabel(
                        text = stringResource(R.string.previous_searches_heading),
                        modifier = Modifier
                            .weight(1f)
                            .semantics { heading() }
                    )

                    SmallHorizontalSpacer()

                    TextButton(
                        onClick = { showDialog = true }
                    ) {
                        BodyRegularLabel(
                            text = stringResource(R.string.remove_all_button),
                            color = GovUkTheme.colourScheme.textAndIcons.link,
                        )
                    }
                }
            }
            items(previousSearches) { searchTerm ->
                Column{
                    ListDivider()
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onClick(searchTerm) },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = GovUkTheme.colourScheme.textAndIcons.secondary
                        )
                        ExtraSmallHorizontalSpacer()
                        BodyRegularLabel(
                            text = searchTerm,
                            modifier = Modifier.weight(1f)
                        )
                        ExtraSmallHorizontalSpacer()
                        TextButton(
                            onClick = { onRemove(searchTerm) }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Clear,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                                tint = GovUkTheme.colourScheme.textAndIcons.trailingIcon
                            )
                        }
                    }
                }
            }
            item {
                ListDivider()
            }
        }

        if (showDialog) {
            ShowRemoveAllConfirmationDialog(
                onConfirm = onRemoveAll,
                onDismiss = { showDialog = false }
            )
        }
    }
}

@Composable
private fun ShowRemoveAllConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = onConfirm
            ) {
                BodyBoldLabel(
                    text = stringResource(R.string.remove_confirmation_dialog_button),
                    color = GovUkTheme.colourScheme.textAndIcons.buttonRemove
                )
            }
        },
        modifier = modifier,
        title = {
            BodyBoldLabel(stringResource(R.string.remove_confirmation_dialog_title))
        },
        text = {
            BodyBoldLabel(
                text = stringResource(R.string.remove_confirmation_dialog_message),
                color = GovUkTheme.colourScheme.textAndIcons.secondary
            )
        }
    )
}