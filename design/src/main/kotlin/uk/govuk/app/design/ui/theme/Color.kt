package uk.govuk.app.design.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import uk.govuk.app.design.ui.theme.GovUkColourScheme.Strokes
import uk.govuk.app.design.ui.theme.GovUkColourScheme.Surfaces
import uk.govuk.app.design.ui.theme.GovUkColourScheme.TextAndIcons

val GovUkBlue = Color(0xFF1D70B8)
val GovUkBlueDarkMode = Color(0xFF259AFF)

val Blue3 = Color(0xFF003078)
val Blue4 = Color(0xFF7AC2FF)
val Blue5 = Color(0xFF00D1FF)
val Blue6 = Color(0xFF45F2FD)

val Yellow = Color(0xFFFFDD00)

val Grey900 = Color(0xFF1A1A1A)
val Grey800 = Color(0xFF333333)
val Grey700 = Color(0xFF4D4D4D)
val Grey600 = Color(0xFF666666)
val Grey500 = Color(0xFF808080)
val Grey400 = Color(0xFF999999)
val Grey300 = Color(0xFFB2B2B2)
val Grey200 = Color(0xFFCCCCCC)
val Grey100 = Color(0xFFE5E5E5)
val Grey50 = Color(0xFFFAFAFA)

val Black = Color(0xFF000000)
val BlackAlpha30 = Color(0x4D000000)

val White = Color(0xFFFFFFFF)
val WhiteAlpha30 = Color(0x4DFFFFFF)

data class GovUkColourScheme(
    val textAndIcons: TextAndIcons,
    val surfaces: Surfaces,
    val strokes: Strokes
) {
    data class TextAndIcons(
        val primary: Color,
        val secondary: Color,
        val link: Color,
        val buttonPrimary: Color
    )

    data class Surfaces(
        val background: Color,
        val primary: Color,
        val card: Color
    )

    data class Strokes(
        val container: Color,
        val listDivider: Color
    )
}

val LightColorScheme = GovUkColourScheme(
    textAndIcons = TextAndIcons(
        primary = Color.Black,
        secondary = Grey700,
        link = GovUkBlue,
        buttonPrimary = Color.White
    ),
    surfaces = Surfaces(
        background = Grey50,
        primary = GovUkBlue,
        card = Color.White
    ),
    strokes = Strokes(
        container = BlackAlpha30,
        listDivider = Grey300
    )
)

val DarkColorScheme = GovUkColourScheme(
    textAndIcons = TextAndIcons(
        primary = Color.White,
        secondary = Grey300,
        link = GovUkBlueDarkMode,
        buttonPrimary = Color.Black
    ),
    surfaces = Surfaces(
        background = Color.Black,
        primary = GovUkBlueDarkMode,
        card = Grey800
    ),
    strokes = Strokes(
        container = WhiteAlpha30,
        listDivider = Grey500
    )
)

val LocalColourScheme = staticCompositionLocalOf {
    GovUkColourScheme(
        textAndIcons = TextAndIcons(
            primary = Color.Unspecified,
            secondary = Color.Unspecified,
            link = Color.Unspecified,
            buttonPrimary = Color.Unspecified
        ),
        surfaces = Surfaces(
            background = Color.Unspecified,
            primary = Color.Unspecified,
            card = Color.Unspecified
        ),
        strokes = Strokes(
            container = Color.Unspecified,
            listDivider = Color.Unspecified
        )
    )
}
