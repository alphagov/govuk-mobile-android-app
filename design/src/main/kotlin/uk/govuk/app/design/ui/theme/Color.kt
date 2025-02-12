package uk.govuk.app.design.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import uk.govuk.app.design.ui.theme.GovUkColourScheme.Strokes
import uk.govuk.app.design.ui.theme.GovUkColourScheme.Surfaces
import uk.govuk.app.design.ui.theme.GovUkColourScheme.TextAndIcons

private val BluePrimary = Color(0xFF1D70B8)
private val BlueAccent = Color(0xFF11E0F1)
private val Blue4 = Color(0xFF45F2FD)
private val Blue6 = Color(0xFF7AC2FF) // Todo - should've been replace by green???
private val BlueLighter50 = Color(0xFF8EB8DC)
private val BlueLighter95 = Color(0xFFF4F8FB)
private val BlueDarker50 = Color(0xFF0F385C)

private val TealAccent = Color(0xFF00FFE0)

private val YellowPrimary = Color(0xFFFFDD00)

private val Red1 = Color(0xFFD4351C)

private val GreenPrimary = Color(0xFF11875A)
private val GreenAccent = Color(0xFF66F39E)
private val GreenLighter25 = Color(0xFF4DA583)
private val GreenLighter95 = Color(0xFFF3F9F7)
private val GreenDarker25 = Color(0xFF0D6544)
private val GreenDarker50 = Color(0xFF09442D)

private val Grey900 = Color(0xFF1A1A1A)
private val Grey850 = Color(0xFF262626)
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
        val buttonSuccess: Color,
        val icon: Color,
        val trailingIcon: Color,
        val buttonRemove: Color,
        val buttonRemoveDisabled: Color,
        val selectedTick: Color,
        val logo: Color,
        val logoDot: Color
    )

    data class Surfaces(
        val background: Color,
        val primary: Color,
        val cardDefault: Color,
        val cardBlue: Color,
        val cardHighlight: Color,
        val cardSelected: Color,
        val fixedContainer: Color,
        val alert: Color,
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
        val icon: Color,
        val homeHeader: Color
    )

    data class Strokes(
        val container: Color,
        val listDivider: Color,
        val buttonCompactBorder: Color,
        val pageControlsInactive: Color,
        val cardBlue: Color,
        val cardSelected: Color
    )
}

internal val LightColorScheme = GovUkColourScheme(
    textAndIcons = TextAndIcons(
        primary = Black,
        secondary = Grey700,
        link = BluePrimary,
        buttonPrimary = White,
        buttonPrimaryHighlight = White,
        buttonPrimaryDisabled = Grey600,
        buttonPrimaryFocused = Black,
        buttonSecondary = BluePrimary,
        buttonSecondaryHighlight = BlueDarker50,
        buttonSecondaryDisabled = Grey700,
        buttonSecondaryFocused = Black,
        buttonCompact = BluePrimary,
        buttonCompactHighlight = BlueDarker50,
        buttonCompactDisabled = Grey600,
        buttonCompactFocused = Black,
        buttonSuccess = GreenPrimary,
        icon = BluePrimary,
        trailingIcon = Grey300,
        buttonRemove = Red1,
        buttonRemoveDisabled = Grey700,
        selectedTick = White,
        logo = White,
        logoDot = TealAccent
    ),
    surfaces = Surfaces(
        background = White,
        primary = BluePrimary,
        cardDefault = White,
        cardBlue = BlueLighter95,
        cardHighlight = Grey400,
        cardSelected = GreenLighter95,
        fixedContainer = WhiteAlpha75,
        alert = Grey100,
        buttonPrimary = GreenPrimary,
        buttonPrimaryHighlight = GreenDarker25,
        buttonPrimaryDisabled = Grey100,
        buttonPrimaryFocused = YellowPrimary,
        buttonSecondary = Grey50,
        buttonSecondaryHighlight = Grey50,
        buttonSecondaryDisabled = Grey50,
        buttonSecondaryFocused = YellowPrimary,
        buttonCompact = White,
        buttonCompactHighlight = BlueLighter95,
        buttonCompactDisabled = Grey100,
        buttonCompactFocused = YellowPrimary,
        searchBox = Grey60,
        toggleEnabled = BluePrimary,
        toggleDisabled = Grey300,
        toggleHandle = White,
        toggleBorder = White,
        icon = BluePrimary,
        homeHeader = BluePrimary
    ),
    strokes = Strokes(
        container = BlackAlpha30,
        listDivider = Grey300,
        buttonCompactBorder = Grey300,
        pageControlsInactive = Grey500,
        cardBlue = BlueLighter50,
        cardSelected = GreenPrimary
    )
)

internal val DarkColorScheme = GovUkColourScheme(
    textAndIcons = TextAndIcons(
        primary = White,
        secondary = Grey300,
        link = BlueAccent,
        buttonPrimary = Black,
        buttonPrimaryHighlight = Black,
        buttonPrimaryDisabled = Black,
        buttonPrimaryFocused = Black,
        buttonSecondary = BlueAccent,
        buttonSecondaryHighlight = Blue6,
        buttonSecondaryDisabled = Grey300,
        buttonSecondaryFocused = Black,
        buttonCompact = BlueAccent,
        buttonCompactHighlight = Blue6,
        buttonCompactDisabled = Black,
        buttonCompactFocused = Black,
        buttonSuccess = GreenAccent,
        icon = BlueLighter95,
        trailingIcon = Grey500,
        buttonRemove = Red1,
        buttonRemoveDisabled = Grey300,
        selectedTick = White,
        logo = White,
        logoDot = TealAccent
    ),
    surfaces = Surfaces(
        background = Black,
        primary = BlueAccent,
        cardDefault = Grey800,
        cardBlue = BlueDarker50,
        cardHighlight = Grey850,
        cardSelected = GreenDarker50,
        fixedContainer = BlackAlpha75,
        alert = Grey800,
        buttonPrimary = GreenAccent,
        buttonPrimaryHighlight = GreenLighter25,
        buttonPrimaryDisabled = Grey400,
        buttonPrimaryFocused = YellowPrimary,
        buttonSecondary = Black,
        buttonSecondaryHighlight = Black,
        buttonSecondaryDisabled = Black,
        buttonSecondaryFocused = YellowPrimary,
        buttonCompact = Grey850,
        buttonCompactHighlight = BlueDarker50,
        buttonCompactDisabled = Grey400,
        buttonCompactFocused = YellowPrimary,
        searchBox = Grey700,
        toggleEnabled = BlueAccent,
        toggleDisabled = Grey600,
        toggleHandle = White,
        toggleBorder = Grey800,
        icon = BlueAccent,
        homeHeader = Black
    ),
    strokes = Strokes(
        container = WhiteAlpha30,
        listDivider = Grey500,
        buttonCompactBorder = Grey500,
        pageControlsInactive = Grey300,
        cardBlue = BluePrimary,
        cardSelected = GreenAccent
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
            buttonSuccess = Color.Unspecified,
            icon = Color.Unspecified,
            trailingIcon = Color.Unspecified,
            buttonRemove = Color.Unspecified,
            buttonRemoveDisabled = Color.Unspecified,
            selectedTick = Color.Unspecified,
            logo = Color.Unspecified,
            logoDot = Color.Unspecified
        ),
        surfaces = Surfaces(
            background = Color.Unspecified,
            primary = Color.Unspecified,
            cardDefault = Color.Unspecified,
            cardBlue = Color.Unspecified,
            cardHighlight = Color.Unspecified,
            cardSelected = Color.Unspecified,
            fixedContainer = Color.Unspecified,
            alert = Color.Unspecified,
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
            icon = Color.Unspecified,
            homeHeader = Color.Unspecified
        ),
        strokes = Strokes(
            container = Color.Unspecified,
            listDivider = Color.Unspecified,
            buttonCompactBorder = Color.Unspecified,
            pageControlsInactive = Color.Unspecified,
            cardBlue = Color.Unspecified,
            cardSelected = Color.Unspecified
        )
    )
}
