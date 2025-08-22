package uk.gov.govuk.alertbanner.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import uk.gov.govuk.config.data.remote.model.AlertBanner
import uk.gov.govuk.design.ui.component.HomeAlertCard

@Composable
fun AlertBanner(
    alertBanner: AlertBanner,
    onClick: (String) -> Unit,
    launchBrowser: (url: String) -> Unit,
    onSuppressClick: ((String) -> Unit),
    modifier: Modifier = Modifier
) {
    HomeAlertCard(
        modifier = modifier,
        description = alertBanner.body,
        linkTitle = alertBanner.link?.title,
        linkUrl = alertBanner.link?.url,
        onClick = { onClick(alertBanner.id) },
        launchBrowser = launchBrowser,
        onSuppressClick = { onSuppressClick(alertBanner.id) }
    )
}
