package uk.govuk.app.design.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import uk.govuk.app.design.ui.theme.GovUkTheme

@Composable
fun BaseCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors().copy(
            containerColor = GovUkTheme.colourScheme.surfaces.card
        ),
        border = BorderStroke(1.dp, GovUkTheme.colourScheme.strokes.listDivider)
    ) {
        Column(Modifier.padding(GovUkTheme.spacing.medium)) {
            content()
        }
    }
}

@Preview
@Composable
fun BaseCardPreview() {
    GovUkTheme {
        BaseCard(
            onClick = { }
        ) {
            Column {
                Text("Card preview title")
                Spacer(Modifier.height(10.dp))
                Text("Card preview message")
            }
        }
    }
}