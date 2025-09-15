package uk.gov.govuk.chat.ui.chat

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import uk.gov.govuk.design.ui.component.BodyRegularLabel
import uk.gov.govuk.design.ui.theme.GovUkTheme

@Composable
internal fun Question(
    question: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = GovUkTheme.colourScheme.surfaces.chatUserMessageBackground,
                contentColor = GovUkTheme.colourScheme.textAndIcons.chatUserMessageText
            )
        ) {
            BodyRegularLabel(
                text = question,
                modifier = Modifier.padding(GovUkTheme.spacing.medium)
            )
        }
    }
}
