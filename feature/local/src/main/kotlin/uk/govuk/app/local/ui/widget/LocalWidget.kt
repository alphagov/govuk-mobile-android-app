package uk.govuk.app.local.ui.widget

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import uk.govuk.app.design.ui.component.HomeNavigationCard
import uk.govuk.app.design.ui.theme.GovUkTheme
import uk.govuk.app.local.R

@Composable
fun LocalWidget(
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val title = stringResource(R.string.local_widget_title)
    HomeNavigationCard(
        title = title,
        onClick = { onClick(title) },
        modifier = modifier,
        icon = R.drawable.baseline_pin_drop_24
    )
}

@Preview
@Composable
private fun LocalWidgetPreview() {
    GovUkTheme {
        LocalWidget(onClick = { })
    }
}
