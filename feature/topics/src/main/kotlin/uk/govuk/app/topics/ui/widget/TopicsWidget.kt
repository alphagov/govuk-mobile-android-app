package uk.govuk.app.topics.ui.widget

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
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

        uiState?.topics?.let{ topics ->
            LazyColumn {
                items(topics) { topic ->
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

@Preview
@Composable
private fun TopicsWidgetPreview() {
    GovUkTheme {
        TopicsWidget(
            Modifier
                .fillMaxWidth()
                .background(GovUkTheme.colourScheme.surfaces.background)
        )
    }
}