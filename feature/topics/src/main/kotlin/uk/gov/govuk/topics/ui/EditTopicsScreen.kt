package uk.gov.govuk.topics.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import uk.gov.govuk.design.ui.component.BodyRegularLabel
import uk.gov.govuk.design.ui.component.ChildPageHeader
import uk.gov.govuk.design.ui.component.ExtraLargeVerticalSpacer
import uk.gov.govuk.design.ui.component.MediumVerticalSpacer
import uk.gov.govuk.design.ui.component.ToggleListItemLegacy
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.gov.govuk.topics.EditTopicsViewModel
import uk.gov.govuk.topics.R
import uk.gov.govuk.topics.ui.model.TopicItemUi

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
    topics: List<TopicItemUi>?,
    onPageView: (String) -> Unit,
    onBack: () -> Unit,
    onTopicSelectedChanged: (String, String, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val title = stringResource(R.string.edit_title)

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
        ) {
            item {
                Column{
                    MediumVerticalSpacer()
                    BodyRegularLabel(stringResource(R.string.edit_message))
                    MediumVerticalSpacer()
                }
            }

            if (!topics.isNullOrEmpty()) {
                itemsIndexed(topics) { index, topic ->
                    ToggleListItemLegacy(
                        title = topic.title,
                        checked = topic.isSelected,
                        onCheckedChange = { checked ->
                            onTopicSelectedChanged(
                                topic.ref,
                                topic.title,
                                checked
                            )
                        },
                        isFirst = index == 0,
                        isLast = index == topics.lastIndex
                    )
                }

                item {
                    ExtraLargeVerticalSpacer()
                }
            }
        }
    }
}
