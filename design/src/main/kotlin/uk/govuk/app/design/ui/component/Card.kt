package uk.govuk.app.design.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
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
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    BaseCard(
        onClick = onClick,
        modifier = modifier
    ) {
        BodyRegularLabel(title)

        Spacer(Modifier.height(GovUkTheme.spacing.medium))

        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(28.dp))
                .background(GovUkTheme.colourScheme.surfaces.searchBox)
                .padding(GovUkTheme.spacing.medium)
        ) {
            Icon(
                imageVector = Icons.Filled.Search,
                contentDescription = null,
                tint = GovUkTheme.colourScheme.textAndIcons.secondary
            )

            Spacer(Modifier.width(GovUkTheme.spacing.medium))

            BodyRegularLabel(
                text = stringResource(R.string.search_bar_text),
                modifier = Modifier.fillMaxWidth(),
                color = GovUkTheme.colourScheme.textAndIcons.secondary
            )
        }
    }
}

@Composable
private fun BaseCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors().copy(
            containerColor = GovUkTheme.colourScheme.surfaces.card
        ),
        border = BorderStroke(1.dp, GovUkTheme.colourScheme.strokes.listDivider)
    ) {
        Column(Modifier.padding(GovUkTheme.spacing.medium)) {
            content()
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