package uk.govuk.app.search.ui.component

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import uk.govuk.app.design.ui.component.BodyRegularLabel
import uk.govuk.app.design.ui.theme.GovUkTheme
import uk.govuk.app.search.R

@Composable
fun SearchField(
    placeholder: String,
    onAutocomplete: (String) -> Unit,
    onSearch: (String) -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by rememberSaveable {
        mutableStateOf("")
    }

    TextField(
        value = searchQuery,
        onValueChange = {
            searchQuery = it
            searchQuery.takeIf { searchQuery.length >= 3 }?.let {
                onAutocomplete(searchQuery)
            }
        },
        modifier = modifier,
        placeholder = {
            BodyRegularLabel(placeholder)
        },
        trailingIcon = {
            if (searchQuery.isNotEmpty()) {
                TextButton(
                    onClick = {
                        searchQuery = ""
                        onClear()
                    },
                ) {
                    Icon(
                        imageVector = Icons.Filled.Clear,
                        contentDescription = stringResource(R.string.content_desc_clear),
                        tint = GovUkTheme.colourScheme.textAndIcons.primary
                    )
                }
            }
        },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { onSearch(searchQuery) }),
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
