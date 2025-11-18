package uk.gov.govuk.widgets.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import uk.gov.govuk.config.data.remote.model.EmergencyBannerType
import uk.gov.govuk.config.data.remote.model.EmergencyBanner
import uk.gov.govuk.design.ui.component.HomeBannerCard
import uk.gov.govuk.design.ui.model.EmergencyBannerUiType

@Composable
fun EmergencyBanner(
    emergencyBanner: EmergencyBanner,
    onClick: (String) -> Unit,
    launchBrowser: (url: String) -> Unit,
    onSuppressClick: ((String) -> Unit),
    modifier: Modifier = Modifier
) {
    HomeBannerCard(
        modifier = modifier,
        title = emergencyBanner.title,
        description = emergencyBanner.body,
        linkTitle = emergencyBanner.link?.title,
        linkUrl = emergencyBanner.link?.url,
        isDismissible = emergencyBanner.allowsDismissal,
        type = emergencyBanner.type.toEmergencyBannerUiType(),
        onClick = { onClick(emergencyBanner.id) },
        launchBrowser = launchBrowser,
        onSuppressClick = { onSuppressClick(emergencyBanner.id) }
    )
}

fun EmergencyBannerType.toEmergencyBannerUiType() = when (this) {
    EmergencyBannerType.NOTABLE_DEATH -> EmergencyBannerUiType.NOTABLE_DEATH
    EmergencyBannerType.NATIONAL_EMERGENCY -> EmergencyBannerUiType.NATIONAL_EMERGENCY
    EmergencyBannerType.LOCAL_EMERGENCY -> EmergencyBannerUiType.LOCAL_EMERGENCY
    EmergencyBannerType.INFORMATION -> EmergencyBannerUiType.INFORMATION
}