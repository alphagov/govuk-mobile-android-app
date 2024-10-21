package uk.govuk.app.visited.ui.widget

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedCard
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import uk.govuk.app.design.R
import uk.govuk.app.design.ui.component.BodyBoldLabel
import uk.govuk.app.design.ui.theme.GovUkTheme

@Composable
fun VisitedWidget(
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        val widgetTitle = stringResource(uk.govuk.app.visited.R.string.visited_items_title)

        OutlinedCard(
            onClick = { onClick(widgetTitle) },
            colors = CardDefaults.cardColors(
                containerColor = GovUkTheme.colourScheme.surfaces.card
            ),
            border = BorderStroke(
                width = 1.dp,
                color = GovUkTheme.colourScheme.strokes.listDivider
            )
        ) {
            Row(
                modifier = Modifier
                    .padding(GovUkTheme.spacing.large)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painterResource(R.drawable.ic_visited),
                    contentDescription = null,
                    modifier = Modifier.size(40.dp)
                        .padding(end = GovUkTheme.spacing.small)
                )
                BodyBoldLabel(
                    widgetTitle,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    painterResource(R.drawable.ic_chevron),
                    contentDescription = null,
                    tint = GovUkTheme.colourScheme.textAndIcons.trailingIcon
                )
            }
        }
    }
}

@Preview
@Composable
private fun VisitedWidgetPreview() {
    GovUkTheme {
        VisitedWidget(
            onClick = { },
            Modifier
                .fillMaxWidth()
                .background(GovUkTheme.colourScheme.surfaces.background)
        )
    }
}
