package uk.govuk.app.design.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import uk.govuk.app.design.R

data class GovUkTypography(
    val titleLarge: TextStyle,
    val bodyRegular: TextStyle,
    val bodyBold: TextStyle,
    val captionBold: TextStyle
)

val transport = FontFamily(
    Font(R.font.transport_bold, FontWeight.Bold),
    Font(R.font.transport_light, FontWeight.Light),
)

val Typography = GovUkTypography(
    titleLarge = TextStyle(
        fontFamily = transport,
        fontWeight = FontWeight.Bold,
        fontSize = 34.sp,
        lineHeight = 39.sp,
        letterSpacing = 0.sp
    ),
    bodyRegular = TextStyle(
        fontFamily = transport,
        fontWeight = FontWeight.Light,
        fontSize = 17.sp,
        lineHeight = 22.sp,
        letterSpacing = 0.sp
    ),
    bodyBold = TextStyle(
        fontFamily = transport,
        fontWeight = FontWeight.Bold,
        fontSize = 17.sp,
        lineHeight = 22.sp,
        letterSpacing = 0.sp
    ),
    captionBold = TextStyle(
        fontFamily = transport,
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp,
        lineHeight = 27.sp,
        letterSpacing = 0.sp
    )
)

val LocalTypography = staticCompositionLocalOf {
    GovUkTypography(
        titleLarge = TextStyle.Default,
        bodyRegular = TextStyle.Default,
        bodyBold = TextStyle.Default,
        captionBold = TextStyle.Default
    )
}