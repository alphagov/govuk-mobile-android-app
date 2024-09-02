package uk.govuk.app.search.ui.widget

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import uk.govuk.app.design.ui.component.SearchCard
import uk.govuk.app.design.ui.theme.GovUkTheme
import uk.govuk.app.search.R

@Composable
fun SearchWidget(
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    SearchCard(
        title = stringResource(R.string.search_widget_title),
        onClick = onClick,
        modifier = modifier
    )
}

@Preview
@Composable
private fun SearchWidgetPreview() {
    GovUkTheme {
        SearchWidget(onClick = { })
    }
}