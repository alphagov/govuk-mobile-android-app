package uk.govuk.app.search.ui.widget

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SearchBarDefaults.inputFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import uk.govuk.app.design.ui.component.BaseCard
import uk.govuk.app.design.ui.component.BodyRegularLabel
import uk.govuk.app.design.ui.theme.GovUkTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchWidget(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    BaseCard(
        onClick = onClick,
        modifier = modifier
    ) {
        // Todo - extract strings
        BodyRegularLabel("Find government services and information")

        Spacer(Modifier.height(GovUkTheme.spacing.small))

        SearchBar(
            query = "Search",
            onQueryChange = { },
            onSearch = { },
            active = false,
            onActiveChange = { },
            enabled = false,
            leadingIcon = {
                Icon(imageVector = Icons.Filled.Search, contentDescription = null)
            },
            colors = SearchBarDefaults.colors(
                containerColor = GovUkTheme.colourScheme.surfaces.searchBox,
                inputFieldColors = inputFieldColors(
                    disabledTextColor = GovUkTheme.colourScheme.textAndIcons.secondary,
                    disabledLeadingIconColor = GovUkTheme.colourScheme.textAndIcons.secondary
                )
            )
        ) { }
    }
}

@Preview
@Composable
fun SearchWidgetPreview() {
    GovUkTheme {
        SearchWidget(onClick = { })
    }
}