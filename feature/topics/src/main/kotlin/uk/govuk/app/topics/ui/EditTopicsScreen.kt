package uk.govuk.app.topics.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedCard
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import uk.govuk.app.design.ui.component.BodyRegularLabel
import uk.govuk.app.design.ui.component.ChildPageHeader
import uk.govuk.app.design.ui.component.ListDivider
import uk.govuk.app.design.ui.component.MediumVerticalSpacer
import uk.govuk.app.design.ui.component.ToggleSwitch
import uk.govuk.app.design.ui.theme.GovUkTheme
import uk.govuk.app.topics.EditTopicsViewModel
import uk.govuk.app.topics.R
import uk.govuk.app.topics.ui.model.TopicUi

@Composable
internal fun EditTopicsRoute(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: EditTopicsViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()

    EditTopicsScreen(
        topics = uiState?.topics,
        onPageView = { title -> viewModel.onPageView(title) },
        onBack = onBack,
        onTopicSelectedChanged = { ref, title, isSelected ->
            viewModel.onTopicSelectedChanged(
                ref = ref,
                title = title,
                isSelected = isSelected
            )
        },
        modifier = modifier
    )
}

@Composable
private fun EditTopicsScreen(
    topics: List<TopicUi>?,
    onPageView: (String) -> Unit,
    onBack: () -> Unit,
    onTopicSelectedChanged: (String, String, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val title = stringResource(R.string.editTitle)

    LaunchedEffect(Unit) {
        onPageView(title)
    }

    Column(modifier) {
        ChildPageHeader(
            text = title,
            onBack = onBack
        )
        Column(
            Modifier
                .verticalScroll(rememberScrollState())
                .padding(horizontal = GovUkTheme.spacing.medium)
                .padding(
                    top = GovUkTheme.spacing.small,
                    bottom = GovUkTheme.spacing.extraLarge
                )
        ) {
            BodyRegularLabel(stringResource(R.string.editMessage))

            MediumVerticalSpacer()

            if (!topics.isNullOrEmpty()) {
                OutlinedCard(
                    colors = CardDefaults.cardColors(
                        containerColor = GovUkTheme.colourScheme.surfaces.card
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Column {
                        topics.forEachIndexed { index, topic ->
                            Row(
                                modifier.padding(
                                    top = GovUkTheme.spacing.small,
                                    bottom = GovUkTheme.spacing.small,
                                    start = GovUkTheme.spacing.medium,
                                    end = GovUkTheme.spacing.medium
                                ),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                BodyRegularLabel(
                                    text = topic.title,
                                    modifier = Modifier.weight(1f)
                                )

                                ToggleSwitch(
                                    checked = topic.isSelected,
                                    onCheckedChange = { checked ->
                                        onTopicSelectedChanged(
                                            topic.ref,
                                            topic.title,
                                            checked
                                        )
                                    }
                                )
                            }

                            if (index < topics.lastIndex) {
                                ListDivider(
                                    Modifier.padding(
                                        top = 1.dp,
                                        bottom = 1.dp,
                                        start = GovUkTheme.spacing.medium,
                                        end = GovUkTheme.spacing.medium
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}