package uk.govuk.app.search.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import uk.govuk.app.design.R
import uk.govuk.app.design.ui.theme.GovUkTheme

class SearchFieldActions(
    val onBack: () -> Unit,
    val onSearchTermChange: (String) -> Unit,
    val onSearch: () -> Unit,
    val onClear: () -> Unit
)

@Composable
fun SearchField(
    searchTerm: String,
    placeholder: String,
    actions: SearchFieldActions,
    modifier: Modifier = Modifier,
    focusRequester: FocusRequester = FocusRequester()
) {
    Column(modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(28.dp))
                .background(GovUkTheme.colourScheme.surfaces.search),
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

            BasicTextField(
                value = searchTerm,
                onValueChange = {
                    actions.onSearchTermChange(it)
                },
                modifier = Modifier
                    .weight(1f)
                    .focusRequester(focusRequester),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { actions.onSearch() }),
                singleLine = true,
                textStyle = GovUkTheme.typography.bodyRegular.copy(
                    color = GovUkTheme.colourScheme.textAndIcons.primary
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SearchHeaderPreview() {
    GovUkTheme {
        Box(Modifier.background(GovUkTheme.colourScheme.surfaces.homeHeader)) {
            SearchField(
                searchTerm = "",
                placeholder = "Search",
                actions = SearchFieldActions(
                    onBack = { },
                    onSearchTermChange = { },
                    onSearch = { },
                    onClear = { }
                )
            )
        }
    }
}