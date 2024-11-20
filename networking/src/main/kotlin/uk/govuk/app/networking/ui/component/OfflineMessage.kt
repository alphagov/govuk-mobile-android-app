package uk.govuk.app.networking.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import uk.govuk.app.design.ui.theme.GovUkTheme
import uk.govuk.app.networking.R

@Composable
fun OfflineMessage(
    title: String? = null,
    description: String? = null,
    linkTitle: String? = null,
    onTryAgainClick: () -> Unit
) {
    Message(
        title = title ?: stringResource(R.string.no_internet_title),
        description = description ?: stringResource(R.string.no_internet_description),
        linkTitle = linkTitle ?: stringResource(R.string.try_again),
        onLinkClick = onTryAgainClick
    )
}

@Preview
@Composable
private fun OfflineMessagePreview() {
    GovUkTheme {
        OfflineMessage(onTryAgainClick = {})
    }
}
