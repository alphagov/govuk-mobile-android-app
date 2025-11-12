package uk.gov.govuk.design.ui.model

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import uk.gov.govuk.design.ui.theme.GovUkTheme

enum class EmergencyBannerUiType {
    NOTABLE_DEATH,
    NATIONAL_EMERGENCY,
    LOCAL_EMERGENCY,
    INFORMATION
}

val EmergencyBannerUiType.showDivider: Boolean
    get() = this != EmergencyBannerUiType.INFORMATION

val EmergencyBannerUiType.backgroundColour: Color
    @Composable
    get() = when (this) {
        EmergencyBannerUiType.NOTABLE_DEATH ->
            GovUkTheme.colourScheme.surfaces.cardEmergencyNotableDeath

        EmergencyBannerUiType.NATIONAL_EMERGENCY ->
            GovUkTheme.colourScheme.surfaces.cardEmergencyNational

        EmergencyBannerUiType.LOCAL_EMERGENCY ->
            GovUkTheme.colourScheme.surfaces.cardEmergencyLocal

        EmergencyBannerUiType.INFORMATION ->
            GovUkTheme.colourScheme.surfaces.cardEmergencyInformation
    }

val EmergencyBannerUiType.textColour: Color
    @Composable
    get() = when (this) {
        EmergencyBannerUiType.NOTABLE_DEATH,
        EmergencyBannerUiType.NATIONAL_EMERGENCY,
        EmergencyBannerUiType.LOCAL_EMERGENCY ->
            GovUkTheme.colourScheme.textAndIcons.linkHeader

        EmergencyBannerUiType.INFORMATION ->
            GovUkTheme.colourScheme.textAndIcons.primary
    }

val EmergencyBannerUiType.linkTitleColour: Color
    @Composable
    get() = when (this) {
        EmergencyBannerUiType.NOTABLE_DEATH,
        EmergencyBannerUiType.NATIONAL_EMERGENCY,
        EmergencyBannerUiType.LOCAL_EMERGENCY ->
            GovUkTheme.colourScheme.textAndIcons.linkHeader

        EmergencyBannerUiType.INFORMATION ->
            GovUkTheme.colourScheme.textAndIcons.link
    }

val EmergencyBannerUiType.borderColour: Color
    @Composable
    get() = when (this) {
        EmergencyBannerUiType.NOTABLE_DEATH ->
            GovUkTheme.colourScheme.strokes.cardEmergencyBannerNotableDeathBorder

        EmergencyBannerUiType.NATIONAL_EMERGENCY ->
            GovUkTheme.colourScheme.strokes.cardEmergencyBannerNationalEmergencyBorder

        EmergencyBannerUiType.LOCAL_EMERGENCY ->
            GovUkTheme.colourScheme.strokes.cardEmergencyBannerLocalEmergencyBorder

        EmergencyBannerUiType.INFORMATION ->
            GovUkTheme.colourScheme.strokes.cardEmergencyBannerInformationBorder
    }

val EmergencyBannerUiType.dismissIconColour: Color
    @Composable
    get() = when (this) {
        EmergencyBannerUiType.NOTABLE_DEATH,
        EmergencyBannerUiType.NATIONAL_EMERGENCY,
        EmergencyBannerUiType.LOCAL_EMERGENCY ->
            GovUkTheme.colourScheme.textAndIcons.iconPrimary

        EmergencyBannerUiType.INFORMATION ->
            GovUkTheme.colourScheme.textAndIcons.iconDismissInformationEmergencyBanner
    }