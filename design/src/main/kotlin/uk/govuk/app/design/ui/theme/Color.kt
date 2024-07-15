package uk.govuk.app.design.ui.theme

import androidx.compose.ui.graphics.Color
import uk.govuk.app.design.ui.theme.GovUkColourScheme.Strokes
import uk.govuk.app.design.ui.theme.GovUkColourScheme.Surfaces
import uk.govuk.app.design.ui.theme.GovUkColourScheme.TextAndIcons

val GovUkBlue = Color(0xFF1D70B8)
val GovUkBlueDarkMode = Color(0xFF259AFF)

val Grey50 = Color(0xFFFAFAFA)
val Grey300 = Color(0xFFB2B2B2)
val Grey500 = Color(0xFF808080)

data class GovUkColourScheme(
    val textAndIcons: TextAndIcons,
    val surfaces: Surfaces,
    val strokes: Strokes
) {
    data class TextAndIcons(
        val primary: Color
    )

    data class Surfaces(
        val background: Color,
        val primary: Color
    )

    data class Strokes(
        val listDivider: Color
    )
}

val LightColorScheme = GovUkColourScheme(
    textAndIcons = TextAndIcons(
        primary = Color.Black
    ),
    surfaces = Surfaces(
        background = Grey50,
        primary = GovUkBlue
    ),
    strokes = Strokes(
        listDivider = Grey300
    )
)

val DarkColorScheme = GovUkColourScheme(
    textAndIcons = TextAndIcons(
        primary = Color.White
    ),
    surfaces = Surfaces(
        background = Color.Black,
        primary = GovUkBlueDarkMode
    ),
    strokes = Strokes(
        listDivider = Grey500
    )
)