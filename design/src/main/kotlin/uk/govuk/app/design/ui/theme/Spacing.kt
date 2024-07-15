package uk.govuk.app.design.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class GovUkSpacing(
    val small: Dp,
    val medium: Dp,
    val large: Dp,
    val extraLarge: Dp
)

val Spacing = GovUkSpacing(
    small = 8.dp,
    medium = 16.dp,
    large = 24.dp,
    extraLarge = 32.dp
)

val LocalSpacing = staticCompositionLocalOf {
    GovUkSpacing(
        small = Dp.Unspecified,
        medium = Dp.Unspecified,
        large = Dp.Unspecified,
        extraLarge = Dp.Unspecified
    )
}