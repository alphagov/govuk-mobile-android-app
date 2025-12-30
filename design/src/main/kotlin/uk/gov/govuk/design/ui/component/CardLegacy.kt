package uk.gov.govuk.design.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedCard
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import uk.gov.govuk.design.ui.theme.GovUkTheme

@Composable
fun GovUkCardLegacy(
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    onClick: (() -> Unit)? = null,
    backgroundColour: Color = GovUkTheme.colourScheme.surfaces.cardBlue,
    borderColour: Color = GovUkTheme.colourScheme.strokes.cardBlue,
    padding: Dp = GovUkTheme.spacing.medium,
    content: @Composable ColumnScope.() -> Unit
) {
    val cardColour = if (isSelected) {
        GovUkTheme.colourScheme.surfaces.listSelected
    } else {
        backgroundColour
    }

    val strokeColour = if (isSelected) {
        GovUkTheme.colourScheme.strokes.cardSelected
    } else {
        borderColour
    }

    OutlinedCard(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = cardColour),
        border = BorderStroke(
            width = 1.dp,
            color = strokeColour
        )
    ) {
        Column(
            modifier = Modifier
                .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)  // if onClick is null Talkback announces 'disabled'
                .padding(padding)
        ) {
            content()
        }
    }
}
