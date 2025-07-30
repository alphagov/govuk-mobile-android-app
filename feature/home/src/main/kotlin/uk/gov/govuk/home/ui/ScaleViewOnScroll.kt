package uk.gov.govuk.home.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
internal fun rememberScaleViewOnScroll(maxViewSize: Dp): ScaleViewOnScroll {
    val minViewSize = 0.dp
    var currentViewSize by remember { mutableStateOf(minViewSize) }
    var scaleFactor by remember { mutableFloatStateOf(1f) }
    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val delta = available.y
                val newImageSize = currentViewSize - delta.dp
                val previousImageSize = currentViewSize
                currentViewSize = newImageSize.coerceIn(minViewSize, maxViewSize)
                val consumed = currentViewSize - previousImageSize
                scaleFactor = currentViewSize / maxViewSize
                return Offset(0f, consumed.value / 2)
            }
        }
    }
    return remember(
        currentViewSize,
        scaleFactor,
        nestedScrollConnection
    ) {
        ScaleViewOnScroll(
            currentViewSize,
            scaleFactor,
            nestedScrollConnection,
            maxViewSize
        )
    }
}

internal data class ScaleViewOnScroll(
    val currentViewSize: Dp,
    val scaleFactor: Float,
    val nestedScrollConnection: NestedScrollConnection,
    val maxViewSize: Dp,
)
