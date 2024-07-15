package uk.govuk.app.ui.theme

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import uk.govuk.app.R

data class GovUkTypography(
    val titleLarge: TextStyle,
    val bodyRegular: TextStyle
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
    )
)