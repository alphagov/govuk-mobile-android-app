package uk.govuk.app.ui.theme

import androidx.compose.ui.graphics.Color
import uk.govuk.app.ui.theme.GovUkColourScheme.Strokes
import uk.govuk.app.ui.theme.GovUkColourScheme.Surfaces
import uk.govuk.app.ui.theme.GovUkColourScheme.TextAndIcons

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
        val buttonPrimary: Color,
        val splash: Color
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
        buttonPrimary = GovUkBlue,
        splash = GovUkBlue
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
        buttonPrimary = GovUkBlueDarkMode,
        splash = GovUkBlueDarkMode,
    ),
    strokes = Strokes(
        listDivider = Grey500
    )
)