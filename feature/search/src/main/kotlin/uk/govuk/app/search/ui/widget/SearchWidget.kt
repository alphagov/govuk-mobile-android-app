package uk.govuk.app.search.ui.widget

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import uk.govuk.app.design.ui.component.BaseCard
import uk.govuk.app.design.ui.component.BodyRegularLabel

@Composable
fun SearchWidget(
    onClick: () -> Unit
) {
    BaseCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        // Todo - extract strings
        BodyRegularLabel("Find government services and information")
    }
}