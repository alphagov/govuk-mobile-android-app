package uk.govuk.app.design.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import uk.govuk.app.design.ui.theme.GovUkTheme

@Composable
fun SearchField(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier
) {
    TextField(
        value = searchQuery,
        onValueChange = {
            onSearchQueryChange(it)
        },
        modifier = modifier,
        placeholder = {
            BodyRegularLabel(placeholder)
        },
        trailingIcon = {
            if (searchQuery.isNotEmpty()) {
                Box(
                    Modifier
                        .size(48.dp)
                        .clickable { onSearchQueryChange("") }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Clear,
                        contentDescription = null,
                        modifier = Modifier.align(Alignment.Center),
                        tint = GovUkTheme.colourScheme.textAndIcons.trailingIcon
                    )
                }
            }
        },
        singleLine = true,
        textStyle = GovUkTheme.typography.bodyRegular,
        colors = TextFieldDefaults.colors()
            .copy(
                focusedTextColor = GovUkTheme.colourScheme.textAndIcons.primary,
                unfocusedTextColor = GovUkTheme.colourScheme.textAndIcons.primary,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
            )
    )
}