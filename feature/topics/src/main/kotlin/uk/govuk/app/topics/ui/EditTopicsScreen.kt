package uk.govuk.app.topics.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import uk.govuk.app.design.ui.component.BodyRegularLabel
import uk.govuk.app.design.ui.component.CardListItem
import uk.govuk.app.design.ui.component.ChildPageHeader
import uk.govuk.app.design.ui.component.ExtraLargeVerticalSpacer
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
    val topics by viewModel.topics.collectAsState()

    EditTopicsScreen(
        topics = topics,
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
        LazyColumn(
            Modifier
                .padding(horizontal = GovUkTheme.spacing.medium)
                .padding(top = GovUkTheme.spacing.small)
        ) {
            item {
                BodyRegularLabel(stringResource(R.string.editMessage))
            }

            item {
                MediumVerticalSpacer()
            }

            if (!topics.isNullOrEmpty()) {
                itemsIndexed(topics) { index, topic ->
                    CardListItem(
                        index = index,
                        lastIndex = topics.lastIndex
                    ) {
                        Row(
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
                    }
                }

                item {
                    ExtraLargeVerticalSpacer()
                }
            }
        }
    }
}