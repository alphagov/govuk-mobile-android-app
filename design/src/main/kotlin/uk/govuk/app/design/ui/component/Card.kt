package uk.govuk.app.design.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import uk.govuk.app.design.R
import uk.govuk.app.design.ui.theme.GovUkTheme

@Composable
fun SearchCard(
    title: String,
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(GovUkTheme.colourScheme.surfaces.card)
            .border(
                width = 1.dp,
                color = GovUkTheme.colourScheme.strokes.listDivider,
                shape = RoundedCornerShape(10.dp)

            )
            .padding(GovUkTheme.spacing.medium)
    ) {
        Column {
            BodyRegularLabel(title)

            MediumVerticalSpacer()

            val searchBarText = stringResource(R.string.search_bar_text)

            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(28.dp))
                    .background(GovUkTheme.colourScheme.surfaces.searchBox)
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
    }
}

@Preview
@Composable
private fun SearchCardPreview() {
    GovUkTheme {
        SearchCard(
            title = "Title",
            onClick = { },
            modifier = Modifier.fillMaxWidth()
        )
    }
}