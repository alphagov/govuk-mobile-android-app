package uk.govuk.app.visited.ui.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import uk.govuk.app.design.ui.component.HomeNavigationCard
import uk.govuk.app.design.ui.theme.GovUkTheme
import uk.govuk.app.visited.R

@Composable
fun VisitedWidget(
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val title = stringResource(R.string.visited_items_title)
    HomeNavigationCard(
        title = title,
        onClick = { onClick(title) },
        modifier = modifier,
        icon = R.drawable.ic_visited
    )
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
