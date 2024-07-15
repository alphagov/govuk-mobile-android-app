package uk.govuk.app.design.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import uk.govuk.app.design.ui.theme.GovUkColourScheme.Strokes
import uk.govuk.app.design.ui.theme.GovUkColourScheme.Surfaces
import uk.govuk.app.design.ui.theme.GovUkColourScheme.TextAndIcons

val LocalColourScheme = staticCompositionLocalOf {
    GovUkColourScheme(
        textAndIcons = TextAndIcons(
            primary = Color.Unspecified
        ),
        surfaces = Surfaces(
            background = Color.Unspecified,
            primary = Color.Unspecified
        ),
        strokes = Strokes(
            listDivider = Color.Unspecified
        )
    )
}

val LocalTypography = staticCompositionLocalOf {
    GovUkTypography(
        titleLarge = TextStyle.Default,
        bodyRegular = TextStyle.Default
    )
}

object GovUkTheme {
    val colourScheme: GovUkColourScheme
        @Composable
        get() = LocalColourScheme.current
    val typography: GovUkTypography
        @Composable
        get() = LocalTypography.current
}

@Composable
fun GovUkTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = if (isSystemInDarkTheme()) DarkColorScheme else LightColorScheme

    CompositionLocalProvider(
        LocalColourScheme provides colorScheme,
        LocalTypography provides Typography,
        content = content
    )
}
