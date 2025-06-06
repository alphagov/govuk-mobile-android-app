package uk.gov.govuk.chat.ui.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import uk.gov.govuk.chat.R
import uk.gov.govuk.design.ui.component.HomeNavigationCard
import uk.gov.govuk.design.ui.theme.GovUkTheme

@Composable
fun ChatWidget(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    HomeNavigationCard(
        title = stringResource(id = R.string.widget_title),
        onClick = { onClick() },
        modifier = modifier,
        icon = R.drawable.outline_chat_24
    )
}

@Preview(showBackground = true)
@Composable
private fun ChatWidgetPreview() {
    GovUkTheme {
        ChatWidget(
            onClick = { },
            Modifier
                .fillMaxWidth()
                .background(GovUkTheme.colourScheme.surfaces.background)
        )
    }
}
