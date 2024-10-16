package uk.govuk.app.design.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import uk.govuk.app.design.ui.theme.GovUkColourScheme.Strokes
import uk.govuk.app.design.ui.theme.GovUkColourScheme.Surfaces
import uk.govuk.app.design.ui.theme.GovUkColourScheme.TextAndIcons

private val Blue1 = Color(0xFF1D70B8)
private val Blue2 = Color(0xFF259AFF)
private val Blue3 = Color(0xFF00D1FF)
private val Blue4 = Color(0xFF45F2FD)
private val Blue5 = Color(0xFF003078)
private val Blue6 = Color(0xFF7AC2FF)

private val Yellow = Color(0xFFFFDD00)

private val Grey900 = Color(0xFF1A1A1A)
private val Grey800 = Color(0xFF333333)
private val Grey700 = Color(0xFF4D4D4D)
private val Grey600 = Color(0xFF666666)
private val Grey500 = Color(0xFF808080)
private val Grey400 = Color(0xFF999999)
private val Grey300 = Color(0xFFB2B2B2)
private val Grey200 = Color(0xFFCCCCCC)
private val Grey100 = Color(0xFFE5E5E5)
private val Grey60 = Color(0xFFF0F0F0)
private val Grey50 = Color(0xFFFAFAFA)

private val Black = Color(0xFF000000)
private val BlackAlpha30 = Color(0x4D000000)
private val BlackAlpha75 = Color(0x4B000000)

private val White = Color(0xFFFFFFFF)
private val WhiteAlpha30 = Color(0x4DFFFFFF)
private val WhiteAlpha75 = Color(0x4BFFFFFF)

data class GovUkColourScheme(
    val textAndIcons: TextAndIcons,
    val surfaces: Surfaces,
    val strokes: Strokes
) {
    data class TextAndIcons(
        val primary: Color,
        val secondary: Color,
        val link: Color,
        val buttonPrimary: Color,
        val buttonPrimaryHighlight: Color,
        val buttonPrimaryDisabled: Color,
        val buttonPrimaryFocused: Color,
        val buttonSecondary: Color,
        val buttonSecondaryHighlight: Color,
        val buttonSecondaryDisabled: Color,
        val buttonSecondaryFocused: Color,
        val buttonCompact: Color,
        val buttonCompactHighlight: Color,
        val buttonCompactDisabled: Color,
        val buttonCompactFocused: Color,
        val trailingIcon: Color
    )

    data class Surfaces(
        val background: Color,
        val primary: Color,
        val card: Color,
        val fixedContainer: Color,
        val buttonPrimary: Color,
        val buttonPrimaryHighlight: Color,
        val buttonPrimaryDisabled: Color,
        val buttonPrimaryFocused: Color,
        val buttonSecondary: Color,
        val buttonSecondaryHighlight: Color,
        val buttonSecondaryDisabled: Color,
        val buttonSecondaryFocused: Color,
        val buttonCompact: Color,
        val buttonCompactHighlight: Color,
        val buttonCompactDisabled: Color,
        val buttonCompactFocused: Color,
        val searchBox: Color,
        val toggleEnabled: Color,
        val toggleDisabled: Color,
        val toggleHandle: Color,
        val toggleBorder: Color,
        val icon: Color
    )

    data class Strokes(
        val container: Color,
        val listDivider: Color,
        val buttonCompactBorder: Color,
        val pageControlsInactive: Color
    )
}

internal val LightColorScheme = GovUkColourScheme(
    textAndIcons = TextAndIcons(
        primary = Black,
        secondary = Grey700,
        link = Blue1,
        buttonPrimary = White,
        buttonPrimaryHighlight = White,
        buttonPrimaryDisabled = Grey600,
        buttonPrimaryFocused = Black,
        buttonSecondary = Blue1,
        buttonSecondaryHighlight = Blue5,
        buttonSecondaryDisabled = Grey700,
        buttonSecondaryFocused = Black,
        buttonCompact = Blue1,
        buttonCompactHighlight = Blue5,
        buttonCompactDisabled = Grey600,
        buttonCompactFocused = Black,
        trailingIcon = Grey300
    ),
    surfaces = Surfaces(
        background = Grey50,
        primary = Blue1,
        card = White,
        fixedContainer = WhiteAlpha75,
        buttonPrimary = Blue1,
        buttonPrimaryHighlight = Blue5,
        buttonPrimaryDisabled = Grey100,
        buttonPrimaryFocused = Yellow,
        buttonSecondary = Grey50,
        buttonSecondaryHighlight = Grey50,
        buttonSecondaryDisabled = Grey50,
        buttonSecondaryFocused = Yellow,
        buttonCompact = White,
        buttonCompactHighlight = Color(0xFFE4EEFA), // Todo
        buttonCompactDisabled = Grey100,
        buttonCompactFocused = Yellow,
        searchBox = Grey60,
        toggleEnabled = Blue1,
        toggleDisabled = Grey300,
        toggleHandle = White,
        toggleBorder = White,
        icon = Blue1
    ),
    strokes = Strokes(
        container = BlackAlpha30,
        listDivider = Grey300,
        buttonCompactBorder = Grey300,
        pageControlsInactive = Grey500
    )
)

internal val DarkColorScheme = GovUkColourScheme(
    textAndIcons = TextAndIcons(
        primary = White,
        secondary = Grey300,
        link = Blue2,
        buttonPrimary = Black,
        buttonPrimaryHighlight = Black,
        buttonPrimaryDisabled = Black,
        buttonPrimaryFocused = Black,
        buttonSecondary = Blue2,
        buttonSecondaryHighlight = Blue6,
        buttonSecondaryDisabled = Grey300,
        buttonSecondaryFocused = Black,
        buttonCompact = Blue2,
        buttonCompactHighlight = Blue6,
        buttonCompactDisabled = Black,
        buttonCompactFocused = Black,
        trailingIcon = Grey500
    ),
    surfaces = Surfaces(
        background = Black,
        primary = Blue2,
        card = Grey800,
        fixedContainer = BlackAlpha75,
        buttonPrimary = Blue2,
        buttonPrimaryHighlight = Blue6,
        buttonPrimaryDisabled = Grey400,
        buttonPrimaryFocused = Yellow,
        buttonSecondary = Black,
        buttonSecondaryHighlight = Black,
        buttonSecondaryDisabled = Black,
        buttonSecondaryFocused = Yellow,
        buttonCompact = Color(0xFF262626), // Todo
        buttonCompactHighlight = Color(0xFF193E61), // Todo
        buttonCompactDisabled = Grey400,
        buttonCompactFocused = Yellow,
        searchBox = Grey700,
        toggleEnabled = Blue2,
        toggleDisabled = Grey600,
        toggleHandle = White,
        toggleBorder = Grey800,
        icon = Blue2
    ),
    strokes = Strokes(
        container = WhiteAlpha30,
        listDivider = Grey500,
        buttonCompactBorder = Grey500,
        pageControlsInactive = Grey300
    )
)

internal val LocalColourScheme = staticCompositionLocalOf {
    GovUkColourScheme(
        textAndIcons = TextAndIcons(
            primary = Color.Unspecified,
            secondary = Color.Unspecified,
            link = Color.Unspecified,
            buttonPrimary = Color.Unspecified,
            buttonPrimaryHighlight = Color.Unspecified,
            buttonPrimaryDisabled = Color.Unspecified,
            buttonPrimaryFocused = Color.Unspecified,
            buttonSecondary = Color.Unspecified,
            buttonSecondaryHighlight = Color.Unspecified,
            buttonSecondaryDisabled = Color.Unspecified,
            buttonSecondaryFocused = Color.Unspecified,
            buttonCompact = Color.Unspecified,
            buttonCompactHighlight = Color.Unspecified,
            buttonCompactDisabled = Color.Unspecified,
            buttonCompactFocused = Color.Unspecified,
            trailingIcon = Color.Unspecified
        ),
        surfaces = Surfaces(
            background = Color.Unspecified,
            primary = Color.Unspecified,
            card = Color.Unspecified,
            fixedContainer = Color.Unspecified,
            buttonPrimary = Color.Unspecified,
            buttonPrimaryHighlight = Color.Unspecified,
            buttonPrimaryDisabled = Color.Unspecified,
            buttonPrimaryFocused = Color.Unspecified,
            buttonSecondary = Color.Unspecified,
            buttonSecondaryHighlight = Color.Unspecified,
            buttonSecondaryDisabled = Color.Unspecified,
            buttonSecondaryFocused = Color.Unspecified,
            buttonCompact = Color.Unspecified,
            buttonCompactHighlight = Color.Unspecified,
            buttonCompactDisabled = Color.Unspecified,
            buttonCompactFocused = Color.Unspecified,
            searchBox = Color.Unspecified,
            toggleEnabled = Color.Unspecified,
            toggleDisabled = Color.Unspecified,
            toggleHandle = Color.Unspecified,
            toggleBorder = Color.Unspecified,
            icon = Color.Unspecified
        ),
        strokes = Strokes(
            container = Color.Unspecified,
            listDivider = Color.Unspecified,
            buttonCompactBorder = Color.Unspecified,
            pageControlsInactive = Color.Unspecified
        )
    )
}
