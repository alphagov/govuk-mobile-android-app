package uk.gov.govuk.design.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import uk.gov.govuk.design.ui.theme.GovUkTheme

@Composable
fun ScreenBackground() {
    Box(
        modifier = Modifier
            .background(GovUkTheme.colourScheme.surfaces.screenBackground)
            .fillMaxSize()
    )
}
