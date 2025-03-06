package uk.govuk.app.search.ui.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import uk.govuk.app.design.ui.component.BodyRegularLabel
import uk.govuk.app.design.ui.component.MediumHorizontalSpacer
import uk.govuk.app.design.ui.theme.GovUkTheme
import uk.govuk.app.search.R

@Composable
fun SearchWidget(
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val searchBarText = stringResource(R.string.search_bar_text)

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(28.dp))
            .background(GovUkTheme.colourScheme.surfaces.search)
            .clickable {
                onClick(searchBarText)
            }
            .padding(GovUkTheme.spacing.medium),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Filled.Search,
            contentDescription = null,
            tint = GovUkTheme.colourScheme.textAndIcons.secondary
        )

        MediumHorizontalSpacer()

        BodyRegularLabel(
            text = searchBarText,
            modifier = Modifier.fillMaxWidth(),
            color = GovUkTheme.colourScheme.textAndIcons.secondary
        )
    }
}

@Preview
@Composable
private fun SearchWidgetPreview() {
    GovUkTheme {
        SearchWidget(onClick = { })
    }
}