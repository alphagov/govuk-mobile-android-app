package uk.gov.govuk.design.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import uk.gov.govuk.design.ui.theme.GovUkColourScheme.Strokes
import uk.gov.govuk.design.ui.theme.GovUkColourScheme.Surfaces
import uk.gov.govuk.design.ui.theme.GovUkColourScheme.TextAndIcons

private val BluePrimary = Color(0xFF1D70B8)
private val BlueAccent = Color(0xFF11E0F1)
private val Blue2 = Color(0xFF259AFF)
private val BlueLighter25 = Color(0xFF5694CA)
private val BlueLighter50 = Color(0xFF8EB8DC)
private val BlueLighter95 = Color(0xFFF4F8FB)
private val BlueDarker25 = Color(0xFF16548A)
private val BlueDarker50 = Color(0xFF0F385C)
private val BlueDarker80 = Color(0xFF061625)

private val TealAccent = Color(0xFF00FFE0)

private val YellowPrimary = Color(0xFFFFDD00)

private val RedPrimary = Color(0xFFCA3535)
private val RedAccent = Color(0xFFFF5E5E)
private val RedDarker25 = Color(0xFF982828)

private val GreenPrimary = Color(0xFF11875A)
private val GreenAccent = Color(0xFF66F39E)
private val GreenLighter25 = Color(0xFF4DA583)
private val GreenLighter50 = Color(0xFF88C3AD)
private val GreenLighter95 = Color(0xFFF3F9F7)
private val GreenDarker25 = Color(0xFF0D6544)
private val GreenDarker50 = Color(0xFF09442D)

private val Grey850 = Color(0xFF262626)
private val Grey800 = Color(0xFF333333)
private val Grey700 = Color(0xFF4D4D4D)
private val Grey600 = Color(0xFF666666)
private val Grey500 = Color(0xFF808080)
private val Grey400 = Color(0xFF999999)
private val Grey300 = Color(0xFFB2B2B2)
private val Grey100 = Color(0xFFE5E5E5)
private val Grey60 = Color(0xFFF0F0F0)

private val Black = Color(0xFF000000)
private val BlackLighter50 = Color(0xFF858686)
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
        val linkHeader: Color,
        val header: Color,
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
        val buttonDestructive: Color,
        val icon: Color,
        val trailingIcon: Color,
        val buttonRemoveDisabled: Color,
        val selectedTick: Color,
        val logo: Color,
        val logoDot: Color,
        val logoCrown: Color,
        val iconGreen: Color,
        val textFieldError: Color
    )

    data class Surfaces(
        val background: Color,
        val primary: Color,
        val splash: Color,
        val cardDefault: Color,
        val cardBlue: Color,
        val cardHighlight: Color,
        val cardSelected: Color,
        val listBlue: Color,
        val listHeadingBlue: Color,
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
        val buttonDestructive: Color,
        val buttonDestructiveHighlight: Color,
        val search: Color,
        val switchOn: Color,
        val switchOff: Color,
        val toggleHandle: Color,
        val icon: Color,
        val homeHeader: Color,
        val cardGreen: Color,
        val textFieldBackground: Color,
        val textFieldHighlighted: Color
    )

    data class Strokes(
        val fixedContainer: Color,
        val listDivider: Color,
        val pageControlsInactive: Color,
        val cardBlue: Color,
        val cardSelected: Color,
        val listBlue: Color,
        val switchOn: Color,
        val switchOff: Color,
        val buttonCompactHighlight: Color,
        val cardGreen: Color,
        val textFieldCursor: Color,
        val textFieldError: Color
    )
}

internal val LightColorScheme = GovUkColourScheme(
    textAndIcons = TextAndIcons(
        primary = Black,
        secondary = Grey700,
        link = BluePrimary,
        linkHeader = White,
        header = White,
        buttonPrimary = White,
        buttonPrimaryHighlight = White,
        buttonPrimaryDisabled = Grey600,
        buttonPrimaryFocused = Black,
        buttonSecondary = BluePrimary,
        buttonSecondaryHighlight = BlueDarker25,
        buttonSecondaryDisabled = Grey700,
        buttonSecondaryFocused = Black,
        buttonCompact = BluePrimary,
        buttonCompactHighlight = BlueDarker25,
        buttonCompactDisabled = Grey600,
        buttonCompactFocused = Black,
        buttonSuccess = GreenPrimary,
        buttonDestructive = RedPrimary,
        icon = BluePrimary,
        trailingIcon = Grey300,
        buttonRemoveDisabled = Grey700,
        selectedTick = White,
        logo = White,
        logoDot = TealAccent,
        logoCrown = BlackLighter50,
        iconGreen = GreenDarker25,
        textFieldError = RedPrimary
    ),
    surfaces = Surfaces(
        background = White,
        primary = BluePrimary,
        splash = BluePrimary,
        cardDefault = White,
        cardBlue = BlueLighter95,
        cardHighlight = Grey400,
        cardSelected = GreenLighter95,
        listBlue = BlueLighter95,
        listHeadingBlue = BlueLighter95,
        fixedContainer = WhiteAlpha75,
        alert = Grey100,
        buttonPrimary = GreenPrimary,
        buttonPrimaryHighlight = GreenDarker25,
        buttonPrimaryDisabled = Grey100,
        buttonPrimaryFocused = YellowPrimary,
        buttonSecondary = Color.Transparent,
        buttonSecondaryHighlight = Color.Transparent,
        buttonSecondaryDisabled = Color.Transparent,
        buttonSecondaryFocused = YellowPrimary,
        buttonCompact = BlueLighter95,
        buttonCompactHighlight = BlueLighter95,
        buttonCompactDisabled = Grey100,
        buttonCompactFocused = YellowPrimary,
        buttonDestructive = RedPrimary,
        buttonDestructiveHighlight = RedDarker25,
        search = White,
        switchOn = GreenPrimary,
        switchOff = BlackLighter50,
        toggleHandle = White,
        icon = BluePrimary,
        homeHeader = BluePrimary,
        cardGreen = GreenLighter95,
        textFieldBackground = Grey60,
        textFieldHighlighted = BlueLighter50
    ),
    strokes = Strokes(
        fixedContainer = BlackAlpha30,
        listDivider = Grey300,
        pageControlsInactive = Grey500,
        cardBlue = BlueLighter50,
        cardSelected = GreenPrimary,
        listBlue = BlueLighter50,
        switchOn = GreenPrimary,
        switchOff = BlackLighter50,
        buttonCompactHighlight = BlueLighter25,
        cardGreen = GreenLighter50,
        textFieldCursor = Grey700,
        textFieldError = RedPrimary
    )
)

internal val DarkColorScheme = GovUkColourScheme(
    textAndIcons = TextAndIcons(
        primary = White,
        secondary = Grey300,
        link = BlueAccent,
        linkHeader = BlueAccent,
        header = White,
        buttonPrimary = Black,
        buttonPrimaryHighlight = Black,
        buttonPrimaryDisabled = Black,
        buttonPrimaryFocused = Black,
        buttonSecondary = BlueAccent,
        buttonSecondaryHighlight = BlueLighter25,
        buttonSecondaryDisabled = Grey300,
        buttonSecondaryFocused = Black,
        buttonCompact = BlueAccent,
        buttonCompactHighlight = BlueLighter25,
        buttonCompactDisabled = Black,
        buttonCompactFocused = Black,
        buttonSuccess = GreenAccent,
        buttonDestructive = RedAccent,
        icon = BlueLighter95,
        trailingIcon = Grey500,
        buttonRemoveDisabled = Grey300,
        selectedTick = White,
        logo = White,
        logoDot = TealAccent,
        logoCrown = BlackLighter50,
        iconGreen = White,
        textFieldError = RedAccent
    ),
    surfaces = Surfaces(
        background = Black,
        primary = BlueAccent,
        splash = BluePrimary,
        cardDefault = Grey800,
        cardBlue = BlueDarker50,
        cardHighlight = Grey850,
        cardSelected = GreenDarker50,
        listBlue = BlueDarker80,
        listHeadingBlue = BlueDarker50,
        fixedContainer = BlackAlpha75,
        alert = Grey800,
        buttonPrimary = GreenAccent,
        buttonPrimaryHighlight = GreenLighter25,
        buttonPrimaryDisabled = Grey400,
        buttonPrimaryFocused = YellowPrimary,
        buttonSecondary = Color.Transparent,
        buttonSecondaryHighlight = Color.Transparent,
        buttonSecondaryDisabled = Color.Transparent,
        buttonSecondaryFocused = YellowPrimary,
        buttonCompact = BlueDarker80,
        buttonCompactHighlight = BlueDarker80,
        buttonCompactDisabled = Grey400,
        buttonCompactFocused = YellowPrimary,
        buttonDestructive = RedAccent,
        buttonDestructiveHighlight = RedPrimary,
        search = Black,
        switchOn = GreenPrimary,
        switchOff = BlackLighter50,
        toggleHandle = White,
        icon = BlueAccent,
        homeHeader = BlueDarker50,
        cardGreen = GreenDarker50,
        textFieldBackground = Grey800,
        textFieldHighlighted = BlueDarker25
    ),
    strokes = Strokes(
        fixedContainer = WhiteAlpha30,
        listDivider = Grey500,
        pageControlsInactive = Grey300,
        cardBlue = BlueDarker25,
        cardSelected = GreenAccent,
        listBlue = BluePrimary,
        switchOn = GreenPrimary,
        switchOff = BlackLighter50,
        buttonCompactHighlight = BlueDarker25,
        cardGreen = GreenLighter25,
        textFieldCursor = Grey300,
        textFieldError = RedAccent
    )
)

internal val LocalColourScheme = staticCompositionLocalOf {
    GovUkColourScheme(
        textAndIcons = TextAndIcons(
            primary = Color.Unspecified,
            secondary = Color.Unspecified,
            link = Color.Unspecified,
            linkHeader = Color.Unspecified,
            header = Color.Unspecified,
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
            buttonDestructive = Color.Unspecified,
            icon = Color.Unspecified,
            trailingIcon = Color.Unspecified,
            buttonRemoveDisabled = Color.Unspecified,
            selectedTick = Color.Unspecified,
            logo = Color.Unspecified,
            logoDot = Color.Unspecified,
            logoCrown = Color.Unspecified,
            iconGreen = Color.Unspecified,
            textFieldError = Color.Unspecified
        ),
        surfaces = Surfaces(
            background = Color.Unspecified,
            primary = Color.Unspecified,
            splash = Color.Unspecified,
            cardDefault = Color.Unspecified,
            cardBlue = Color.Unspecified,
            cardHighlight = Color.Unspecified,
            cardSelected = Color.Unspecified,
            listBlue = Color.Unspecified,
            listHeadingBlue = Color.Unspecified,
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
            buttonDestructive = Color.Unspecified,
            buttonDestructiveHighlight = Color.Unspecified,
            search = Color.Unspecified,
            switchOn = Color.Unspecified,
            switchOff = Color.Unspecified,
            toggleHandle = Color.Unspecified,
            icon = Color.Unspecified,
            homeHeader = Color.Unspecified,
            cardGreen = Color.Unspecified,
            textFieldBackground = Color.Unspecified,
            textFieldHighlighted = Color.Unspecified
        ),
        strokes = Strokes(
            fixedContainer = Color.Unspecified,
            listDivider = Color.Unspecified,
            pageControlsInactive = Color.Unspecified,
            cardBlue = Color.Unspecified,
            cardSelected = Color.Unspecified,
            listBlue = Color.Unspecified,
            switchOn = Color.Unspecified,
            switchOff = Color.Unspecified,
            buttonCompactHighlight = Color.Unspecified,
            cardGreen = Color.Unspecified,
            textFieldCursor = Color.Unspecified,
            textFieldError = Color.Unspecified
        )
    )
}
