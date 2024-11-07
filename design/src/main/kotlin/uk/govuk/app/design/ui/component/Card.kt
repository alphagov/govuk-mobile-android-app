package uk.govuk.app.design.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedCard
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import uk.govuk.app.design.ui.theme.GovUkTheme

@Composable
fun GovUkCard(
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val cardColour = if (isSelected) {
        GovUkTheme.colourScheme.surfaces.cardSelected
    } else {
        GovUkTheme.colourScheme.surfaces.card
    }

    OutlinedCard(
        modifier.clickable { },
        colors = CardDefaults.cardColors(containerColor = cardColour),
        border = BorderStroke(
            width = 1.dp,
            color = GovUkTheme.colourScheme.strokes.listDivider
        )
    ) {
        Column(
            modifier = Modifier
                .clickable(
                    enabled = onClick != null,
                    onClick = { onClick?.invoke() }
                )
                .padding(GovUkTheme.spacing.medium)
        ) {
            content()
        }
    }
}