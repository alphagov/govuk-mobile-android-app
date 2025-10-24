package uk.gov.govuk.design.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import uk.gov.govuk.design.ui.theme.GovUkColourScheme.Strokes
import uk.gov.govuk.design.ui.theme.GovUkColourScheme.Surfaces
import uk.gov.govuk.design.ui.theme.GovUkColourScheme.TextAndIcons

private val BluePrimary = Color(0xFF1D70B8)
private val BlueAccent = Color(0xFF11E0F1)
private val Blue2 = Color(0xFF259AFF)
private val Blue99 = Color(0xFF092237)

private val BlueLighter25 = Color(0xFF5694CA)
private val BlueLighter50 = Color(0xFF8EB8DC)
private val BlueLighter80 = Color(0xFFD2E2F1)
private val BlueLighter90 = Color(0xFFE8F1F8)
private val BlueLighter95 = Color(0xFFF4F8FB)
private val BlueDarker25 = Color(0xFF16548A)
private val BlueDarker50 = Color(0xFF0F385C)
private val BlueDarker65 = Color(0xFF0A2740)
private val BlueDarker80 = Color(0xFF061625)
private val BlueDarker80Alpha50 = Color(0x80061625)
private val BlueDarkMode = Color(0xFF263D54)
private val TealAccent = Color(0xFF00FFE0)

private val YellowPrimary = Color(0xFFFFDD00)
private val YellowDarker50 = Color(0xFF806F0D)

private val RedPrimary = Color(0xFFCA3535)
private val RedAccent = Color(0xFFFF5E5E)
private val RedDarker25 = Color(0xFF982828)
private val RedDarker50 = Color(0xFF651B1B)
private val RedDarker80 = Color(0xFF280B0B)

private val GreenPrimary = Color(0xFF11875A)
private val GreenAccent = Color(0xFF66F39E)
private val GreenLighter25 = Color(0xFF4DA583)
private val GreenLighter50 = Color(0xFF88C3AD)
private val GreenLighter95 = Color(0xFFF3F9F7)
private val GreenDarker25 = Color(0xFF0D6544)
private val GreenDarker50 = Color(0xFF09442D)
private val GreenDarker80 = Color(0xFF031B12)

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
private val BlackLighter95 = Color(0xFFF3F3F3)


private val White = Color(0xFFFFFFFF)
private val WhiteAlpha30 = Color(0x4DFFFFFF)
private val WhiteAlpha50 = Color(0x80FFFFFF)
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
        val linkPrimary: Color,
        val linkSecondary: Color,
        val linkHeader: Color,
        val listSelected: Color,
        val listUnselected: Color,
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
        val iconPrimary: Color,
        val iconSecondary: Color,
        val iconTertiary: Color,
        val iconSurroundPrimary: Color,
        val iconSurroundSecondary: Color,
        val trailingIcon: Color,
        val buttonRemoveDisabled: Color,
        val selectedTick: Color,
        val logo: Color,
        val logoDot: Color,
        val logoCrown: Color,
        val iconGreen: Color,
        val textFieldError: Color,
        val chatButtonIconDisabled: Color,
        val chatButtonIconEnabled: Color,
        val chatUserMessageText: Color,
        val chatBotMessageText: Color,
        val chatBotHeaderText: Color,
        val chatLoadingTextLight: Color,
        val chatLoadingIcon: Color
    )

    data class Surfaces(
        val background: Color,
        val primary: Color,
        val splash: Color,
        val cardDefault: Color,
        val cardBlue: Color,
        val cardHighlight: Color, // TODO - DELETE ON COMPLETION OF DESIGN REFRESH!!!
        val cardNonTappable: Color,
        val list: Color,
        val listBlue: Color, // TODO - DELETE ON COMPLETION OF DESIGN REFRESH!!!
        val listHeadingBlue: Color,
        val listSelected: Color,
        val listUnselected: Color,
        val fixedContainer: Color,
        val alert: Color,
        val buttonPrimary: Color,
        val buttonPrimaryHighlight: Color,
        val buttonPrimaryDisabled: Color,
        val buttonPrimaryFocused: Color,
        val buttonPrimaryStroke: Color,
        val buttonPrimaryStrokeHighlight: Color,
        val buttonPrimaryStrokeFocussed: Color,
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
        val buttonDestructiveStroke: Color,
        val buttonDestructiveStrokeHighlight: Color,
        val buttonDestructiveStrokeFocussed: Color,
        val connectedButtonGroupActive: Color,
        val connectedButtonGroupInactive: Color,
        val search: Color,
        val switchOn: Color,
        val switchOff: Color,
        val toggleHandle: Color,
        val icon: Color,
        val homeHeader: Color,
        val cardGreen: Color,
        val textFieldBackground: Color,
        val textFieldHighlighted: Color,
        val radioSelected: Color,
        val radioUnselected: Color,
        val chatBackground: Color,
        val chatTextFieldBackground: Color,
        val chatButtonBackgroundDisabled: Color,
        val chatButtonBackgroundEnabled: Color,
        val chatUserMessageBackground: Color,
        val chatBotMessageBackground: Color,
        val chatIntroCardBackground: Color,
        val screenBackground: Color
    )

    data class Strokes(
        val fixedContainer: Color,
        val listDivider: Color,
        val pageControlsInactive: Color,
        val cardAlert: Color,
        val cardBlue: Color,
        val cardDefault: Color,
        val cardSelected: Color,
        val listBlue: Color, // TODO - DELETE ON COMPLETION OF DESIGN REFRESH!!!
        val switchOn: Color,
        val switchOff: Color,
        val buttonCompactHighlight: Color,
        val cardGreen: Color,
        val textFieldCursor: Color,
        val textFieldError: Color,
        val radioDivider: Color,
        val chatTextFieldBorder: Color,
        val chatTextFieldBorderDisabled: Color,
        val chatDivider: Color,
        val chatIntroCardBorder: Color
    )
}

internal val LightColorScheme = GovUkColourScheme(
    textAndIcons = TextAndIcons(
        primary = Black,
        secondary = Grey700,
        link = BluePrimary,
        linkPrimary = BluePrimary,
        linkSecondary = BluePrimary,
        linkHeader = White,
        listSelected = White,
        listUnselected = Black,
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
        iconPrimary = White,
        iconSecondary = BluePrimary,
        iconTertiary = BluePrimary,
        iconSurroundPrimary = BluePrimary,
        iconSurroundSecondary = BlueLighter80,
        trailingIcon = Grey300,
        buttonRemoveDisabled = Grey700,
        selectedTick = White,
        logo = White,
        logoDot = TealAccent,
        logoCrown = BlackLighter50,
        iconGreen = GreenDarker25,
        textFieldError = RedPrimary,
        chatButtonIconDisabled = Grey600,
        chatButtonIconEnabled = White,
        chatUserMessageText = Black,
        chatBotMessageText = Black,
        chatBotHeaderText = Grey700,
        chatLoadingTextLight = Grey300,
        chatLoadingIcon = BluePrimary
    ),
    surfaces = Surfaces(
        background = White,
        primary = BluePrimary,
        splash = BluePrimary,
        cardDefault = White,
        cardBlue = BlueLighter95,
        cardHighlight = Grey400, // TODO - DELETE ON COMPLETION OF DESIGN REFRESH!!!
        cardNonTappable = BlueLighter80,
        list = White,
        listBlue = BlueLighter95, // TODO - DELETE ON COMPLETION OF DESIGN REFRESH!!!
        listHeadingBlue = BlueLighter95,
        listSelected = BluePrimary,
        listUnselected = BlueLighter90,
        fixedContainer = WhiteAlpha75,
        alert = Grey100,
        buttonPrimary = GreenPrimary,
        buttonPrimaryHighlight = GreenDarker25,
        buttonPrimaryDisabled = Grey100,
        buttonPrimaryFocused = YellowPrimary,

        // Todo - move these into strokes???
        buttonPrimaryStroke = GreenDarker50,
        buttonPrimaryStrokeHighlight = GreenDarker80,
        buttonPrimaryStrokeFocussed = Black,

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

        // Todo - move these into strokes???
        buttonDestructiveStroke = RedDarker50,
        buttonDestructiveStrokeHighlight = RedDarker80,
        buttonDestructiveStrokeFocussed = Black,

        connectedButtonGroupActive = BluePrimary,
        connectedButtonGroupInactive = BlueLighter90,
        search = White,
        switchOn = GreenPrimary,
        switchOff = BlackLighter50,
        toggleHandle = White,
        icon = BluePrimary,
        homeHeader = BluePrimary,
        cardGreen = GreenLighter95,
        textFieldBackground = Grey60,
        textFieldHighlighted = BlueLighter50,
        radioSelected = GreenPrimary,
        radioUnselected = Grey300,
        chatBackground = BlueLighter90,
        chatTextFieldBackground = White,
        chatButtonBackgroundDisabled = Grey100,
        chatButtonBackgroundEnabled = BluePrimary,
        chatUserMessageBackground = WhiteAlpha50,
        chatBotMessageBackground = White,
        chatIntroCardBackground = BlueLighter95,
        screenBackground = BlueLighter90
    ),
    strokes = Strokes(
        fixedContainer = BlackAlpha30,
        listDivider = BlueLighter80,
        pageControlsInactive = Grey500,
        cardAlert = BlueLighter25,
        cardBlue = BlueLighter50,
        cardDefault = BlueLighter80,
        cardSelected = GreenPrimary,
        listBlue = BlueLighter50, // TODO - DELETE ON COMPLETION OF DESIGN REFRESH!!!
        switchOn = GreenPrimary,
        switchOff = BlackLighter50,
        buttonCompactHighlight = BlueLighter25,
        cardGreen = GreenLighter50,
        textFieldCursor = Grey700,
        textFieldError = RedPrimary,
        radioDivider = Grey300,
        chatTextFieldBorder = BluePrimary,
        chatTextFieldBorderDisabled = Grey300,
        chatDivider = BlueLighter80,
        chatIntroCardBorder = BlueLighter80
    )
)

internal val DarkColorScheme = GovUkColourScheme(
    textAndIcons = TextAndIcons(
        primary = White,
        secondary = Grey300,
        link = White,
        linkPrimary = White,
        linkSecondary = BlueAccent,
        linkHeader = BlueAccent,
        listSelected = White,
        listUnselected = White,
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
        iconPrimary = White,
        iconSecondary = BlackLighter95,
        iconTertiary = White,
        iconSurroundPrimary = BlueDarkMode,
        iconSurroundSecondary = BlueDarkMode,
        trailingIcon = Grey500,
        buttonRemoveDisabled = Grey300,
        selectedTick = White,
        logo = White,
        logoDot = TealAccent,
        logoCrown = BlackLighter50,
        iconGreen = White,
        textFieldError = RedAccent,
        chatButtonIconDisabled = Black,
        chatButtonIconEnabled = BlueDarker80,
        chatUserMessageText = White,
        chatBotMessageText = White,
        chatBotHeaderText = Grey300,
        chatLoadingTextLight = BlueLighter80,
        chatLoadingIcon = BluePrimary
    ),
    surfaces = Surfaces(
        background = BlueDarker80,
        primary = BlueAccent,
        splash = BluePrimary,
        cardDefault = BlueDarkMode,
        cardBlue = BlueDarker50,
        cardHighlight = Grey850, // TODO - DELETE ON COMPLETION OF DESIGN REFRESH!!!
        cardNonTappable = BlueDarker65,
        list = BlueDarker65,
        listBlue = BlueDarker80, // TODO - DELETE ON COMPLETION OF DESIGN REFRESH!!!
        listHeadingBlue = BlueDarker50,
        listSelected = BlueDarker25,
        listUnselected = BlueDarker65,
        fixedContainer = BlackAlpha75,
        alert = Grey800,
        buttonPrimary = GreenAccent,
        buttonPrimaryHighlight = GreenLighter25,
        buttonPrimaryDisabled = Grey400,
        buttonPrimaryFocused = YellowPrimary,

        // Todo - move these into strokes???
        buttonPrimaryStroke = GreenPrimary,
        buttonPrimaryStrokeHighlight = GreenDarker50,
        buttonPrimaryStrokeFocussed = YellowDarker50,

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

        // Todo - move these into strokes???
        buttonDestructiveStroke = RedPrimary,
        buttonDestructiveStrokeHighlight = RedDarker50,
        buttonDestructiveStrokeFocussed = YellowDarker50,

        connectedButtonGroupActive = BlueDarkMode,
        connectedButtonGroupInactive = BlueDarker80,
        search = Black,
        switchOn = GreenPrimary,
        switchOff = BlackLighter50,
        toggleHandle = White,
        icon = BlueAccent,
        homeHeader = BlueDarker65,
        cardGreen = GreenDarker50,
        textFieldBackground = Grey800,
        textFieldHighlighted = BlueDarker25,
        radioSelected = GreenAccent,
        radioUnselected = Grey500,
        chatBackground = BlueDarker80,
        chatTextFieldBackground = BlueDarker80,
        chatButtonBackgroundDisabled = Grey400,
        chatButtonBackgroundEnabled = BlueAccent,
        chatUserMessageBackground = BlueDarker80Alpha50,
        chatBotMessageBackground = BlueDarker80,
        chatIntroCardBackground = Blue99,
        screenBackground = BlueDarker80
    ),
    strokes = Strokes(
        fixedContainer = WhiteAlpha30,
        listDivider = BlueDarkMode,
        pageControlsInactive = Grey300,
        cardAlert = BlueLighter25,
        cardBlue = BlueDarker25,
        cardDefault = BlueDarkMode,
        cardSelected = GreenAccent,
        listBlue = BluePrimary, // TODO - DELETE ON COMPLETION OF DESIGN REFRESH!!!
        switchOn = GreenPrimary,
        switchOff = BlackLighter50,
        buttonCompactHighlight = BlueDarker25,
        cardGreen = GreenLighter25,
        textFieldCursor = Grey300,
        textFieldError = RedAccent,
        radioDivider = Grey500,
        chatTextFieldBorder = BlueAccent,
        chatTextFieldBorderDisabled = BlueLighter25,
        chatDivider = BlueDarker25,
        chatIntroCardBorder = BlueDarker50
    )
)

internal val LocalColourScheme = staticCompositionLocalOf {
    GovUkColourScheme(
        textAndIcons = TextAndIcons(
            primary = Color.Unspecified,
            secondary = Color.Unspecified,
            link = Color.Unspecified,
            linkPrimary = Color.Unspecified,
            linkSecondary = Color.Unspecified,
            linkHeader = Color.Unspecified,
            listSelected = Color.Unspecified,
            listUnselected = Color.Unspecified,
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
            iconPrimary = Color.Unspecified,
            iconSecondary = Color.Unspecified,
            iconTertiary = Color.Unspecified,
            iconSurroundPrimary = Color.Unspecified,
            iconSurroundSecondary = Color.Unspecified,
            trailingIcon = Color.Unspecified,
            buttonRemoveDisabled = Color.Unspecified,
            selectedTick = Color.Unspecified,
            logo = Color.Unspecified,
            logoDot = Color.Unspecified,
            logoCrown = Color.Unspecified,
            iconGreen = Color.Unspecified,
            textFieldError = Color.Unspecified,
            chatButtonIconDisabled = Color.Unspecified,
            chatButtonIconEnabled = Color.Unspecified,
            chatUserMessageText = Color.Unspecified,
            chatBotMessageText = Color.Unspecified,
            chatBotHeaderText = Color.Unspecified,
            chatLoadingTextLight = Color.Unspecified,
            chatLoadingIcon = Color.Unspecified
        ),
        surfaces = Surfaces(
            background = Color.Unspecified,
            primary = Color.Unspecified,
            splash = Color.Unspecified,
            cardDefault = Color.Unspecified,
            cardBlue = Color.Unspecified,
            cardHighlight = Color.Unspecified, // TODO - DELETE ON COMPLETION OF DESIGN REFRESH!!!
            cardNonTappable = Color.Unspecified,
            list = Color.Unspecified,
            listBlue = Color.Unspecified, // TODO - DELETE ON COMPLETION OF DESIGN REFRESH!!!
            listHeadingBlue = Color.Unspecified,
            listSelected = Color.Unspecified,
            listUnselected = Color.Unspecified,
            fixedContainer = Color.Unspecified,
            alert = Color.Unspecified,
            buttonPrimary = Color.Unspecified,
            buttonPrimaryHighlight = Color.Unspecified,
            buttonPrimaryDisabled = Color.Unspecified,
            buttonPrimaryFocused = Color.Unspecified,
            buttonPrimaryStroke = Color.Unspecified,
            buttonPrimaryStrokeHighlight = Color.Unspecified,
            buttonPrimaryStrokeFocussed = Color.Unspecified,
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
            buttonDestructiveStroke = Color.Unspecified,
            buttonDestructiveStrokeHighlight = Color.Unspecified,
            buttonDestructiveStrokeFocussed = Color.Unspecified,
            connectedButtonGroupActive = Color.Unspecified,
            connectedButtonGroupInactive = Color.Unspecified,
            search = Color.Unspecified,
            switchOn = Color.Unspecified,
            switchOff = Color.Unspecified,
            toggleHandle = Color.Unspecified,
            icon = Color.Unspecified,
            homeHeader = Color.Unspecified,
            cardGreen = Color.Unspecified,
            textFieldBackground = Color.Unspecified,
            textFieldHighlighted = Color.Unspecified,
            radioSelected = Color.Unspecified,
            radioUnselected = Color.Unspecified,
            chatBackground = Color.Unspecified,
            chatTextFieldBackground = Color.Unspecified,
            chatButtonBackgroundDisabled = Color.Unspecified,
            chatButtonBackgroundEnabled = Color.Unspecified,
            chatUserMessageBackground = Color.Unspecified,
            chatBotMessageBackground = Color.Unspecified,
            chatIntroCardBackground = Color.Unspecified,
            screenBackground = Color.Unspecified
        ),
        strokes = Strokes(
            fixedContainer = Color.Unspecified,
            listDivider = Color.Unspecified,
            pageControlsInactive = Color.Unspecified,
            cardAlert = Color.Unspecified,
            cardBlue = Color.Unspecified,
            cardDefault = Color.Unspecified,
            cardSelected = Color.Unspecified,
            listBlue = Color.Unspecified, // TODO - DELETE ON COMPLETION OF DESIGN REFRESH!!!
            switchOn = Color.Unspecified,
            switchOff = Color.Unspecified,
            buttonCompactHighlight = Color.Unspecified,
            cardGreen = Color.Unspecified,
            textFieldCursor = Color.Unspecified,
            textFieldError = Color.Unspecified,
            radioDivider = Color.Unspecified,
            chatTextFieldBorder = Color.Unspecified,
            chatTextFieldBorderDisabled = Color.Unspecified,
            chatDivider = Color.Unspecified,
            chatIntroCardBorder = Color.Unspecified
        )
    )
}
