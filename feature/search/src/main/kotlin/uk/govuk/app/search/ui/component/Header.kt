package uk.govuk.app.search.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import uk.govuk.app.design.R
import uk.govuk.app.design.ui.component.ListDivider
import uk.govuk.app.design.ui.theme.GovUkTheme

class SearchHeaderActions(
    val onBack: () -> Unit,
    val onSearchTermChange: (String) -> Unit,
    val onSearch: () -> Unit,
    val onClear: () -> Unit
)

@Composable
fun SearchHeader(
    searchTerm: String,
    placeholder: String,
    actions: SearchHeaderActions,
    modifier: Modifier = Modifier,
    focusRequester: FocusRequester = FocusRequester()
) {
    Column(modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(
                onClick = actions.onBack
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.content_desc_back),
                    tint = GovUkTheme.colourScheme.textAndIcons.link
                )
            }
            SearchField(
                searchTerm = searchTerm,
                placeholder = placeholder,
                onSearchTermChange = actions.onSearchTermChange,
                onSearch = actions.onSearch,
                onClear = actions.onClear,
                modifier = Modifier
                    .weight(1f)
                    .focusRequester(focusRequester)
            )
        }
        ListDivider()
    }
}

@Preview(showBackground = true)
@Composable
private fun SearchHeaderPreview() {
    GovUkTheme {
        SearchHeader(
            searchTerm = "",
            placeholder = "Search",
            actions = SearchHeaderActions(
                onBack = { },
                onSearchTermChange = { },
                onSearch = { },
                onClear = { }
            )
        )
    }
}