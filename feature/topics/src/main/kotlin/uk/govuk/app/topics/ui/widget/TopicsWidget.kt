package uk.govuk.app.topics.ui.widget

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import uk.govuk.app.design.ui.component.SmallVerticalSpacer
import uk.govuk.app.design.ui.component.Title3BoldLabel
import uk.govuk.app.design.ui.theme.GovUkTheme
import uk.govuk.app.topics.TopicsViewModel

@Composable
fun TopicsWidget(
    modifier: Modifier = Modifier
) {
    val viewModel: TopicsViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()

    Column(modifier = modifier) {
        Title3BoldLabel("Topics")

        SmallVerticalSpacer()

        uiState?.topics?.let{ topics ->
            // Todo - ideally this would be a lazy column to gain from performance optimizations, however
            //  nested lazy columns are not allowed without a non-trivial workaround (some widgets will
            //  themselves contain a lazy column/grid). The performance impact should be negligible with
            //  the amount of items currently displayed on the home screen but we may have to re-visit
            //  this in the future.
            Column {
                for (topic in topics) {
                    OutlinedCard(
                        colors = CardDefaults.cardColors(
                            containerColor = GovUkTheme.colourScheme.surfaces.card
                        ),
                        border = BorderStroke(
                            width = 1.dp,
                            color = GovUkTheme.colourScheme.strokes.listDivider
                        )
                    ) {
                        Box(
                            Modifier
                                .padding(GovUkTheme.spacing.medium)
                        ){
                            Text(topic.title)
                        }
                    }
                }
            }
        }
    }
}