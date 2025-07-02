package uk.gov.govuk.notifications.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
internal fun EmptyScreen() {
    Box(
        Modifier
            .fillMaxSize()
            .background(Color.Transparent)
    )
}
