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
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import uk.govuk.app.design.ui.component.BodyBoldLabel
import uk.govuk.app.design.ui.component.BodyRegularLabel
import uk.govuk.app.design.ui.component.ExtraSmallHorizontalSpacer
import uk.govuk.app.design.ui.component.ListDivider
import uk.govuk.app.design.ui.theme.GovUkTheme
import uk.govuk.app.search.R

@Composable
internal fun SearchAutocomplete(
    searchTerm: String,
    suggestions: List<String>,
    onSearch: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (suggestions.isNotEmpty()) {
        val context = LocalContext.current

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
                            top = GovUkTheme.spacing.medium,
                            bottom = GovUkTheme.spacing.small
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BodyBoldLabel(
                        text = stringResource(R.string.search_autocomplete_heading),
                        modifier = Modifier
                            .semantics { heading() }
                    )
                }
            }
            items(suggestions) { suggestion ->
                Column {
                    ListDivider()
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSearch(suggestion) }
                            .padding(
                                top = GovUkTheme.spacing.medium,
                                bottom = GovUkTheme.spacing.medium
                            )
                            .semantics {
                                role = Role.Button
                                onClick(label = context.getString(R.string.content_desc_search)) { true }
                            },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = GovUkTheme.colourScheme.textAndIcons.secondary
                        )
                        ExtraSmallHorizontalSpacer()
                        BodyRegularLabel(text = suggestion)
                    }
                }
            }
            item {
                ListDivider()
            }
        }
    }
}
