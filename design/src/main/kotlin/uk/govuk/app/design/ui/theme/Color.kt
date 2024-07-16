package uk.govuk.app.design.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import uk.govuk.app.design.ui.theme.GovUkColourScheme.Strokes
import uk.govuk.app.design.ui.theme.GovUkColourScheme.Surfaces
import uk.govuk.app.design.ui.theme.GovUkColourScheme.TextAndIcons

val GovUkBlue = Color(0xFF1D70B8)
val GovUkBlueDarkMode = Color(0xFF259AFF)
val GovUkDot = Color(0xFF17C7FF)

val Grey50 = Color(0xFFFAFAFA)
val Grey300 = Color(0xFFB2B2B2)
val Grey500 = Color(0xFF808080)
val GreyContainer = Color(0xFF262626)

data class GovUkColourScheme(
    val textAndIcons: TextAndIcons,
    val surfaces: Surfaces,
    val strokes: Strokes
) {
    data class TextAndIcons(
        val primary: Color,
        val link: Color,
        val buttonPrimary: Color,
        val dot: Color,
    )

    data class Surfaces(
        val background: Color,
        val primary: Color,
        val container: Color
    )

    data class Strokes(
        val listDivider: Color
    )
}

val LightColorScheme = GovUkColourScheme(
    textAndIcons = TextAndIcons(
        primary = Color.Black,
        link = GovUkBlue,
        buttonPrimary = Color.White,
        dot = GovUkDot
    ),
    surfaces = Surfaces(
        background = Grey50,
        primary = GovUkBlue,
        container = Color.White
    ),
    strokes = Strokes(
        listDivider = Grey300
    )
)

val DarkColorScheme = GovUkColourScheme(
    textAndIcons = TextAndIcons(
        primary = Color.White,
        link = GovUkBlueDarkMode,
        buttonPrimary = Color.Black,
        dot = GovUkDot
    ),
    surfaces = Surfaces(
        background = Color.Black,
        primary = GovUkBlueDarkMode,
        container = GreyContainer
    ),
    strokes = Strokes(
        listDivider = Grey500
    )
)

val LocalColourScheme = staticCompositionLocalOf {
    GovUkColourScheme(
        textAndIcons = TextAndIcons(
            primary = Color.Unspecified,
            link = Color.Unspecified,
            buttonPrimary = Color.Unspecified,
            dot = Color.Unspecified
        ),
        surfaces = Surfaces(
            background = Color.Unspecified,
            primary = Color.Unspecified,
            container = Color.Unspecified
        ),
        strokes = Strokes(
            listDivider = Color.Unspecified
        )
    )
}
